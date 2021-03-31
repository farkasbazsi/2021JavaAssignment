package model;

import game.GameEngine;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.*;
import res.ResourceLoader;
import model.building.*;

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

    private final ArrayList<Building> buildingList = new ArrayList<>();

    private GameEngine engine;

    public FfnProject() throws IOException {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setTitle("TAPS - Total Accurate Park Simulator");

        southPanel = new JPanel();
        southLabel = new JLabel();

        //centerPanel = new Board();
        centerPanel = new JPanel();
        engine = new GameEngine(centerPanel);

        read_buildings();

        westPanel = new JPanel();
        fillWestPanel();
        eastPanel = new JPanel();
        fillEastPanel();

        updateSouthLabelText();
        southPanel.setBackground(Color.white);

        westPanel.setBackground(Color.LIGHT_GRAY);
        westPanel.setPreferredSize(new Dimension(170, 736));

        eastPanel.setBackground(Color.LIGHT_GRAY);
        eastPanel.setPreferredSize(new Dimension(170, 736));

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

        exitMenuItem.addActionListener((ActionEvent event) -> {
            exitConfirmation();
        });

        setSize(1080, 1920); //1200,850
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void read_buildings() {
        try {
            File myObj = new File("src/game/buildings.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String name = myReader.next();
                String img = myReader.next();
                int height = myReader.nextInt();
                int length = myReader.nextInt();
                int cost = myReader.nextInt();
                String type = myReader.next();

                Details dt = new Details(img, height, length);

                switch (type) {
                    case "ride":
                        Ride rd = new Ride(dt, cost, name);
                        buildingList.add(rd);
                        break;
                    case "restaurant":
                        Restaurant rt = new Restaurant(dt, cost, name);
                        buildingList.add(rt);
                        break;
                    case "toilet":
                        Toilet to = new Toilet(dt, cost, name);
                        buildingList.add(to);
                        break;
                    case "trash":
                        TrashBin tb = new TrashBin(dt, cost, name);
                        buildingList.add(tb);
                        break;
                    case "plant":
                        Plant pl = new Plant(dt, cost, name, 5);
                        buildingList.add(pl);
                        break;
                    case "road":
                        Road ro = new Road(dt, cost, name);
                        buildingList.add(ro);
                        break;
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
        }
    }

    private void fillEastPanel() {
        //unfinished
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.LIGHT_GRAY);
        eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.PAGE_AXIS));

        JLabel eastLabel = new JLabel("Árak megadása");
        eastLabel.setFont(new Font("Calibri", Font.BOLD, 16));
        eastLabel.setPreferredSize(new Dimension(170, 80));
        eastLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton entranceFeebutton = new JButton();
        entranceFeebutton.setText("Belépődíj");
        entranceFeebutton.setPreferredSize(new Dimension(120, 40));
        entranceFeebutton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(entranceFeebutton);

        JButton gamesFeebutton = new JButton();
        gamesFeebutton.setText("Játék árak");
        gamesFeebutton.setPreferredSize(new Dimension(120, 40));
        buttonPanel.add(gamesFeebutton);

        JButton workerFeebutton = new JButton();
        workerFeebutton.setText("Dolgozói bérek");
        workerFeebutton.setPreferredSize(new Dimension(120, 40));
        buttonPanel.add(workerFeebutton);

        eastPanel.add(eastLabel);
        eastPanel.add(buttonPanel);
    }

    private void updateSouthLabelText() {
        //temporary amounts
        int money = 100000;
        int visitors = 0;
        int happinessLevel = 100;
        int parkValue = 200;

        southLabel.setText("Egyenleg: " + money + "Ft       Látogatók száma: "
                + visitors + " fő       Boldogság szintje: " + happinessLevel
                + "%        Park összértéke: " + parkValue + "Ft");
    }

    private void fillWestPanel() throws IOException {
        JPanel buildingPanel = new JPanel();
        JPanel insertPanel = new JPanel();
        final int row = 10;
        final int column = 2;

        westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
        buildingPanel.setLayout(new BorderLayout());
        insertPanel.setLayout(new GridLayout(row, column));

        for (int i = 0; i < column * row; ++i) {
            if (i < buildingList.size()) {
                Image icon;
                icon = ResourceLoader.loadImage("res/" + buildingList.get(i).getDetails().image);
                Image sized_icon = icon.getScaledInstance(64, 64, java.awt.Image.SCALE_SMOOTH);
                JButton button = new JButton(new ImageIcon(sized_icon));
                button.setToolTipText(buildingList.get(i).getName() + "     "
                        + buildingList.get(i).getBUILDING_COST() + "Ft");
                button.setContentAreaFilled(false);
                button.setPreferredSize(new Dimension(70, 90));

                //if u try the listener with "i", it doesnt work
                int iSubstitute = i;
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        engine.building = buildingList.get(iSubstitute);
                        //engine.placeBuilding = buildingList.get(iSubstitute).getName();
                    }
                });

                insertPanel.add(button);
            } else {
                JButton button = new JButton();
                button.setText("épület");
                button.setPreferredSize(new Dimension(70, 90));
                button.setContentAreaFilled(false);
                insertPanel.add(button);
            }
        }

        final Image bulldozer;
        bulldozer = ResourceLoader.loadImage("res/bulldozer.png");

        Image sized_bulldozer = bulldozer.getScaledInstance(64, 64, java.awt.Image.SCALE_SMOOTH);

        JButton Bbutton = new JButton(new ImageIcon(sized_bulldozer));
        Bbutton.setContentAreaFilled(false);

        buildingPanel.add(BorderLayout.CENTER, new JScrollPane(insertPanel));

        JLabel westLabel = new JLabel("Épületek beillesztése");
        westLabel.setFont(new Font("Calibri", Font.BOLD, 16));
        westLabel.setPreferredSize(new Dimension(170, 80));

        westPanel.add(westLabel);
        westPanel.add(buildingPanel);
        westPanel.add(Bbutton);
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

    public static void main(String[] args) throws IOException {
        FfnProject project = new FfnProject();
    }

}
