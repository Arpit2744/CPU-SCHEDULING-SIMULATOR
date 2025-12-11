import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Scheduler {
    private List<Process> originalProcesses;
    public Scheduler(List<Process> processesToSchedule) {
        this.originalProcesses = new ArrayList<>();
        for (Process p : processesToSchedule) {
            this.originalProcesses.add(new Process(p));
        }
    }
    private List<Process> deepCopy() {
        List<Process> newList = new ArrayList<>();
        for (Process p : this.originalProcesses) {
            newList.add(new Process(p));
        }
        return newList;
    }
    private void calculateTimes(Process p) {
        p.turnaroundTime = p.completionTime - p.arrivalTime;
        p.waitingTime = p.turnaroundTime - p.burstTime;
    }
    public SimulationResult runFCFS() {
        List<Process> processes = deepCopy();
        List<GanttChartBlock> ganttChart = new ArrayList<>();
        // Sort by arrival time
        Collections.sort(processes, new Comparator<Process>() {
            @Override
            public int compare(Process p1, Process p2) {
                return Integer.compare(p1.arrivalTime, p2.arrivalTime);
            }
        });

        int currentTime = 0;
        for (Process p : processes) {
            if (currentTime < p.arrivalTime) {
                // Record IDLE time
                ganttChart.add(new GanttChartBlock("IDLE", currentTime, p.arrivalTime));
                currentTime = p.arrivalTime;
            }

            p.completionTime = currentTime + p.burstTime;
            // Record Process execution
            ganttChart.add(new GanttChartBlock("P" + p.pid, currentTime, p.completionTime));
            
            // Call our own helper method
            calculateTimes(p);
            currentTime = p.completionTime;
        }

        Collections.sort(processes, Comparator.comparingInt(p -> p.pid));
        // Return the new result object
        return new SimulationResult(processes, ganttChart);
    }

    // --- 2. Shortest Job First (SJF) - Non-Preemptive ---
 
    public SimulationResult runSJF() {
        // Get a fresh list of processes
        List<Process> processes = deepCopy();
        
        List<GanttChartBlock> ganttChart = new ArrayList<>();
        
        int n = processes.size();
        int completedCount = 0;
        int currentTime = 0;

        while (completedCount < n) {
            List<Process> readyQueue = new ArrayList<>();
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && !p.isCompleted) {
                    readyQueue.add(p);
                }
            }

            if (readyQueue.isEmpty()) {
                int nextArrivalTime = Integer.MAX_VALUE;
                for (Process p : processes) {
                    if (!p.isCompleted) {
                        nextArrivalTime = Math.min(nextArrivalTime, p.arrivalTime);
                    }
                }
                if (nextArrivalTime == Integer.MAX_VALUE) {
                     break; 
                }
                // Record IDLE time
                ganttChart.add(new GanttChartBlock("IDLE", currentTime, nextArrivalTime));
                currentTime = nextArrivalTime;
            } else {
                Process shortestJob = readyQueue.get(0);
                for (Process p : readyQueue) {
                    if (p.burstTime < shortestJob.burstTime) {
                        shortestJob = p;
                    }
                }

                shortestJob.completionTime = currentTime + shortestJob.burstTime;
                // Record Process execution
                ganttChart.add(new GanttChartBlock("P" + shortestJob.pid, currentTime, shortestJob.completionTime));
                
                calculateTimes(shortestJob);
                shortestJob.isCompleted = true;
                completedCount++;
                currentTime = shortestJob.completionTime;
            }
        }
        
        Collections.sort(processes, Comparator.comparingInt(p -> p.pid));
        return new SimulationResult(processes, ganttChart);
    }

    // --- 3. Round Robin (RR) ---
    public SimulationResult runRR(int timeQuantum) {
        // Get a fresh list of processes
        List<Process> processes = deepCopy();
        
        List<GanttChartBlock> ganttChart = new ArrayList<>();
        
        int n = processes.size();
        Queue<Process> readyQueue = new LinkedList<>();
        // We need a *separate* copy for sorting by arrival
        List<Process> sortedByArrival = deepCopy();
        sortedByArrival.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0;
        int completedCount = 0;
        int arrivalIndex = 0; 

        while (completedCount < n) {
            while (arrivalIndex < n && sortedByArrival.get(arrivalIndex).arrivalTime <= currentTime) {
                int pidToFind = sortedByArrival.get(arrivalIndex).pid;
                // Find the *actual* process object from our main 'processes' list
                for (Process p : processes) {
                    if (p.pid == pidToFind) {
                        readyQueue.add(p);
                        break;
                    }
                }
                arrivalIndex++;
            }

            if (readyQueue.isEmpty()) {
                if (arrivalIndex < n) {
                    // Record IDLE time
                    ganttChart.add(new GanttChartBlock("IDLE", currentTime, sortedByArrival.get(arrivalIndex).arrivalTime));
                    currentTime = sortedByArrival.get(arrivalIndex).arrivalTime;
                } else {
                    break;
                }
            } else {
                Process currentProcess = readyQueue.poll();

                if (currentProcess.remainingBurstTime > timeQuantum) {
                    // Record Process execution (partial)
                    ganttChart.add(new GanttChartBlock("P" + currentProcess.pid, currentTime, currentTime + timeQuantum));
                    
                    currentTime += timeQuantum;
                    currentProcess.remainingBurstTime -= timeQuantum;
                    
                    // Check for new arrivals *during* this quantum
                    while (arrivalIndex < n && sortedByArrival.get(arrivalIndex).arrivalTime <= currentTime) {
                         int pidToFind = sortedByArrival.get(arrivalIndex).pid;
                         for (Process p : processes) {
                             if (p.pid == pidToFind) {
                                readyQueue.add(p);
                                break;
                             }
                         }
                        arrivalIndex++;
                    }
                    readyQueue.add(currentProcess); // Add back to end of queue

                } else {
                    // Record Process execution (final)
                    ganttChart.add(new GanttChartBlock("P" + currentProcess.pid, currentTime, currentTime + currentProcess.remainingBurstTime));
                    
                    currentTime += currentProcess.remainingBurstTime;
                    currentProcess.remainingBurstTime = 0;
                    currentProcess.isCompleted = true;
                    completedCount++;
                    
                    currentProcess.completionTime = currentTime;
                    calculateTimes(currentProcess);
                    
                     // Check for new arrivals that finished *at* the same time
                     while (arrivalIndex < n && sortedByArrival.get(arrivalIndex).arrivalTime <= currentTime) {
                         int pidToFind = sortedByArrival.get(arrivalIndex).pid;
                         for (Process p : processes) {
                             if (p.pid == pidToFind) {
                                readyQueue.add(p);
                                break;
                             }
                         }
                        arrivalIndex++;
                    }
                }
            }
        }
        
        Collections.sort(processes, Comparator.comparingInt(p -> p.pid));
        return new SimulationResult(processes, ganttChart);
    }
}
class SimulationResult {
    
    public List<Process> processes;
    public List<GanttChartBlock> ganttChart;

    public SimulationResult(List<Process> processes, List<GanttChartBlock> ganttChart) {
        this.processes = processes;
        this.ganttChart = ganttChart;
    }
}
class GanttChartBlock {
    String pid;
    int startTime;
    int endTime;

    public GanttChartBlock(String pid, int startTime, int endTime) {
        this.pid = pid;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}