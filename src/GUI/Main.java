/**
 ************************************************************
 * File: Main.java 
 * Author: Ahmed Ghannam (0910337)
 * 
 * Runs the main program. 
 */
package GUI;

import javax.swing.UIManager;

/**
 *
 * @author Ahmed
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ex) {
            ;
        }
        new Start().setVisible(true);
    }
}
