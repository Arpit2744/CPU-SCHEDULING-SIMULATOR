import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class CPUSimulatorGUI extends JFrame {

    // --- GUI Components (Unchanged) ---
    private JTextArea inputTextArea;
    private JTextField quantumField;
    private JButton runButton;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JLabel fcfsAvgLabel;
    private JLabel sjfAvgLabel;
    private JLabel rrAvgLabel;
    
    private GanttChartPanel fcfsGanttPanel;
    private GanttChartPanel sjfGanttPanel;
    private GanttChartPanel rrGanttPanel;

    /**
     * Constructor: Sets up the entire GUI. (Unchanged)
     */
    public CPUSimulatorGUI() {
        // --- Basic Frame Setup ---
        setTitle("CPU Scheduling Simulator (OOP Version)");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- 1. Input Panel (Top) ---
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout()); 
        inputPanel.setBorder(BorderFactory.createTitledBorder("Input"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST; 
        inputPanel.add(new JLabel("Enter Processes (PID AT BT):"), gbc);
        inputTextArea = new JTextArea(5, 20);
        inputTextArea.setText("1 0 5\n2 1 3\n3 2 8\n4 3 6"); 
        JScrollPane scrollPane = new JScrollPane(inputTextArea);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0; 
        gbc.weighty = 1.0; 
        gbc.fill = GridBagConstraints.BOTH; 
        inputPanel.add(scrollPane, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0; 
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST; 
        inputPanel.add(new JLabel("Round Robin Quantum:"), gbc);
        quantumField = new JTextField("2", 5); 
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST; 
        inputPanel.add(quantumField, gbc);
        runButton = new JButton("Run Simulation");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        inputPanel.add(runButton, gbc);
        add(inputPanel, BorderLayout.NORTH); 

        // --- 2. Results Panel (Center) ---
        JPanel mainCenterPanel = new JPanel();
        mainCenterPanel.setLayout(new BoxLayout(mainCenterPanel, BoxLayout.Y_AXIS));

        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Results Comparison"));
        String[] columnNames = {
                "PID", "AT", "BT",
                "FCFS WT", "FCFS TAT",
                "SJF WT", "SJF TAT",
                "RR WT", "RR TAT"
        };
        tableModel = new DefaultTableModel(columnNames, 0); 
        resultsTable = new JTable(tableModel);
        resultsTable.setEnabled(false); 
        JScrollPane tableScrollPane = new JScrollPane(resultsTable);
        resultsPanel.add(tableScrollPane, BorderLayout.CENTER);
        resultsPanel.setPreferredSize(new Dimension(0, 200));
        
        JPanel ganttContainerPanel = new JPanel();
        ganttContainerPanel.setLayout(new GridLayout(3, 1, 5, 5)); // 3 rows, 1 col
        ganttContainerPanel.setBorder(BorderFactory.createTitledBorder("Gantt Charts"));

        // 'GanttChartPanel' class is now in this same file (see below)
        fcfsGanttPanel = new GanttChartPanel("FCFS");
        sjfGanttPanel = new GanttChartPanel("SJF");
        rrGanttPanel = new GanttChartPanel("Round Robin");

        ganttContainerPanel.add(fcfsGanttPanel);
        ganttContainerPanel.add(sjfGanttPanel);
        ganttContainerPanel.add(rrGanttPanel);
        
        mainCenterPanel.add(resultsPanel);
        mainCenterPanel.add(ganttContainerPanel);
        add(mainCenterPanel, BorderLayout.CENTER); 

        // --- 3. Summary Panel (Bottom) ---
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new GridLayout(1, 3, 10, 0)); 
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Average Times"));
        fcfsAvgLabel = new JLabel("FCFS: Avg WT=0.0, Avg TAT=0.0", SwingConstants.CENTER);
        sjfAvgLabel = new JLabel("SJF: Avg WT=0.0, Avg TAT=0.0", SwingConstants.CENTER);
        rrAvgLabel = new JLabel("RR: Avg WT=0.0, Avg TAT=0.0", SwingConstants.CENTER);
        summaryPanel.add(fcfsAvgLabel);
        summaryPanel.add(sjfAvgLabel);
        summaryPanel.add(rrAvgLabel);
        add(summaryPanel, BorderLayout.SOUTH); 

        // --- 4. Button Action Listener ---
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // This is the only method we really changed
                runSimulation();
            }
        });
    }

    private void runSimulation() {
        String inputText = inputTextArea.getText().trim();
        String[] lines = inputText.split("\n");
        List<Process> originalProcesses = new ArrayList<>();
        
        try {
            // 1. Parse Input (Unchanged)
            for (String line : lines) {
                if (line.isEmpty()) continue;
                String[] parts = line.trim().split("\\s+"); 
                if (parts.length != 3) {
                    throw new Exception("Invalid line: " + line);
                }
                int pid = Integer.parseInt(parts[0]);
                int at = Integer.parseInt(parts[1]);
                int bt = Integer.parseInt(parts[2]);
                originalProcesses.add(new Process(pid, at, bt));
            }
            int timeQuantum = Integer.parseInt(quantumField.getText());
            if (timeQuantum <= 0) {
                 throw new Exception("Time Quantum must be > 0");
            }
            
            // 2. Run Simulation
            // Step 1: Create the object, giving it the data.
            Scheduler scheduler = new Scheduler(originalProcesses);
            
            // Step 2: Call methods on that object.
            // The object already "knows" what the processes are.
            SimulationResult fcfsResult = scheduler.runFCFS();
            SimulationResult sjfResult = scheduler.runSJF();
            SimulationResult rrResult = scheduler.runRR(timeQuantum);
            
            // 3. Get results from the wrapper objects
            List<Process> fcfsResults = fcfsResult.processes;
            List<Process> sjfResults = sjfResult.processes;
            List<Process> rrResults = rrResult.processes;

            // 4. Populate Table (Unchanged logic)
            tableModel.setRowCount(0); 
            double totalFcfsWT = 0, totalFcfsTAT = 0;
            double totalSjfWT = 0, totalSjfTAT = 0;
            double totalRrWT = 0, totalRrTAT = 0;
            
            int n = originalProcesses.size();
            for (int i = 0; i < n; i++) {
                Process p_fcfs = fcfsResults.get(i);
                Process p_sjf = sjfResults.get(i);
                Process p_rr = rrResults.get(i);
                
                Object[] rowData = {
                    p_fcfs.pid, p_fcfs.arrivalTime, p_fcfs.burstTime,
                    p_fcfs.waitingTime, p_fcfs.turnaroundTime,
                    p_sjf.waitingTime, p_sjf.turnaroundTime,
                    p_rr.waitingTime, p_rr.turnaroundTime
                };
                tableModel.addRow(rowData);
                
                totalFcfsWT += p_fcfs.waitingTime;
                totalFcfsTAT += p_fcfs.turnaroundTime;
                totalSjfWT += p_sjf.waitingTime;
                totalSjfTAT += p_sjf.turnaroundTime;
                totalRrWT += p_rr.waitingTime;
                totalRrTAT += p_rr.turnaroundTime;
            }
            
            // 5. Update Summary Labels (Unchanged)
            fcfsAvgLabel.setText(String.format("FCFS: Avg WT=%.2f, Avg TAT=%.2f", totalFcfsWT / n, totalFcfsTAT / n));
            sjfAvgLabel.setText(String.format("SJF: Avg WT=%.2f, Avg TAT=%.2f", totalSjfWT / n, totalSjfTAT / n));
            rrAvgLabel.setText(String.format("RR: Avg WT=%.2f, Avg TAT=%.2f", totalRrWT / n, totalRrTAT / n));

            // 6. Update Gantt Charts (Unchanged)
            fcfsGanttPanel.setData(fcfsResult.ganttChart);
            sjfGanttPanel.setData(sjfResult.ganttChart);
            rrGanttPanel.setData(rrResult.ganttChart);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error parsing input: " + ex.getMessage() + "\nPlease use format: PID AT BT (e.g., 1 0 5)",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            fcfsGanttPanel.setData(null);
            sjfGanttPanel.setData(null);
            rrGanttPanel.setData(null);
        }
    }
    /**
     * Main method - the entry point of the application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CPUSimulatorGUI().setVisible(true);
            }
        });
    }
}
class GanttChartPanel extends JPanel {

    private String title;
    private List<GanttChartBlock> chart;
    private Map<String, Color> colorMap;

    /**
     * Constructor for the Gantt Chart panel.
     */
    public GanttChartPanel(String title) {
        this.title = title;
        this.chart = null;
        this.colorMap = new HashMap<>();
        
        setPreferredSize(new Dimension(0, 60));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    /**
     * This method gives the panel new data and tells it to repaint.
     */
    public void setData(List<GanttChartBlock> chart) {
        this.chart = chart;
        
        // Regenerate the color map for the new PIDs
        colorMap.clear();
        if (chart != null) {
            int colorIndex = 0;
            Color[] colors = {
                new Color(173, 216, 230), // Light Blue
                new Color(144, 238, 144), // Light Green
                new Color(255, 182, 193), // Light Pink
                new Color(255, 255, 224), // Light Yellow
                new Color(221, 160, 221), // Plum
                new Color(250, 128, 114)  // Salmon
            };
            
            for (GanttChartBlock block : chart) {
                if (!block.pid.equals("IDLE") && !colorMap.containsKey(block.pid)) {
                    colorMap.put(block.pid, colors[colorIndex % colors.length]);
                    colorIndex++;
                }
            }
        }
        
        // Tell Swing to re-draw this component
        repaint();
    }

    /**
     * This is the main drawing method.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Erase the background
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the title
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString(title, 5, 20);

        if (chart == null || chart.isEmpty()) {
            return; // Nothing to draw
        }

        // Get total chart time and panel dimensions
        int totalTime = chart.get(chart.size() - 1).endTime;
        if (totalTime == 0) return; // Avoid division by zero
        
        int panelWidth = getWidth() - 20; // 10px padding on each side
        int barY = 30; // Y-position of the bar
        int barHeight = 25; // Height of the bar

        // Draw the Gantt chart blocks
        for (GanttChartBlock block : chart) {
            // Calculate pixel position and width
            int x = 10 + (int) ((double) block.startTime / totalTime * panelWidth);
            int width = (int) ((double) (block.endTime - block.startTime) / totalTime * panelWidth);

            // Set the color
            if (block.pid.equals("IDLE")) {
                g2d.setColor(Color.LIGHT_GRAY);
            } else {
                g2d.setColor(colorMap.getOrDefault(block.pid, Color.CYAN));
            }
            
            // Draw the rectangle
            g2d.fillRect(x, barY, width, barHeight);
            
            // Draw the border
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, barY, width, barHeight);

            // Draw the PID label
            String pidLabel = block.pid;
            FontMetrics metrics = g2d.getFontMetrics();
            int labelWidth = metrics.stringWidth(pidLabel);
            if (labelWidth < width - 4) { // Only draw if it fits
                g2d.drawString(pidLabel, x + (width - labelWidth) / 2, barY + barHeight / 2 + 5);
            }
            
            // Draw the start time label
            g2d.drawString(String.valueOf(block.startTime), x, barY + barHeight + 15);
        }
        
        // Draw the final time label
        g2d.drawString(String.valueOf(totalTime), 10 + panelWidth, barY + barHeight + 15);
    }
}