CPU Scheduling Simulator

A Java-based Graphical User Interface (GUI) application designed to simulate, visualize, and compare various CPU scheduling algorithms. This project helps in understanding Operating System concepts by calculating metrics like Waiting Time and Turnaround Time and visualizing execution via Gantt Charts.


🚀 Features

Single-File Simplicity: All logic (GUI, Scheduler, Process) is combined into one file for easy execution.

Algorithms Implemented:

First Come First Serve (FCFS)

Shortest Job First (SJF) (Non-Preemptive)

Round Robin (RR) (with adjustable Time Quantum)

Visual Gantt Charts: Color-coded timelines show exactly when processes run and when the CPU is IDLE.

Comparison Table: Side-by-side comparison of Waiting Time and Turnaround Time for all algorithms.

Performance Metrics: Automatically calculates Average Waiting Time and Average Turnaround Time.

🛠️ Prerequisites

Java Development Kit (JDK): Version 8 or higher installed on your system.

⚙️ How to Run

Since the project is contained in a single file, compilation is straightforward.

Save the File:
Ensure the code is saved as CPUSimulatorGUI.java.

Compile:
Open your terminal or command prompt in the folder containing the file and run:

javac CPUSimulatorGUI.java


Run:
Execute the program:

java CPUSimulatorGUI


🖥️ Usage Instructions

Input Processes:
In the text area, enter your processes one per line in the format:
[PID] [Arrival Time] [Burst Time]

Example:

1 0 5
2 1 3
3 2 8
4 3 6


Set Time Quantum:
Enter an integer value for the Round Robin Quantum (e.g., 2).

Simulate:
Click the "Run Simulation" button.

Analyze Results:

Table: View individual process statistics.

Charts: Observe the graphical execution order.

Footer: Check the average performance metrics to see which algorithm performed best.

🧩 Algorithms Explained

FCFS: Processes execute in the order they arrive. Simple but can lead to the "convoy effect" (long wait times for short processes).

SJF (Non-Preemptive): The process with the shortest burst time is selected next. This minimizes average waiting time but requires knowing the burst time in advance.

Round Robin: The CPU is assigned to each process for a fixed time slice (Quantum). If the process isn't finished, it goes to the back of the queue. Ideal for time-sharing systems.