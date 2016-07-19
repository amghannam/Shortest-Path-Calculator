/**
 ****************************************************************
 * File: Singlelayer.java
 * Author: Ahmed Ghannam (0910337)
 * 
 * Implements the interface for the single-layer networks form. 
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
public class Singlelayer extends javax.swing.JFrame {

    private int numberOfLinks = 0;
    private Node[] nodes;
    private Graph<Node> mGraph;
    private Dijkstra run = new Dijkstra();
    private ButtonGroup myButtonGroup;
    private DecimalFormat decimalFormat;
    private final double NANOSECONDS_TO_MILLISECONDS = 1.0 / 1000000.0;
    private final String DEFAULT_TEXT = "[Output of Dijkstra's algorithm is shown here]";

    /**
     * Creates new form Singlelayer
     */
    public Singlelayer() {
        initComponents();
        addMenuBar();
        groupRadioButtons();
        cbxNodeList.setMaximumRowCount(8);
        disableButtons();
        setTitle("Single-layer Networks");
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
        });;
    }

    private void groupRadioButtons() {
        myButtonGroup = new ButtonGroup();

        myButtonGroup.add(rdbBinaryHeap);
        myButtonGroup.add(rdbFibonacciHeap);
    }

    private void disableButtons() {
        if (btnComputePaths.isEnabled()) {
            btnComputePaths.setEnabled(false);
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

    private void displayRoutingTable(JRadioButton selectedStructure, Node source) {
        String dataStructure = null;
        decimalFormat = new DecimalFormat("0.00");
        if (selectedStructure.getText().contains("binary heap")) {
            dataStructure = "binary heap";
        } else if (selectedStructure.getText().contains("Fibonacci heap")) {
            dataStructure = "Fibonacci heap";
        }
        String newLine = "\n\n- Routing table for " + source.toString() + " using a " + dataStructure + ":";
        updateTextArea(newLine);
        if (selectedStructure.getText().contains("binary heap")) {
            for (Node node : nodes) {
                List<Node> path = run.getShortestPathTo(node);
                String table = "\n- Optimal Path to " + node.toString() + ": "
                        + path + "   " + "Cost: " + decimalFormat.format(node.minDistance);
                updateTextArea(table);
            }
        } else if (selectedStructure.getText().contains("Fibonacci heap")) {
            String table = "\n" + run.computePathsFibonacciHeap(mGraph, source);
            updateTextArea(table);
        }
    }

    private long getExecutionTime(JRadioButton selectedStructure, Node source) {
        long startTime = 0;
        if (selectedStructure.getText().contains("binary heap")) {
            if (cbxNodeList.getSelectedItem().toString().equals("All")) {
                startTime = System.nanoTime();
                for (int i = 0; i < nodes.length; i++) {
                    source = nodes[i];
                    run.computePathsBinaryHeap(nodes, source);
                }
            } else {
                startTime = System.nanoTime();
                run.computePathsBinaryHeap(nodes, source);
            }
        } else if (selectedStructure.getText().contains("Fibonacci heap")) {
            if (cbxNodeList.getSelectedItem().toString().equals("All")) {
                startTime = System.nanoTime();
                for (int i = 0; i < nodes.length; i++) {
                    source = nodes[i];
                    run.computePathsFibonacciHeap(mGraph, source);
                }
            } else {
                startTime = System.nanoTime();
                run.computePathsFibonacciHeap(mGraph, source);
            }
        }
        long duration = System.nanoTime() - startTime;
        return duration;
    }

    private double toMillis(long nanoTime) {
        return nanoTime * NANOSECONDS_TO_MILLISECONDS;
    }

    private void displayExecutionTime(JRadioButton selectedStructure, Node source) {
        String str = null;
        long nanoTime = getExecutionTime(selectedStructure, source);
        decimalFormat = new DecimalFormat("0.00");
        String newLine = "\n\n- Running Dijkstra's algorithm with " + nodes.length + " nodes"
                + " and " + numberOfLinks + " links..."
                + "\n- Source node: " + cbxNodeList.getSelectedItem().toString().toUpperCase();
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

        pnlAlgorithm = new javax.swing.JPanel();
        lblSelectNode = new javax.swing.JLabel();
        cbxNodeList = new javax.swing.JComboBox();
        lblSelectHeap = new javax.swing.JLabel();
        rdbBinaryHeap = new javax.swing.JRadioButton();
        rdbFibonacciHeap = new javax.swing.JRadioButton();
        btnComputePaths = new javax.swing.JButton();
        chxShowTimes = new javax.swing.JCheckBox();
        pnlResult = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        txtResult = new javax.swing.JTextArea();
        btnSaveToFile = new javax.swing.JButton();
        btnResetOutput = new javax.swing.JButton();
        pnlFile = new javax.swing.JPanel();
        lblSelectFile = new javax.swing.JLabel();
        txtFile = new javax.swing.JTextField();
        btnBrowseFile = new javax.swing.JButton();
        btnUploadFile = new javax.swing.JButton();
        lblNodes = new javax.swing.JLabel();
        lblLinks = new javax.swing.JLabel();
        lblNumberOfNodes = new javax.swing.JLabel();
        lblNumberOfLinks = new javax.swing.JLabel();
        lblFile = new javax.swing.JLabel();
        lblFileName = new javax.swing.JLabel();
        btnClearFile = new javax.swing.JButton();
        btnGoBack = new javax.swing.JButton();
        btnResetAll = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        helpMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        pnlAlgorithm.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "2. Dijkstra's Algorithm", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, null, new java.awt.Color(0, 51, 204)));

        lblSelectNode.setText("- Select a source node:");

        cbxNodeList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Upload to select" }));
        cbxNodeList.setToolTipText("The node from which to find the shortest paths.");

        lblSelectHeap.setText("- Select a data structure under which to run Dijkstra's algorithm:");

        rdbBinaryHeap.setText("Java-based binary heap");
        rdbBinaryHeap.setToolTipText("Shows paths and costs. ");

        rdbFibonacciHeap.setText("High-performance Fibonacci heap data structure");
        rdbFibonacciHeap.setToolTipText("Shows costs only.");

        btnComputePaths.setText("Compute Paths");
        btnComputePaths.setToolTipText("Compute the shortest paths for the current selection.");
        btnComputePaths.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnComputePathsActionPerformed(evt);
            }
        });

        chxShowTimes.setText("Only show execution times.");
        chxShowTimes.setToolTipText("Check for performance comparisons. (Routing tables will be omitted.)");

        javax.swing.GroupLayout pnlAlgorithmLayout = new javax.swing.GroupLayout(pnlAlgorithm);
        pnlAlgorithm.setLayout(pnlAlgorithmLayout);
        pnlAlgorithmLayout.setHorizontalGroup(
            pnlAlgorithmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAlgorithmLayout.createSequentialGroup()
                .addGroup(pnlAlgorithmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlAlgorithmLayout.createSequentialGroup()
                        .addGroup(pnlAlgorithmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSelectHeap)
                            .addGroup(pnlAlgorithmLayout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addGroup(pnlAlgorithmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(rdbBinaryHeap)
                                    .addComponent(rdbFibonacciHeap))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlAlgorithmLayout.createSequentialGroup()
                        .addGroup(pnlAlgorithmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSelectNode)
                            .addComponent(chxShowTimes))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pnlAlgorithmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnComputePaths, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cbxNodeList, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(10, 10, 10))
        );
        pnlAlgorithmLayout.setVerticalGroup(
            pnlAlgorithmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAlgorithmLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAlgorithmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSelectNode)
                    .addComponent(cbxNodeList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblSelectHeap)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rdbBinaryHeap)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rdbFibonacciHeap)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addGroup(pnlAlgorithmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chxShowTimes)
                    .addComponent(btnComputePaths)))
        );

        pnlResult.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "3. Output", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, null, new java.awt.Color(0, 51, 204)));

        txtResult.setEditable(false);
        txtResult.setColumns(20);
        txtResult.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        txtResult.setRows(5);
        txtResult.setText("[Output of Dijkstra's algorithm is shown here]");
        scrollPane.setViewportView(txtResult);

        btnSaveToFile.setText("Save to File");
        btnSaveToFile.setToolTipText("Save the current result to an external file. ");
        btnSaveToFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveToFileActionPerformed(evt);
            }
        });

        btnResetOutput.setText("Reset");
        btnResetOutput.setToolTipText("Clear the current result. ");
        btnResetOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetOutputActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlResultLayout = new javax.swing.GroupLayout(pnlResult);
        pnlResult.setLayout(pnlResultLayout);
        pnlResultLayout.setHorizontalGroup(
            pnlResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlResultLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPane)
                    .addGroup(pnlResultLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSaveToFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnResetOutput)))
                .addContainerGap())
        );
        pnlResultLayout.setVerticalGroup(
            pnlResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlResultLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnResetOutput)
                    .addComponent(btnSaveToFile)))
        );

        pnlFile.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "1. Topology", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, null, new java.awt.Color(0, 51, 204)));

        lblSelectFile.setText("- Browse and select a network topology definition file to upload:");

        txtFile.setEditable(false);

        btnBrowseFile.setText("Browse...");
        btnBrowseFile.setToolTipText("Browse and select a topology definition file to begin.");
        btnBrowseFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseFileActionPerformed(evt);
            }
        });

        btnUploadFile.setText("Upload");
        btnUploadFile.setToolTipText("Upload to construct the selected topology definition.");
        btnUploadFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadFileActionPerformed(evt);
            }
        });

        lblNodes.setText("- Total number of nodes:");

        lblLinks.setText("- Total number of links: ");

        lblNumberOfNodes.setText("[Unspecified]");
        lblNumberOfNodes.setToolTipText("Number of nodes in this network. ");

        lblNumberOfLinks.setText("[Unspecified]");
        lblNumberOfLinks.setToolTipText("Number of links in this network. ");

        lblFile.setText("- File name:");

        lblFileName.setText("[Unspecified]");
        lblFileName.setToolTipText("Name of the selected topology definition file. ");

        btnClearFile.setText("Clear");
        btnClearFile.setToolTipText("Clear the selected file's information. ");
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
                .addContainerGap()
                .addGroup(pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFileLayout.createSequentialGroup()
                        .addGroup(pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlFileLayout.createSequentialGroup()
                                .addComponent(lblLinks)
                                .addGap(24, 24, 24)
                                .addComponent(lblNumberOfLinks)
                                .addGap(0, 24, Short.MAX_VALUE))
                            .addComponent(txtFile))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnBrowseFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnUploadFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClearFile)
                        .addGap(14, 14, 14))
                    .addGroup(pnlFileLayout.createSequentialGroup()
                        .addGroup(pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSelectFile)
                            .addGroup(pnlFileLayout.createSequentialGroup()
                                .addGroup(pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblNodes)
                                    .addComponent(lblFile))
                                .addGap(18, 18, 18)
                                .addGroup(pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblFileName)
                                    .addComponent(lblNumberOfNodes))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        pnlFileLayout.setVerticalGroup(
            pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFileLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSelectFile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBrowseFile))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addGroup(pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFileLayout.createSequentialGroup()
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
                        .addGap(20, 20, 20))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnUploadFile)
                        .addComponent(btnClearFile))))
        );

        btnGoBack.setText("Back to Main Menu");
        btnGoBack.setToolTipText("Return to the main menu. ");
        btnGoBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoBackActionPerformed(evt);
            }
        });

        btnResetAll.setText("Reset All");
        btnResetAll.setToolTipText("Clear the entire form. ");
        btnResetAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetAllActionPerformed(evt);
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
                    .addComponent(pnlAlgorithm, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlResult, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlFile, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnGoBack)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnResetAll)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlAlgorithm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlResult, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGoBack)
                    .addComponent(btnResetAll))
                .addGap(19, 19, 19))
        );

        pnlResult.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>                        

    private void btnBrowseFileActionPerformed(java.awt.event.ActionEvent evt) {                                              
        JFileChooser myFileChooser = new JFileChooser();
        int rVal = myFileChooser.showOpenDialog(Singlelayer.this);
        if (rVal == JFileChooser.APPROVE_OPTION) {
            txtFile.setText(myFileChooser.getSelectedFile().getAbsolutePath());
        }
    }                                             

    private void btnUploadFileActionPerformed(java.awt.event.ActionEvent evt) {                                              
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
                        + "Please select a source node and a data structure to continue.");
                // extract the name of the file 
                String fileName = new File(txtFile.getText()).getName();
                // update the file information labels with the appropriate values
                lblFileName.setText(fileName);
                lblNumberOfNodes.setText(String.valueOf(nodes.length));
                lblNumberOfLinks.setText(String.valueOf(numberOfLinks));
                // update and [re]populate the combo box to reflect the new changes
                cbxNodeList.removeAllItems(); 
                cbxNodeList.addItem(makeObj("Select a node"));
                for (int i = 0; i < nodes.length; i++) {
                    cbxNodeList.addItem(nodes[i]);
                }
                cbxNodeList.addItem(makeObj("All"));
                btnComputePaths.setEnabled(true);
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
        cbxNodeList.removeAllItems();
        cbxNodeList.addItem(makeObj("Upload to select"));
        numberOfLinks = 0;
        btnComputePaths.setEnabled(false);
    }                                            

    private void btnComputePathsActionPerformed(java.awt.event.ActionEvent evt) {                                                
        Node source = null;
        if (!txtFile.getText().endsWith(lblFileName.getText())) {
            JOptionPane.showMessageDialog(null, "You have not uploaded your newly selected file.",
                    "Message", JOptionPane.WARNING_MESSAGE);
        } else if (cbxNodeList.getSelectedItem().toString().equals("Select a node")) {
            JOptionPane.showMessageDialog(null, "No source node selected. Please select one to continue.",
                    "Message", JOptionPane.WARNING_MESSAGE);
        } else if (!rdbBinaryHeap.isSelected() && !rdbFibonacciHeap.isSelected()) {
            JOptionPane.showMessageDialog(null, "You must select a data structure to continue.",
                    "Message", JOptionPane.WARNING_MESSAGE);
        } else if (rdbBinaryHeap.isSelected()) {
            if (cbxNodeList.getSelectedItem().toString().equals("All")) {
                if (!chxShowTimes.isSelected()) {
                    for (int i = 0; i < nodes.length; i++) {
                        source = nodes[i];
                        run.computePathsBinaryHeap(nodes, source);
                        displayRoutingTable(rdbBinaryHeap, source);
                    }
                } else {
                    displayExecutionTime(rdbBinaryHeap, source);
                }
            } else {
                source = (Node) cbxNodeList.getSelectedItem();
                if (!chxShowTimes.isSelected()) {
                    run.computePathsBinaryHeap(nodes, source);
                    displayRoutingTable(rdbBinaryHeap, source);
                } else {
                    displayExecutionTime(rdbBinaryHeap, source);
                }
            }
            btnSaveToFile.setEnabled(true);
        } else if (rdbFibonacciHeap.isSelected()) {
            if (cbxNodeList.getSelectedItem().toString().equals("All")) {
                if (!chxShowTimes.isSelected()) {
                    for (int i = 0; i < nodes.length; i++) {
                        source = nodes[i];
                        displayRoutingTable(rdbFibonacciHeap, source);
                    }
                } else {
                    displayExecutionTime(rdbFibonacciHeap, source);
                }
            } else {
                source = (Node) cbxNodeList.getSelectedItem();
                if (!chxShowTimes.isSelected()) {
                    displayRoutingTable(rdbFibonacciHeap, source);
                } else {
                    displayExecutionTime(rdbFibonacciHeap, source);
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

    private void btnResetAllActionPerformed(java.awt.event.ActionEvent evt) {                                            
        btnClearFileActionPerformed(evt);
        undoSelections();
        txtResult.setText(DEFAULT_TEXT);
        btnSaveToFile.setEnabled(false);
    }                                           

    private void btnGoBackActionPerformed(java.awt.event.ActionEvent evt) {                                          
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

    private void btnResetOutputActionPerformed(java.awt.event.ActionEvent evt) {                                               
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
            java.util.logging.Logger.getLogger(Singlelayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Singlelayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Singlelayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Singlelayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Singlelayer().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify                     
    private javax.swing.JButton btnBrowseFile;
    private javax.swing.JButton btnClearFile;
    private javax.swing.JButton btnComputePaths;
    private javax.swing.JButton btnGoBack;
    private javax.swing.JButton btnResetAll;
    private javax.swing.JButton btnResetOutput;
    private javax.swing.JButton btnSaveToFile;
    private javax.swing.JButton btnUploadFile;
    private javax.swing.JComboBox cbxNodeList;
    private javax.swing.JCheckBox chxShowTimes;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JLabel lblFile;
    private javax.swing.JLabel lblFileName;
    private javax.swing.JLabel lblLinks;
    private javax.swing.JLabel lblNodes;
    private javax.swing.JLabel lblNumberOfLinks;
    private javax.swing.JLabel lblNumberOfNodes;
    private javax.swing.JLabel lblSelectFile;
    private javax.swing.JLabel lblSelectHeap;
    private javax.swing.JLabel lblSelectNode;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JPanel pnlAlgorithm;
    private javax.swing.JPanel pnlFile;
    private javax.swing.JPanel pnlResult;
    private javax.swing.JRadioButton rdbBinaryHeap;
    private javax.swing.JRadioButton rdbFibonacciHeap;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextField txtFile;
    private javax.swing.JTextArea txtResult;
    // End of variables declaration                   
}
