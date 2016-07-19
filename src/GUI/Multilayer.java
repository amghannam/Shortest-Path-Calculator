/**
 **************************************************************
 * File: Multilayer.java
 * Author: Ahmed Ghannam (0910337)
 * 
 * Implements the interface for the multilayer side. 
 */
package GUI;

import Content.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

/**
 *
 * @author Ahmed
 */
public class Multilayer extends javax.swing.JFrame {
    
    private int numberOfLinks = 0;
    private Node[] nodes;
    private Graph<Node> mGraph;
    private Dijkstra run = new Dijkstra();
    private ButtonGroup myButtonGroup;
    private DecimalFormat decimalFormat;
    private final double NANOSECONDS_TO_MILLISECONDS = 1.0 / 1000000.0;
    private final String DEFAULT_TEXT = "[Output of Dijkstra's algorithm is shown here]";

    /**
     * Creates new form Multilayer
     */
    public Multilayer() {
        initComponents();
        addMenuBar();
        groupRadioButtons();
        setMaximumRowCount();
        disableButtons();
        setTitle("Multilayer Networks");
        setLocationRelativeTo(null);
    }
    
    private void addMenuBar() {
        // create and initialize menu bar items 
        JMenuItem generateGraphItem = new JMenuItem("New Graph");
        JMenuItem aboutItem = new JMenuItem("About");
        JMenuItem exitItem = new JMenuItem("Exit");
        // add the items under their respective menus
        fileMenu.add(generateGraphItem);
        fileMenu.add(exitItem);
        helpMenu.add(aboutItem);
        // add an appropriate functionality to each item
        generateGraphItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new Generator().setVisible(true);
            }
        });
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                exit();
            }
        });
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new About().setVisible(true);
            }
        });
    }
    
    private void setMaximumRowCount() {
        cbxSourceList.setMaximumRowCount(8);
        cbxDestinationList.setMaximumRowCount(8);
    }
    
    private void groupRadioButtons() {
        myButtonGroup = new ButtonGroup();
        
        myButtonGroup.add(rdbBinaryHeap);
        myButtonGroup.add(rdbFibonacciHeap);
    }
    
    private void disableButtons() {
        if (btnComputePath.isEnabled()) {
            btnComputePath.setEnabled(false);
        }
        if (btnSaveToFile.isEnabled()) {
            btnSaveToFile.setEnabled(false);
        }
    }
    
    private void undoSelections() {
        if (rdbBinaryHeap.isSelected() || rdbFibonacciHeap.isSelected()) {
            myButtonGroup.clearSelection();
        }
        if (chxShowTimes.isSelected()) {
            chxShowTimes.setSelected(false);
        }
    }
    
    private void exit() {
        while (true) {
            int selection = JOptionPane.showConfirmDialog(null, "This will terminate "
                    + "the current session. All unsaved progress will be lost. " 
                    + "Are you sure you want to quit?", "Quit?", JOptionPane.YES_NO_OPTION);
            if (selection == JOptionPane.NO_OPTION) {
                break;
            } else {
                System.exit(0);
            }
        }
    }
    
    private Object makeObj(final String item) {
        return new Object() {
            public String toString() {
                return item;
            }
        };
    }
    
    private Node[] readFile(String fileName) {
        Map<String, Node> nodeMap = new HashMap<>();
        mGraph = new Graph<>();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(fileName));
            
            String line;
            boolean inNode = true;
            
            while ((line = in.readLine()) != null) {
                if (line.charAt(0) == '#') {
                    inNode = false;
                    continue;
                }
                if (inNode) {
                    //store the nodes
                    int indexOfSpace = line.indexOf(' ');
                    String nodeId = line.substring(0, indexOfSpace);
                    String nodeName = line.substring(indexOfSpace + 1);
                    Node v = new Node(nodeId, nodeName);
                    nodeMap.put(nodeId, v);
                    mGraph.addNode(v);
                } else {
                    //store the edges
                    String[] parts = line.split(" ");
                    String vFrom = parts[0];
                    String vTo = parts[1];
                    double weight = Double.parseDouble(parts[2]);
                    //if a negative edge is found, don't add it
                    if (weight < 0) {
                        continue;
                    }
                    Node v = nodeMap.get(vFrom);
                    if (v != null) {
                        v.addEdge(new Link(nodeMap.get(vTo), weight));
                        mGraph.addEdge(nodeMap.get(vFrom), nodeMap.get(vTo), weight);
                        numberOfLinks++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Unknown error occurred!", "Message",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
        Collection<Node> nodes = nodeMap.values();
        Node[] nodeList = (Node[]) (nodes.toArray(new Node[nodes.size()]));
        
        return nodeList;
    }
    
    private void displayRoutingTable(JRadioButton selectedStructure, Node source, Node dest) {
        String table = "";
        decimalFormat = new DecimalFormat("0.00");
        if (selectedStructure.getText().contains("binary heap")) {
                    List<Node> path = run.getShortestPathTo(dest);
                    table = "\n\n- Optimal multilayer path from " + source.toString() 
                            + " to " + dest.toString() + ":" + "\n- Path: " + path 
                            + "\n- Cost: " + decimalFormat.format(dest.minDistance);
                    updateTextArea(table);
        } else if (selectedStructure.getText().contains("Fibonacci heap")) {
            table = "\n\n- Cost of optimal multilayer path from " + source.toString()
                    + " to " + dest.toString() + ": " + run.dijkstraFibonacciHeapMultilayer(mGraph, source, dest);
            updateTextArea(table);
        }
    }
    
    private long getExecutionTime(JRadioButton selectedStructure, Node source, Node dest) {
        long startTime = 0;
        if (selectedStructure.getText().contains("binary heap")) {
            if (cbxDestinationList.getSelectedItem().toString().equals("All")) {
                startTime = System.nanoTime();
                for (int i = 0; i < nodes.length; i++) {
                    dest = nodes[i];
                    run.dijkstraMultilayer(nodes, source, dest);
                }
            } else {
                startTime = System.nanoTime();
                run.dijkstraMultilayer(nodes, source, dest);
            }
        } else if (selectedStructure.getText().contains("Fibonacci heap")) {
            if (cbxDestinationList.getSelectedItem().toString().equals("All")) {
                startTime = System.nanoTime();
                for (int i = 0; i < nodes.length; i++) {
                    dest = nodes[i];
                    run.dijkstraFibonacciHeapMultilayer(mGraph, source, dest);
                }
            } else {
                startTime = System.nanoTime();
                run.dijkstraFibonacciHeapMultilayer(mGraph, source, dest);
            }
        }
        long duration = System.nanoTime() - startTime;
        return duration;
    }
    
    private double toMillis(long nanoTime) {
        return nanoTime * NANOSECONDS_TO_MILLISECONDS;
    }
    
    private void displayExecutionTime(JRadioButton selectedStructure, Node source, Node dest) {
        String str = "";
        long nanoTime = getExecutionTime(selectedStructure, source, dest);
        decimalFormat = new DecimalFormat("0.00");
        String newLine = "\n\n- Running Dijkstra's algorithm with " + nodes.length + " nodes"
                + " and " + numberOfLinks + " links..."
                + "\n- Source node: " + cbxSourceList.getSelectedItem().toString()
                + "\n- Destination node: " + cbxDestinationList.getSelectedItem().toString();
        updateTextArea(newLine);
        if (selectedStructure.getText().contains("binary heap")) {
            str = "\n- Time using binary heap = " + decimalFormat.format(toMillis(nanoTime)) + " ms";
        } else if (selectedStructure.getText().contains("Fibonacci heap")) {
            str = "\n- Time using Fibonacci heap = " + decimalFormat.format(toMillis(nanoTime)) + " ms";
        }
        updateTextArea(str);
    }
    
    private void updateTextArea(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                txtResult.append(text);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        pnlFile = new javax.swing.JPanel();
        lblSelectFile = new javax.swing.JLabel();
        txtFile = new javax.swing.JTextField();
        btnBrowse = new javax.swing.JButton();
        lblFile = new javax.swing.JLabel();
        lblFileName = new javax.swing.JLabel();
        lblNodes = new javax.swing.JLabel();
        lblNumberOfNodes = new javax.swing.JLabel();
        lblLinks = new javax.swing.JLabel();
        lblNumberOfLinks = new javax.swing.JLabel();
        btnUpload = new javax.swing.JButton();
        btnClearFile = new javax.swing.JButton();
        pnlAlgorithm = new javax.swing.JPanel();
        lblSelectSource = new javax.swing.JLabel();
        cbxSourceList = new javax.swing.JComboBox();
        lblSelectHeap = new javax.swing.JLabel();
        rdbBinaryHeap = new javax.swing.JRadioButton();
        rdbFibonacciHeap = new javax.swing.JRadioButton();
        chxShowTimes = new javax.swing.JCheckBox();
        btnComputePath = new javax.swing.JButton();
        lblSelectDestination = new javax.swing.JLabel();
        cbxDestinationList = new javax.swing.JComboBox();
        pnlOutput = new javax.swing.JPanel();
        myScrollPane = new javax.swing.JScrollPane();
        txtResult = new javax.swing.JTextArea();
        btnResetOutput = new javax.swing.JButton();
        btnSaveToFile = new javax.swing.JButton();
        btnResetAll = new javax.swing.JButton();
        btnToMainMenu = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        helpMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);

        pnlFile.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "1. Topology", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, null, new java.awt.Color(0, 51, 204)));

        lblSelectFile.setText("- Browse and select a network topology definition file to upload:");

        txtFile.setEditable(false);

        btnBrowse.setText("Browse...");
        btnBrowse.setToolTipText("Browse and select a topology definition file to begin.");
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        lblFile.setText("- File name:");

        lblFileName.setText("[Unspecified]");
        lblFileName.setToolTipText("Name of the selected topology definition file. ");

        lblNodes.setText("- Total number of nodes:");

        lblNumberOfNodes.setText("[Unspecified]");
        lblNumberOfNodes.setToolTipText("Number of nodes across all layers. ");

        lblLinks.setText("- Total number of links:");

        lblNumberOfLinks.setText("[Unspecified]");
        lblNumberOfLinks.setToolTipText("Number of links across all layers. ");

        btnUpload.setText("Upload");
        btnUpload.setToolTipText("Upload to construct the selected topology definition.");
        btnUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadActionPerformed(evt);
            }
        });

        btnClearFile.setText("Clear");
        btnClearFile.setToolTipText("Clear the selected file's information.");
        btnClearFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFileLayout = new javax.swing.GroupLayout(pnlFile);
        pnlFile.setLayout(pnlFileLayout);
        pnlFileLayout.setHorizontalGroup(
            pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFileLayout.createSequentialGroup()
                .addGroup(pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFile)
                    .addComponent(lblNodes)
                    .addComponent(lblLinks))
                .addGap(18, 18, 18)
                .addGroup(pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNumberOfNodes)
                    .addComponent(lblNumberOfLinks)
                    .addComponent(lblFileName))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(pnlFileLayout.createSequentialGroup()
                .addGroup(pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFileLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnUpload)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClearFile))
                    .addGroup(pnlFileLayout.createSequentialGroup()
                        .addGroup(pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSelectFile)
                            .addGroup(pnlFileLayout.createSequentialGroup()
                                .addComponent(txtFile, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnBrowse)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlFileLayout.setVerticalGroup(
            pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFileLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSelectFile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBrowse))
                .addGap(18, 18, 18)
                .addGroup(pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFile)
                    .addComponent(lblFileName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNodes)
                    .addComponent(lblNumberOfNodes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLinks)
                    .addComponent(lblNumberOfLinks))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnClearFile)
                    .addComponent(btnUpload)))
        );

        pnlAlgorithm.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "2. Dijkstra's Algorithm", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, null, new java.awt.Color(0, 51, 204)));

        lblSelectSource.setText("- Select a source node:");

        cbxSourceList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Upload to select" }));
        cbxSourceList.setToolTipText("An upper-layer start node from which to find the shortest path.");

        lblSelectHeap.setText("- Select a data structure under which to run the algorithm:");

        rdbBinaryHeap.setText("Java-based binary heap");
        rdbBinaryHeap.setToolTipText("Shows costs and paths. ");

        rdbFibonacciHeap.setText("High-performance Fibonacci heap data structure");
        rdbFibonacciHeap.setToolTipText("Shows costs only. ");

        chxShowTimes.setText("Only show execution time.");
        chxShowTimes.setToolTipText("Check for performance comparisons. (Routing tables will be omitted.)");

        btnComputePath.setText("Compute Path");
        btnComputePath.setToolTipText("Compute the shortest path for the current selection.");
        btnComputePath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnComputePathActionPerformed(evt);
            }
        });

        lblSelectDestination.setText("- Select a destination node:");

        cbxDestinationList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Upload to select" }));
        cbxDestinationList.setToolTipText("An upper-layer target node to which the shortest path is calculated.");

        javax.swing.GroupLayout pnlAlgorithmLayout = new javax.swing.GroupLayout(pnlAlgorithm);
        pnlAlgorithm.setLayout(pnlAlgorithmLayout);
        pnlAlgorithmLayout.setHorizontalGroup(
            pnlAlgorithmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAlgorithmLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(pnlAlgorithmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdbFibonacciHeap)
                    .addComponent(rdbBinaryHeap))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(pnlAlgorithmLayout.createSequentialGroup()
                .addGroup(pnlAlgorithmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlAlgorithmLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chxShowTimes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnComputePath))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAlgorithmLayout.createSequentialGroup()
                        .addGroup(pnlAlgorithmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSelectSource)
                            .addComponent(lblSelectHeap)
                            .addComponent(lblSelectDestination))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pnlAlgorithmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cbxSourceList, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cbxDestinationList, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        pnlAlgorithmLayout.setVerticalGroup(
            pnlAlgorithmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAlgorithmLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAlgorithmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSelectSource)
                    .addComponent(cbxSourceList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlAlgorithmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSelectDestination)
                    .addComponent(cbxDestinationList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSelectHeap)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdbBinaryHeap)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlAlgorithmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlAlgorithmLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(pnlAlgorithmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnComputePath)
                            .addComponent(chxShowTimes)))
                    .addGroup(pnlAlgorithmLayout.createSequentialGroup()
                        .addComponent(rdbFibonacciHeap)
                        .addContainerGap(33, Short.MAX_VALUE))))
        );

        pnlOutput.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "3. Output", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, null, new java.awt.Color(0, 51, 204)));

        txtResult.setEditable(false);
        txtResult.setColumns(20);
        txtResult.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        txtResult.setRows(5);
        txtResult.setText("[Output of Dijkstra's algorithm is shown here]");
        myScrollPane.setViewportView(txtResult);

        btnResetOutput.setText("Reset");
        btnResetOutput.setToolTipText("Clear the current result. ");
        btnResetOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetOutputActionPerformed(evt);
            }
        });

        btnSaveToFile.setText("Save to File");
        btnSaveToFile.setToolTipText("Save the current result to an external file. ");
        btnSaveToFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveToFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlOutputLayout = new javax.swing.GroupLayout(pnlOutput);
        pnlOutput.setLayout(pnlOutputLayout);
        pnlOutputLayout.setHorizontalGroup(
            pnlOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOutputLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(myScrollPane)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlOutputLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSaveToFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnResetOutput)))
                .addContainerGap())
        );
        pnlOutputLayout.setVerticalGroup(
            pnlOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOutputLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(myScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSaveToFile)
                    .addComponent(btnResetOutput)))
        );

        btnResetAll.setText("Reset All");
        btnResetAll.setToolTipText("Clear the entire form. ");
        btnResetAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetAllActionPerformed(evt);
            }
        });

        btnToMainMenu.setText("Back to Main Menu");
        btnToMainMenu.setToolTipText("Return to the main menu. ");
        btnToMainMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToMainMenuActionPerformed(evt);
            }
        });

        fileMenu.setText("File");
        menuBar.add(fileMenu);

        helpMenu.setText("Help");
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlAlgorithm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlOutput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnToMainMenu)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnResetAll)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlAlgorithm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlOutput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnToMainMenu)
                    .addComponent(btnResetAll))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>                        

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {                                          
        JFileChooser myFileChooser = new JFileChooser();
        int rVal = myFileChooser.showOpenDialog(Multilayer.this);
        if (rVal == JFileChooser.APPROVE_OPTION) {
            txtFile.setText(myFileChooser.getSelectedFile().getAbsolutePath());
        }
    }                                         
    
    private void btnUploadActionPerformed(java.awt.event.ActionEvent evt) {                                          
        try {
            if (txtFile.getText().isEmpty()) { // verify if a file has been specified
                JOptionPane.showMessageDialog(null, "No file selected. You must select a file before you can upload.", 
                        "Message", JOptionPane.WARNING_MESSAGE);
            } else if (!txtFile.getText().endsWith(".txt")) { // check whether the specified file's extension is valid
                JOptionPane.showMessageDialog(null, "Invalid file extension! Please ensure "
                        + "that your file's name ends with .txt. (Example: MyTopology.txt)", "Message",
                        JOptionPane.ERROR_MESSAGE);
            } else if (txtFile.getText().endsWith(lblFileName.getText())) {
                JOptionPane.showMessageDialog(null, "You have already uploaded this file.", "Message",
                        JOptionPane.WARNING_MESSAGE);
            } else { // all is okay; proceed normally
                // reset the number of links
                numberOfLinks = 0;
                // store the nodes in an array
                nodes = readFile(txtFile.getText());
                // display an information message 
                JOptionPane.showMessageDialog(null, "Upload successful! "
                        + "Please select your desired nodes and data structure to continue.");
                // extract the name of the file
                String fileName = new File(txtFile.getText()).getName();
                // update the file information labels with the appropriate values
                lblFileName.setText(fileName);
                lblNumberOfNodes.setText(String.valueOf(nodes.length));
                lblNumberOfLinks.setText(String.valueOf(numberOfLinks));
                // update and [re]populate the combo boxes to reflect the new changes
                cbxSourceList.removeAllItems();
                cbxDestinationList.removeAllItems();
                cbxSourceList.addItem(makeObj("Select a node"));
                cbxDestinationList.addItem(makeObj("Select a node"));
                for (int i = 0; i < nodes.length; i++) {
                    if (nodes[i].id.charAt(0) == 'u') {
                        cbxSourceList.addItem(nodes[i]);
                        cbxDestinationList.addItem(nodes[i]);
                    }
                }
                cbxDestinationList.addItem(makeObj("All")); // NEW ADDITION
                btnComputePath.setEnabled(true);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Invalid file format. Consider revising.",
                    "Message", JOptionPane.ERROR_MESSAGE);
        }
    }                                         
    
    private void btnClearFileActionPerformed(java.awt.event.ActionEvent evt) {                                             
        txtFile.setText("");
        lblFileName.setText("[Unspecified]");
        lblNumberOfNodes.setText("[Unspecified]");
        lblNumberOfLinks.setText("[Unspecified]");
        cbxSourceList.removeAllItems();
        cbxDestinationList.removeAllItems();
        cbxSourceList.addItem(makeObj("Upload to select"));
        cbxDestinationList.addItem(makeObj("Upload to select"));
        numberOfLinks = 0;
        btnComputePath.setEnabled(false);
    }                                            
    
    private void btnComputePathActionPerformed(java.awt.event.ActionEvent evt) {                                               
        Node source = null;
        Node dest = null;
        if (!txtFile.getText().endsWith(lblFileName.getText())) {
            JOptionPane.showMessageDialog(null, "You have not uploaded your newly selected file.",
                    "Message", JOptionPane.WARNING_MESSAGE);
        } else if (cbxSourceList.getSelectedItem().toString().equals("Select a node")) {
            JOptionPane.showMessageDialog(null, "No source node selected. Please select one to continue.",
                    "Message", JOptionPane.WARNING_MESSAGE);
        } else if (cbxDestinationList.getSelectedItem().toString().equals("Select a node")) {
            JOptionPane.showMessageDialog(null, "No destination node selected. Please select one to continue.",
                    "Message", JOptionPane.WARNING_MESSAGE);
        } else if (!rdbBinaryHeap.isSelected() && !rdbFibonacciHeap.isSelected()) {
            JOptionPane.showMessageDialog(null, "You must select a data structure to continue.",
                    "Message", JOptionPane.WARNING_MESSAGE);
        } else if (rdbBinaryHeap.isSelected()) {
            source = (Node) cbxSourceList.getSelectedItem();
            if (cbxDestinationList.getSelectedItem().toString().equals("All")) {
                if (!chxShowTimes.isSelected()) {
                    for (int i = 0; i < nodes.length; i++) {
                        dest = nodes[i];
                        run.dijkstraMultilayer(nodes, source, dest);
                        if (dest.id.charAt(0) == 'u') {
                            displayRoutingTable(rdbBinaryHeap, source, dest);
                        }
                    }
                } else {
                    displayExecutionTime(rdbBinaryHeap, source, dest);
                }
            } else {
                dest = (Node) cbxDestinationList.getSelectedItem();
                if (!chxShowTimes.isSelected()) {
                    run.dijkstraMultilayer(nodes, source, dest);
                    displayRoutingTable(rdbBinaryHeap, source, dest);
                } else {
                    displayExecutionTime(rdbBinaryHeap, source, dest);
                }
            }
            btnSaveToFile.setEnabled(true);
        } else if (rdbFibonacciHeap.isSelected()) {
            source = (Node) cbxSourceList.getSelectedItem();
            if (cbxDestinationList.getSelectedItem().toString().equals("All")) {
                if (!chxShowTimes.isSelected()) {
                    for (int i = 0; i < nodes.length; i++) {
                        dest = nodes[i];
                        if (dest.id.charAt(0) == 'u') {
                            displayRoutingTable(rdbFibonacciHeap, source, dest);
                        }
                    }
                } else {
                    displayExecutionTime(rdbFibonacciHeap, source, dest);
                }
            } else {
                dest = (Node) cbxDestinationList.getSelectedItem();
                if (!chxShowTimes.isSelected()) {
                    displayRoutingTable(rdbFibonacciHeap, source, dest);
                } else {
                    displayExecutionTime(rdbFibonacciHeap, source, dest);
                }
            }
            btnSaveToFile.setEnabled(true);
        }
    }                                              
    
    private void btnSaveToFileActionPerformed(java.awt.event.ActionEvent evt) {                                              
        BufferedWriter fileOut = null;
        while (true) {
            try {
                String fileName = JOptionPane.showInputDialog(null, "- Enter a name for your file:",
                        "Save to File", JOptionPane.QUESTION_MESSAGE);
                if (fileName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No file saved. You must name your file "
                            + "before you can save it.", "Message",
                            JOptionPane.WARNING_MESSAGE);
                    continue;
                }
                try {
                    fileOut = new BufferedWriter(new FileWriter(fileName));
                    txtResult.write(fileOut);
                    JOptionPane.showMessageDialog(null, "Your file has been saved successfully!");
                    break;
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Unexpected error occurred! "
                            + "Please enter a valid name.", "Message", JOptionPane.ERROR_MESSAGE);
                } finally {
                    try {
                        fileOut.close();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Unknown error occurred!", "Message",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                break;
            }
        }
    }                                             
    
    private void btnResetOutputActionPerformed(java.awt.event.ActionEvent evt) {                                               
        txtResult.setText(DEFAULT_TEXT);
        btnSaveToFile.setEnabled(false);
    }                                              
    
    private void btnToMainMenuActionPerformed(java.awt.event.ActionEvent evt) {                                              
        boolean continueLoop = true;
        while (continueLoop) {
            int selection = JOptionPane.showConfirmDialog(null, "You will lose any unsaved work. "
                    + "Return to the main menu?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);
            if (selection == JOptionPane.NO_OPTION) {
                continueLoop = false;
            } else {
                continueLoop = false;
                this.setVisible(false);
                this.dispose();
                new Start().setVisible(true);
            }
        }
    }                                             
    
    private void btnResetAllActionPerformed(java.awt.event.ActionEvent evt) {                                            
        btnClearFileActionPerformed(evt);
        undoSelections();
        txtResult.setText(DEFAULT_TEXT);
        btnSaveToFile.setEnabled(false);
    }                                           

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Multilayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Multilayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Multilayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Multilayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Multilayer().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify                     
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnClearFile;
    private javax.swing.JButton btnComputePath;
    private javax.swing.JButton btnResetAll;
    private javax.swing.JButton btnResetOutput;
    private javax.swing.JButton btnSaveToFile;
    private javax.swing.JButton btnToMainMenu;
    private javax.swing.JButton btnUpload;
    private javax.swing.JComboBox cbxDestinationList;
    private javax.swing.JComboBox cbxSourceList;
    private javax.swing.JCheckBox chxShowTimes;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JLabel lblFile;
    private javax.swing.JLabel lblFileName;
    private javax.swing.JLabel lblLinks;
    private javax.swing.JLabel lblNodes;
    private javax.swing.JLabel lblNumberOfLinks;
    private javax.swing.JLabel lblNumberOfNodes;
    private javax.swing.JLabel lblSelectDestination;
    private javax.swing.JLabel lblSelectFile;
    private javax.swing.JLabel lblSelectHeap;
    private javax.swing.JLabel lblSelectSource;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JScrollPane myScrollPane;
    private javax.swing.JPanel pnlAlgorithm;
    private javax.swing.JPanel pnlFile;
    private javax.swing.JPanel pnlOutput;
    private javax.swing.JRadioButton rdbBinaryHeap;
    private javax.swing.JRadioButton rdbFibonacciHeap;
    private javax.swing.JTextField txtFile;
    private javax.swing.JTextArea txtResult;
    // End of variables declaration                   
}
