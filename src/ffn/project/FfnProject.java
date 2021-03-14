package ffn.project;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class FfnProject extends JFrame {

    private final Container cp = getContentPane();
    private final JMenuBar menuBar = new JMenuBar();
    private final JMenu fileMenu = new JMenu("Fájl");
    private final JMenuItem exitMenuItem = new JMenuItem("Kilép");

    private final JPanel southPanel;
    private final JLabel southLabel;
    private final JPanel centerPanel;
    private final JPanel westPanel;
    private final JPanel eastPanel;

    public FfnProject() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setTitle("TAPS - Total Accurate Park Simulator");

        southPanel = new JPanel();
        southLabel = new JLabel();
        centerPanel = new JPanel();
        westPanel = new JPanel();
        eastPanel = new JPanel();

        southLabel.setText("Hanyas a kabat");
        southPanel.setBackground(Color.LIGHT_GRAY);

        westPanel.setBackground(Color.gray);
        westPanel.setPreferredSize(new Dimension(170, 100));

        eastPanel.setBackground(Color.gray);
        eastPanel.setPreferredSize(new Dimension(170, 100));

        centerPanel.setBackground(new Color(152, 251, 152));
        centerPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        southPanel.add(southLabel);

        setJMenuBar(menuBar);
        menuBar.add(fileMenu);
        fileMenu.add(exitMenuItem);

        cp.setLayout(new BorderLayout());
        cp.add(southPanel, "South");
        cp.add(westPanel, "West");
        cp.add(eastPanel, "East");
        cp.add(centerPanel, "Center");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                exitConfirmation();
            }
        });

        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                exitConfirmation();
            }
        });

        setSize(1200, 850);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

    private void exitConfirmation() {
        Object[] buttons = {"Igen", "Nem"};
        int choice = JOptionPane.showOptionDialog(this, "Valóban ki akar lépni?",
                "Megerősítés",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                buttons, buttons[1]);
        if (choice == 0) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        FfnProject project = new FfnProject();
    }

}
