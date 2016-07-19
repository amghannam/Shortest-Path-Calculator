/** 
 ******************************************************************
 * File: Generator.java 
 * Author: Ahmed Ghannam (0910337)
 * 
 * Implements the interface for the graph generator. 
 */
package GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Random;
import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 *
 * @author Ahmed
 */
public class Generator extends javax.swing.JFrame {

    private ButtonGroup myButtonGroup;

    /**
     * Creates new form Generator
     */
    public Generator() {
        initComponents();
        addMenuBar();
        groupRadioButtons();
        setTitle("Graph Generator");
        setLocationRelativeTo(null);
    }

    private void addMenuBar() {
        // create and initialize menu bar items 
        JMenuItem aboutItem = new JMenuItem("About");
        JMenuItem exitItem = new JMenuItem("Exit");
        // add the items under their respective menus
        fileMenu.add(exitItem);
        helpMenu.add(aboutItem);
        // add an appropriate functionality to each item
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new About().setVisible(true);
            }
        });
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                exit();
            }
        });
    }

    private void groupRadioButtons() {
        myButtonGroup = new ButtonGroup();

        myButtonGroup.add(rdbSinglelayer);
        myButtonGroup.add(rdbMultilayer);
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

    private void toFile(String fileName, String contentToWrite) {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName)));
            writer.write(contentToWrite);
        } catch (IOException ex) {
            //this is empty for now
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {
                //this is empty for now
            }
        }
    }

    private String buildSinglelayer() {
        String strCount, strMax, strMin, full = "";
        int count;
        int max;
        int min;
        // get the input
        strCount = txtNumberOfNodes.getText();
        strMax = txtMaxWeight.getText();
        strMin = txtMinWeight.getText();
        while (true) {
            // parse the input
            try {
                count = Integer.parseInt(strCount);
                if (count <= 1) {
                    JOptionPane.showMessageDialog(null, "Vertex count is too small. "
                            + "A minimum of 2 vertices is required for single-layer topologies.",
                            "Message", JOptionPane.ERROR_MESSAGE);
                    break;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Invalid input detected. The generator only accepts integer values.",
                        "Message", JOptionPane.ERROR_MESSAGE);
                break;
            }
            try {
                max = Integer.parseInt(strMax);
                if (max < 0) {
                    JOptionPane.showMessageDialog(null, "Negative weights are not allowed!",
                            "Message", JOptionPane.ERROR_MESSAGE);
                    break;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Invalid input detected. The generator only accepts integer values.",
                        "Message", JOptionPane.ERROR_MESSAGE);
                break;
            }
            try {
                min = Integer.parseInt(strMin);
                if (min < 0) {
                    JOptionPane.showMessageDialog(null, "Negative weights are not allowed!",
                            "Message", JOptionPane.ERROR_MESSAGE);
                    break;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Invalid input detected. The generator only accepts integer values.",
                        "Message", JOptionPane.ERROR_MESSAGE);
                break;
            }
            if (max < min) {
                JOptionPane.showMessageDialog(null, "Maximum weight is smaller than minimum weight!",
                        "Message", JOptionPane.ERROR_MESSAGE);
                break;
            }
            // build the graph accordingly
            Random random = new Random();
            String str = "";
            for (int i = 0; i < count; i++) {
                str = str + "\n" + i + " " + i;
            }
            String anotherStr = str + "\n#";
            String yetAnotherStr = "";
            for (int i = 0; i < count * 3; i++) {
                yetAnotherStr = yetAnotherStr + "\n" + String.valueOf(random.nextInt(count))
                        + " " + String.valueOf(random.nextInt(count)) + " "
                        + String.valueOf(random.nextInt(max - min + 1) + min);
            }
            full = anotherStr + yetAnotherStr;
            break;
        }
        return full;
    }

    private String buildMultilayer() {
        String strCount, strMax, strMin, full = "";
        int count;
        int max;
        int min;
        // get the input
        strCount = txtNumberOfNodes.getText();
        strMax = txtMaxWeight.getText();
        strMin = txtMinWeight.getText();
        while (true) {
            // parse the input
            try {
                count = Integer.parseInt(strCount);
                if (count <= 3) {
                    JOptionPane.showMessageDialog(null, "Vertex count is too small. "
                            + "A minimum of 4 vertices is required for multilayer topologies.",
                            "Message", JOptionPane.ERROR_MESSAGE);
                    break;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Invalid input detected. The generator only accepts integer values.",
                        "Message", JOptionPane.ERROR_MESSAGE);
                break;
            }
            try {
                max = Integer.parseInt(strMax);
                if (max < 0) {
                    JOptionPane.showMessageDialog(null, "Negative weights are not allowed!",
                            "Message", JOptionPane.ERROR_MESSAGE);
                    break;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Invalid input detected. The generator only accepts integer values.",
                        "Message", JOptionPane.ERROR_MESSAGE);
                break;
            }
            try {
                min = Integer.parseInt(strMin);
                if (min < 0) {
                    JOptionPane.showMessageDialog(null, "Negative weights are not allowed!",
                            "Message", JOptionPane.ERROR_MESSAGE);
                    break;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Invalid input detected. The generator only accepts integer values.",
                        "Message", JOptionPane.ERROR_MESSAGE);
                break;
            }
            // build the graph accordingly
            Random random = new Random();
            String upper = "";
            for (int i = 0; i < count; i++) {
                upper = upper + "\nu" + i + " " + "u" + i;
            }
            String lower = "";
            for (int i = 0; i < count; i++) {
                lower = lower + "\nl" + i + " " + "l" + i;
            }
            String nodes = upper + lower + "\n#";
            String upperEdges = "";
            for (int i = 0; i < count * 3; i++) {
                upperEdges = upperEdges + "\nu" + String.valueOf(random.nextInt(count))
                        + " " + "u" + String.valueOf(random.nextInt(count))
                        + " " + String.valueOf(random.nextInt(max - min + 1) + min);
            }
            String lowerEdges = "";
            for (int i = 0; i < count * 3; i++) {
                lowerEdges = lowerEdges + "\nl" + String.valueOf(random.nextInt(count))
                        + " " + "l" + String.valueOf(random.nextInt(count))
                        + " " + String.valueOf(random.nextInt(max - min + 1) + min);
            }
            String connections = "";
            for (int i = 0; i < count; i++) {
                connections = connections + "\nu" + i + " " + "l" + i + " " + 0;
            }
            String connectionsReverse = "";
            for (int i = 0; i < count; i++) {
                connectionsReverse = connectionsReverse + "\nl" + i + " " + "u" + i + " " + 0;
            }
            full = nodes + upperEdges + lowerEdges + connections + connectionsReverse;
            break;
        }
        return full;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        pnlGenerator = new javax.swing.JPanel();
        lblType = new javax.swing.JLabel();
        rdbSinglelayer = new javax.swing.JRadioButton();
        rdbMultilayer = new javax.swing.JRadioButton();
        lblCount = new javax.swing.JLabel();
        txtNumberOfNodes = new javax.swing.JTextField();
        lblMax = new javax.swing.JLabel();
        txtMaxWeight = new javax.swing.JTextField();
        lblMin = new javax.swing.JLabel();
        txtMinWeight = new javax.swing.JTextField();
        btnGenerate = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        lblFile = new javax.swing.JLabel();
        txtFileName = new javax.swing.JTextField();
        separator = new javax.swing.JSeparator();
        btnClose = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        helpMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        pnlGenerator.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Generate a Graph", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, null, new java.awt.Color(0, 51, 204)));

        lblType.setText("Network type:");

        rdbSinglelayer.setText("Single-layer");

        rdbMultilayer.setText("Multilayer");

        lblCount.setText("Vertex count:");

        lblMax.setText("   Maximum weight:");

        lblMin.setText("  Minimum weight:");

        btnGenerate.setText("Generate");
        btnGenerate.setToolTipText("Generate a random topology with the specified properties.");
        btnGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateActionPerformed(evt);
            }
        });

        btnReset.setText("Reset");
        btnReset.setToolTipText("Clear all fields. ");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        lblFile.setText("  File name:");

        javax.swing.GroupLayout pnlGeneratorLayout = new javax.swing.GroupLayout(pnlGenerator);
        pnlGenerator.setLayout(pnlGeneratorLayout);
        pnlGeneratorLayout.setHorizontalGroup(
            pnlGeneratorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGeneratorLayout.createSequentialGroup()
                .addGroup(pnlGeneratorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlGeneratorLayout.createSequentialGroup()
                        .addGap(227, 227, 227)
                        .addComponent(btnGenerate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnReset))
                    .addGroup(pnlGeneratorLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlGeneratorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblMax)
                            .addComponent(lblMin, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblCount, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblFile, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblType, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(pnlGeneratorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlGeneratorLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(pnlGeneratorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtMaxWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtMinWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtFileName, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtNumberOfNodes, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(pnlGeneratorLayout.createSequentialGroup()
                                .addComponent(rdbSinglelayer)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(22, 22, 22)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlGeneratorLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(rdbMultilayer)
                .addGap(54, 54, 54))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlGeneratorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(separator)
                .addContainerGap())
        );
        pnlGeneratorLayout.setVerticalGroup(
            pnlGeneratorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGeneratorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGeneratorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblType)
                    .addComponent(rdbSinglelayer)
                    .addComponent(rdbMultilayer))
                .addGap(15, 15, 15)
                .addComponent(separator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlGeneratorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtNumberOfNodes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCount))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlGeneratorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMax)
                    .addComponent(txtMaxWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlGeneratorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMin)
                    .addComponent(txtMinWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlGeneratorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFile)
                    .addComponent(txtFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(pnlGeneratorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGenerate)
                    .addComponent(btnReset))
                .addContainerGap())
        );

        btnClose.setText("Close");
        btnClose.setToolTipText("Close this form.");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
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
                    .addComponent(pnlGenerator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnClose)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlGenerator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClose)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                        

    private void btnGenerateActionPerformed(java.awt.event.ActionEvent evt) {                                            
        String graph = "";
        if (!rdbSinglelayer.isSelected() && !rdbMultilayer.isSelected()) {
            JOptionPane.showMessageDialog(null, "No network type selected. You must select one to continue.",
                    "Message", JOptionPane.WARNING_MESSAGE);
        } else if (txtNumberOfNodes.getText().isEmpty() || txtMaxWeight.getText().isEmpty()
                || txtMinWeight.getText().isEmpty() || txtFileName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "All fields are required!",
                    "Message", JOptionPane.WARNING_MESSAGE);
        } else {
            if (rdbSinglelayer.isSelected()) {
                graph = buildSinglelayer();
                if (!graph.isEmpty()) {
                    toFile(txtFileName.getText(), graph);
                    JOptionPane.showMessageDialog(null, "Generation successful. "
                            + "You can now use the generated topology for your calculations.");
                    this.dispose();
                }
            } else if (rdbMultilayer.isSelected()) {
                graph = buildMultilayer();
                if (!graph.isEmpty()) {
                    toFile(txtFileName.getText(), graph);
                    JOptionPane.showMessageDialog(null, "Generation successful. "
                            + "You can now use the generated topology for your calculations.");
                    this.dispose();
                }
            }
        }
    }                                           

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {                                         
        myButtonGroup.clearSelection();
        txtNumberOfNodes.setText("");
        txtMaxWeight.setText("");
        txtMinWeight.setText("");
        txtFileName.setText("");
    }                                        

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {                                         
        this.setVisible(false);
        this.dispose();
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
            java.util.logging.Logger.getLogger(Generator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Generator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Generator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Generator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Generator().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify                     
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnGenerate;
    private javax.swing.JButton btnReset;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JLabel lblCount;
    private javax.swing.JLabel lblFile;
    private javax.swing.JLabel lblMax;
    private javax.swing.JLabel lblMin;
    private javax.swing.JLabel lblType;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JPanel pnlGenerator;
    private javax.swing.JRadioButton rdbMultilayer;
    private javax.swing.JRadioButton rdbSinglelayer;
    private javax.swing.JSeparator separator;
    private javax.swing.JTextField txtFileName;
    private javax.swing.JTextField txtMaxWeight;
    private javax.swing.JTextField txtMinWeight;
    private javax.swing.JTextField txtNumberOfNodes;
    // End of variables declaration                   
}
