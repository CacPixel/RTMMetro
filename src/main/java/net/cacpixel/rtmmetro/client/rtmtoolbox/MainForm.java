/*
 * Created by JFormDesigner on Sat Aug 24 22:23:53 CST 2024
 */

package net.cacpixel.rtmmetro.client.rtmtoolbox;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import net.cacpixel.rtmmetro.util.ModLog;
import net.miginfocom.swing.*;

/**
 * @author CacPixel
 */
public class MainForm extends JFrame {
    public MainForm() {
        initComponents();
    }

    private void button6(ActionEvent e) {
        ModLog.debug("test button pressed!");
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        menuBar1 = new JMenuBar();
        menuFile = new JMenu();
        menuItem1 = new JMenuItem();
        menuItem2 = new JMenuItem();
        menuEdit = new JMenu();
        menuItem3 = new JMenuItem();
        menuItem4 = new JMenuItem();
        menuHelp = new JMenu();
        menuItem5 = new JMenuItem();
        menuItem6 = new JMenuItem();
        toolBar1 = new JToolBar();
        button1 = new JButton();
        button2 = new JButton();
        button3 = new JButton();
        button4 = new JButton();
        button5 = new JButton();
        splitPane2 = new JSplitPane();
        splitPane1 = new JSplitPane();
        scrollPane1 = new JScrollPane();
        tree1 = new JTree();
        panel1 = new JPanel();
        label1 = new JLabel();
        button6 = new JButton();
        scrollPane2 = new JScrollPane();
        textPane1 = new JTextPane();

        //======== this ========
        setBackground(Color.white);
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "hidemode 3",
            // columns
            "[1280,grow,fill]",
            // rows
            "[]" +
            "[720,grow]"));

        //======== menuBar1 ========
        {

            //======== menuFile ========
            {
                menuFile.setText("File");

                //---- menuItem1 ----
                menuItem1.setText("?MainForm.menuItem1.text?");
                menuFile.add(menuItem1);
                menuFile.add(menuItem2);
            }
            menuBar1.add(menuFile);

            //======== menuEdit ========
            {
                menuEdit.setText("Edit");
                menuEdit.add(menuItem3);
                menuEdit.add(menuItem4);
            }
            menuBar1.add(menuEdit);

            //======== menuHelp ========
            {
                menuHelp.setText("Help");
                menuHelp.add(menuItem5);
                menuHelp.add(menuItem6);
            }
            menuBar1.add(menuHelp);
        }
        setJMenuBar(menuBar1);

        //======== toolBar1 ========
        {
            toolBar1.setBackground(new Color(0xe5e5e5));
            toolBar1.addSeparator();
            toolBar1.add(button1);
            toolBar1.add(button2);
            toolBar1.addSeparator();
            toolBar1.add(button3);
            toolBar1.add(button4);
            toolBar1.add(button5);
            toolBar1.addSeparator();
        }
        contentPane.add(toolBar1, "cell 0 0,aligny top,growy 0");

        //======== splitPane2 ========
        {
            splitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
            splitPane2.setDividerLocation(500);

            //======== splitPane1 ========
            {
                splitPane1.setDividerLocation(200);

                //======== scrollPane1 ========
                {

                    //---- tree1 ----
                    tree1.setShowsRootHandles(true);
                    scrollPane1.setViewportView(tree1);
                }
                splitPane1.setLeftComponent(scrollPane1);

                //======== panel1 ========
                {
                    panel1.setLayout(new MigLayout(
                        "fill,hidemode 3,align center center",
                        // columns
                        "[fill]" +
                        "[fill]",
                        // rows
                        "[]" +
                        "[]"));
                    panel1.add(label1, "cell 0 0,alignx center,growx 0");

                    //---- button6 ----
                    button6.addActionListener(e -> button6(e));
                    panel1.add(button6, "cell 1 0,alignx center,growx 0");
                }
                splitPane1.setRightComponent(panel1);
            }
            splitPane2.setTopComponent(splitPane1);

            //======== scrollPane2 ========
            {

                //---- textPane1 ----
                textPane1.setEditable(false);
                scrollPane2.setViewportView(textPane1);
            }
            splitPane2.setBottomComponent(scrollPane2);
        }
        contentPane.add(splitPane2, "cell 0 1,grow");
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JMenuBar menuBar1;
    private JMenu menuFile;
    private JMenuItem menuItem1;
    private JMenuItem menuItem2;
    private JMenu menuEdit;
    private JMenuItem menuItem3;
    private JMenuItem menuItem4;
    private JMenu menuHelp;
    private JMenuItem menuItem5;
    private JMenuItem menuItem6;
    private JToolBar toolBar1;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JButton button5;
    private JSplitPane splitPane2;
    private JSplitPane splitPane1;
    private JScrollPane scrollPane1;
    private JTree tree1;
    private JPanel panel1;
    private JLabel label1;
    private JButton button6;
    private JScrollPane scrollPane2;
    private JTextPane textPane1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
