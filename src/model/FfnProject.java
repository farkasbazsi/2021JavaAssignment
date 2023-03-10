package model;

import game.GameEngine;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.*;
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
    private final ArrayList<JButton> buttons = new ArrayList<>();
    private int chosenIndex;

    private Timer playTimer;
    private Timer gameTime;
    private int currentTime = 0;
    private int buildingNum = 0;

    private GameEngine engine;

    public FfnProject() throws IOException {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("TAPS - Total Accurate Park Simulator");

        southPanel = new JPanel();
        southLabel = new JLabel();

        centerPanel = new JPanel();

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

        playTimer = new Timer(100, new playTimerListener());
        playTimer.start();

        gameTime = new Timer(1000, new gameTimerListener());

        URL url = getClass().getResource("../res/dojo.png");
        setIconImage(Toolkit.getDefaultToolkit().getImage(url));

        setSize(1080, 1920);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class gameTimerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            currentTime++;
        }
    }

    class playTimerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            updateSouthLabelText();
        }

    }

    /**
     * Gets the attributes of the buildings from the buildings.txt. Insert them
     * to the buildings arraylist.
     *
     * @param
     */
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

    /**
     * Fill the right sided pane with content. In this pane the player can
     * change the different fees, set which games to be free to use, and open
     * the park.
     *
     */
    private void fillEastPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.LIGHT_GRAY);
        JPanel checkPanel = new JPanel();
        checkPanel.setBackground(Color.LIGHT_GRAY);
        checkPanel.setPreferredSize(new Dimension(170, 50));
        JLabel checkLabel = new JLabel("<html><div style='text-align: center;'>"
                + "A belépődíj<br>az alábbi játékokat<br>tartalmazza</html>");
        Border border = checkLabel.getBorder();
        Border margin = new EmptyBorder(10, 10, 10, 10);
        checkLabel.setBorder(new CompoundBorder(border, margin));
        checkPanel.add(checkLabel);

        eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.PAGE_AXIS));

        JLabel eastLabel = new JLabel("Beállítások");
        eastLabel.setFont(new Font("Calibri", Font.BOLD, 16));
        eastLabel.setPreferredSize(new Dimension(170, 50));
        eastLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton entranceFeebutton = new JButton();
        entranceFeebutton.setText("Belépődíj");
        entranceFeebutton.setPreferredSize(new Dimension(120, 30));
        buttonPanel.add(entranceFeebutton);

        entranceFeebutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engine.getPayment().setEntranceFee(100);
            }
        });

        JButton workerFeebutton = new JButton();
        workerFeebutton.setText("Dolgozói bérek");
        workerFeebutton.setPreferredSize(new Dimension(120, 30));
        buttonPanel.add(workerFeebutton);

        workerFeebutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engine.getPayment().setWorkerFee(30);
            }
        });

        JButton gamesFeebutton = new JButton();
        gamesFeebutton.setText("Játékok ára");
        gamesFeebutton.setPreferredSize(new Dimension(120, 30));
        buttonPanel.add(gamesFeebutton);

        gamesFeebutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engine.getPayment().setGamesFee(60);
            }
        });

        JButton restaurantFeebutton = new JButton();
        restaurantFeebutton.setText("Éttermek ára");
        restaurantFeebutton.setPreferredSize(new Dimension(120, 30));
        buttonPanel.add(restaurantFeebutton);

        restaurantFeebutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engine.getPayment().setRestaurantFee(50);
            }
        });

        JButton toiletFeebutton = new JButton();
        toiletFeebutton.setText("Mosdó ára");
        toiletFeebutton.setPreferredSize(new Dimension(120, 30));
        buttonPanel.add(toiletFeebutton);

        toiletFeebutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engine.getPayment().setToiletFee(10);
            }
        });

        JButton hireCleanerButton = new JButton();
        hireCleanerButton.setText("+Takarító");
        hireCleanerButton.setPreferredSize(new Dimension(120, 30));
        buttonPanel.add(hireCleanerButton);

        hireCleanerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    engine.newCleaner();
                } catch (IOException ex) {
                    Logger.getLogger(FfnProject.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        JButton fireCleanerButton = new JButton();
        fireCleanerButton.setText("-Takarító");
        fireCleanerButton.setPreferredSize(new Dimension(120, 30));
        buttonPanel.add(fireCleanerButton);

        fireCleanerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engine.fireCleaner();
            }
        });

        JButton newMechanicButton = new JButton();
        newMechanicButton.setText("+Szerelő");
        newMechanicButton.setPreferredSize(new Dimension(120, 30));
        buttonPanel.add(newMechanicButton);

        newMechanicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    engine.newMechanic();
                } catch (IOException ex) {
                    Logger.getLogger(FfnProject.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        JButton fireMechanicButton = new JButton();
        fireMechanicButton.setText("-Szerelő");
        fireMechanicButton.setPreferredSize(new Dimension(120, 30));
        buttonPanel.add(fireMechanicButton);

        fireMechanicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engine.fireMechanic();
            }
        });

        for (int i = 0; i < 9; i++) {
            JCheckBox cb = new JCheckBox(buildingList.get(i).getName());
            cb.setBackground(Color.LIGHT_GRAY);
            cb.setPreferredSize(new Dimension(150, 20));

            cb.addItemListener((ItemEvent arg0) -> {
                if (cb.isSelected()) {
                    engine.getFreeGames().add(cb.getText());
                } else {
                    engine.getFreeGames().remove(cb.getText());
                }
            });

            checkPanel.add(cb);
        }

        JButton openbutton = new JButton();
        openbutton.setText("Park megnyitása");
        openbutton.setPreferredSize(new Dimension(100, 40));
        openbutton.setAlignmentX(Component.CENTER_ALIGNMENT);

        openbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < engine.buildings.size(); i++) {
                    if (engine.buildings.get(i) != null && engine.buildings.get(i).getBUILDING_COST() >= 60) {
                        buildingNum++;
                    }
                }
                if (buildingNum < 5) {
                    showMessage();
                    buildingNum = 0;
                } else {
                    try {
                        engine.openPark();
                    } catch (IOException ex) {
                        Logger.getLogger(FfnProject.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    openbutton.setEnabled(false);
                    gameTime.start();
                }
            }
        });

        eastPanel.add(eastLabel);
        eastPanel.add(buttonPanel);
        eastPanel.add(checkPanel);
        eastPanel.add(openbutton);
    }

    /**
     * Error message if the doesnt contain 5 buildings
     */
    private void showMessage() {
        JOptionPane.showMessageDialog(this, "A minimális épületszámot (5) el kell érnie a parknak a megnyitáshoz!", "Park megnyitása",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Updates the south panel
     */
    private void updateSouthLabelText() {
        southLabel.setText("Egyenleg: " + engine.getMoney() + "Ft       Látogatók száma: "
                + engine.getVisitorsCount() + " fő       Boldogság szintje: " + engine.getAvgHappiness()
                + "%        Park összértéke: " + engine.getParkValue() + "Ft       "
                + "Megnyitás óta eltelt idő: " + (currentTime < 60 ? currentTime + " mp" : currentTime < 3600 ? currentTime / 60 + " perc " + currentTime % 60 + " másodperc"
                                : currentTime / 3600 + " óra " + currentTime % 3600 + " perc"));
    }

    /**
     * Fill the left sided pane with content. From this pane the player can
     * insert buildings into the park, and destroy them by the bulldozer icon.
     *
     * @throws IOException , if the ResourceLoader can't find the pictures
     */
    private void fillWestPanel() throws IOException {
        JPanel buildingPanel = new JPanel();
        JPanel insertPanel = new JPanel();
        final int row = 8;
        final int column = 2;

        westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
        buildingPanel.setLayout(new BorderLayout());
        insertPanel.setLayout(new GridLayout(row, column));

        for (int i = 0; i < column * row; ++i) {
            if (i < buildingList.size()) {
                if ("road".equals(buildingList.get(i).getName())) {
                    engine = new GameEngine(centerPanel, buildingList.get(i));
                }
                Image icon;
                icon = ResourceLoader.loadImage("res/" + buildingList.get(i).getDetails().image);
                Image sized_icon = icon.getScaledInstance(64, 64, java.awt.Image.SCALE_SMOOTH);
                JButton button = new JButton(new ImageIcon(sized_icon));
                button.setToolTipText(buildingList.get(i).getName() + "     "
                        + buildingList.get(i).getBUILDING_COST() + "Ft");
                button.setContentAreaFilled(false);
                button.setPreferredSize(new Dimension(70, 90));
                int iSubstitute = i;
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (engine.getBuilding() == buildingList.get(iSubstitute)) {
                            engine.setBuilding(null);
                            button.setBackground(null);
                        } else if (engine.getBuilding() == null) {
                            button.setBackground(Color.green.brighter());
                            button.setOpaque(true);
                            engine.setBuilding(buildingList.get(iSubstitute));
                            chosenIndex = iSubstitute;
                            engine.setDestroy(false);
                            buttons.get(buttons.size() - 1).setBackground(null);
                        } else {
                            buttons.get(chosenIndex).setBackground(null);
                            button.setBackground(Color.green.brighter());
                            button.setOpaque(true);
                            engine.setBuilding(buildingList.get(iSubstitute));
                            chosenIndex = iSubstitute;
                        }
                    }
                });
                buttons.add(button);
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
        Bbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (engine.isDestroy()) {
                    engine.setDestroy(false);
                    Bbutton.setBackground(null);
                } else if (engine.getBuilding() != null) {
                    engine.setBuilding(null);
                    buttons.get(chosenIndex).setBackground(null);
                    engine.setDestroy(true);
                    Bbutton.setBackground(Color.red.brighter());
                    Bbutton.setOpaque(true);
                } else {
                    engine.setDestroy(true);
                    Bbutton.setBackground(Color.red.brighter());
                    Bbutton.setOpaque(true);
                }

            }
        });
        buttons.add(Bbutton);

        Bbutton.setContentAreaFilled(false);
        Bbutton.setToolTipText("Épület lebontása. Építési költség fele visszatérítésre kerül.");

        buildingPanel.add(BorderLayout.CENTER, new JScrollPane(insertPanel));

        JLabel westLabel = new JLabel("Épületek beillesztése");
        westLabel.setFont(new Font("Calibri", Font.BOLD, 16));
        westLabel.setPreferredSize(new Dimension(170, 80));

        westPanel.add(westLabel);
        westPanel.add(buildingPanel);
        westPanel.add(Bbutton);
    }

    /**
     * Exitconfirmation if you want to leave the game
     */
    private void exitConfirmation() {
        Object[] btns = {"Igen", "Nem"};
        int choice = JOptionPane.showOptionDialog(this, "Valóban ki akar lépni?",
                "Megerősítés",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                btns, btns[1]);
        if (choice == 0) {
            System.exit(0);
        }
    }

    public static void main(String[] args) throws IOException {
        FfnProject project = new FfnProject();
    }

}
