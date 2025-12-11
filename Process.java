public class Process {
    // --- Input Properties ---
    int pid; // Process ID
    int arrivalTime; // Time when the process enters the ready queue
    int burstTime; // Total CPU time required by the process

    // --- State Properties (for simulation) ---
    int remainingBurstTime; // CPU time left to execute (especially for Round Robin)
    boolean isCompleted; // Flag to check if the process is finished (especially for SJF)

    // --- Output Properties (Results) ---
    int completionTime; // Time when the process finishes execution
    int turnaroundTime; // Total time from arrival to completion (CT - AT)
    int waitingTime; // Time spent waiting in the ready queue (TAT - BT)

    /**
     * Standard constructor to create a new process from user input.
     */
    public Process(int pid, int arrivalTime, int burstTime) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        
        // Initialize state properties
        this.remainingBurstTime = this.burstTime; // Starts with the full burst time
        this.isCompleted = false; // Not completed yet
    }

    /**
     * Copy Constructor.
     * This is still very important. We use it to give the
     * Scheduler object its own safe copy of the processes.
     */
    public Process(Process other) {
        this.pid = other.pid;
        this.arrivalTime = other.arrivalTime;
        this.burstTime = other.burstTime;
        
        // Also copy the initial state
        this.remainingBurstTime = other.burstTime;
        this.isCompleted = false;
        
        // We don't copy output properties (CT, TAT, WT)
        // because each algorithm will calculate its own.
    }
}