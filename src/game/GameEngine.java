/**
 * 2020/2021/2
 * Szoftvertechnológia
 *
 * FFN project
 * Nagy Gergő, Falusi Gergő Gábor, Farkas Balázs
 *
 * 2021.05.12.
 */
package game;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;
import model.Details;
import model.ModelTile;
import model.Payment;
import model.Tile;
import model.worker.Worker;
import model.worker.Cleaner;
import model.worker.Mechanic;
import model.building.Building;
import model.Visitor;
import model.building.BuildingState;
import model.building.Plant;
import model.building.Restaurant;
import model.building.Ride;
import model.building.Road;
import model.building.Toilet;
import model.building.TrashBin;
import res.ResourceLoader;

public class GameEngine {

    private Timer timer;
    private int money;
    private ArrayList<Worker> workers = new ArrayList<>();
    public ArrayList<Building> buildings;
    private ArrayList<Visitor> visitors = new ArrayList<>();
    private Payment payment = new Payment();

    private ArrayList<String> freeGames = new ArrayList<>();

    ModelTile[][] modelTiles;
    Tile[][] tiles;
    private final int height = 23;
    private final int width = 25;
    Building building;
    private boolean destroy;

    private Hashtable<Integer, ArrayList<Integer>> hm = new Hashtable<>();
    private int v = 1;
    ArrayList<ArrayList<Integer>> graph
            = new ArrayList<>();
    int parentRoadKey = 0;
    int parentRoadI = 0;
    int parentRoadJ = 0;

    private boolean isOpen = false;

    int hmDeleteIndex;

    Timer t = new Timer(700, new visitorTimer());//1500 testing, 700 original
    Timer arrival = new Timer(5000, new arrivalTimer());//5000 original
    Timer repair = new Timer(10000, new repairTimer());

    private boolean edge = false;

    Timer wT = new Timer(600, new workerTimer());

    int ii = -1;
    int jj = -1;
    int keresett = -1;

    private ArrayList<Building> wrongBuildings = new ArrayList<>();

    ArrayList<Integer> activateInd = new ArrayList<>();

    public GameEngine(JPanel panel, Building spawnRoad) throws IOException {
        this.money = 10000;
        buildings = new ArrayList<>();
        tiles = new Tile[height][width];
        modelTiles = new ModelTile[height][width];
        building = null;
        destroy = false;

        generateField(panel, spawnRoad);

        wT.start();
    }

    /**
     * Generates the tiles matrix, and fills up the modelTiles matrix
     * accordingly. Places down the spawning road(undestroyable).
     *
     * @param panelthe panel to which the tiles matrix gets added (centerpanel.)
     * @param spawnRoad an object of a road to form the first road
     * @throws IOException , if the ResourceLoader can't find the pictures
     */
    private void generateField(JPanel panel, Building spawnRoad) throws IOException {
        panel.setLayout(new GridLayout(height, width));
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i == 22 && j == 12) {
                    buildings.add(new Road((Road) spawnRoad));
                    modelTiles[i][j] = new ModelTile("road");
                    modelTiles[i][j].setIndex(0);
                    buildings.get(0).getIndexes().add(new Point(i, j));
                    tiles[i][j] = new Tile(ResourceLoader.loadImage("res/entrance.png"));

                    ArrayList<Integer> newNode = new ArrayList<>();
                    newNode.add(i);
                    newNode.add(j);
                    hm.put(0, newNode);

                } else {
                    modelTiles[i][j] = new ModelTile("grass");
                    tiles[i][j] = new Tile(ResourceLoader.loadImage("res/Grass.png"));
                }

                tiles[i][j].addMouseListener(new mouseListener(i, j));
                panel.add(tiles[i][j]);
            }
        }
    }

    /**
     * Opens the park for the visitors.
     *
     * @throws IOException
     */
    public void openPark() throws IOException {
        isOpen = true;
        newVisitor();
        repair.start();
    }

    /**
     * Insert new visitor into the park. Adds the enterancefee - upkeep to the
     * players money
     *
     * @throws IOException, if the ResourceLoader can't find the picture
     */
    private void newVisitor() throws IOException {
        Details dt = new Details("res/happy_man.png", 10, 10);

        Visitor visitor = new Visitor(dt/*, icon*/);
        visitor.setBackground(visitor.getHappiness() < 40 ? Color.RED : visitor.getHappiness() > 70 ? Color.GREEN : Color.YELLOW);
        tiles[22][12].add(visitor);
        visitors.add(visitor);
        t.start();
        arrival.start();
        getRandomElement(visitor);

        int sum = 0;
        for (Building b : buildings) {
            if (b != null && !(b instanceof Road)) {
                sum += (int) b.getBUILDING_COST() / 50;
            }
        }
        money += payment.getEntranceFee() - sum;
    }

    /**
     * Insert new visitor in every five seconds, until the visitors number less
     * than the given number.
     *
     */
    private class arrivalTimer implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (visitors.size() < 15) {
                try {
                    newVisitor();
                } catch (IOException ex) {
                    Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Gets called, if the placement is correct (no overlapping, no
     * outindexing). Searches in the buildings arrayList for a null element, if
     * there is none, adds a newly created element accordingly to the type. (if
     * there is a null element, overwrites the given element). Sets the
     * types(dojo, etc) and indexes(buildings arrayList index, showing the
     * building itself). Changes the pictures of the correct tiles in the Tiles
     * matrix & repaints them.
     *
     * @param iSubstitute, i index of matrixes.
     * @param jSubstitute, j index of matrixes.
     */
    private void createBuilding(int iSubstitute, int jSubstitute) throws IOException {
        boolean full = true;
        int ind = -1;
        for (int i = 0; i < buildings.size(); i++) {
            if (buildings.get(i) == null) {
                if (building instanceof Ride) {
                    buildings.set(i, new Ride((Ride) building));
                } else if (building instanceof Restaurant) {
                    buildings.set(i, new Restaurant((Restaurant) building));
                } else if (building instanceof Toilet) {
                    buildings.set(i, new Toilet((Toilet) building));
                } else if (building instanceof TrashBin) {
                    buildings.set(i, new TrashBin((TrashBin) building));
                } else if (building instanceof Road) {
                    buildings.set(i, new Road((Road) building));
                } else if (building instanceof Plant) {
                    buildings.set(i, new Plant((Plant) building));
                } else {
                    System.err.println("Can't find class");
                }
                full = false;
                ind = i;
                break;
            }
        }
        if (full) {
            if (building instanceof Ride) {
                buildings.add(new Ride((Ride) building));

                if (building.getDetails().height == 2) {
                    insertRoadToGraph(iSubstitute + 1, jSubstitute);
                }
                if (building.getDetails().length == 2) {
                    insertRoadToGraph(iSubstitute, jSubstitute + 1);
                }
                if (building.getDetails().length == 2 && building.getDetails().height == 2) {
                    insertRoadToGraph(iSubstitute + 1, jSubstitute + 1);
                }
                insertRoadToGraph(iSubstitute, jSubstitute);

            } else if (building instanceof Restaurant) {
                buildings.add(new Restaurant((Restaurant) building));

                if (building.getDetails().height == 2) {
                    insertRoadToGraph(iSubstitute + 1, jSubstitute);
                }
                if (building.getDetails().length == 2) {
                    insertRoadToGraph(iSubstitute, jSubstitute + 1);
                }
                if (building.getDetails().length == 2 && building.getDetails().height == 2) {
                    insertRoadToGraph(iSubstitute + 1, jSubstitute + 1);
                }
                insertRoadToGraph(iSubstitute, jSubstitute);

            } else if (building instanceof Toilet) {
                buildings.add(new Toilet((Toilet) building));

                insertRoadToGraph(iSubstitute, jSubstitute);

            } else if (building instanceof TrashBin) {
                buildings.add(new TrashBin((TrashBin) building));
            } else if (building instanceof Road) {
                buildings.add(new Road((Road) building));

                Road temp = (Road) buildings.get(buildings.size() - 1);
                temp.setTrashOnIt(true);
                insertRoadToGraph(iSubstitute, jSubstitute);

            } else if (building instanceof Plant) {
                buildings.add(new Plant((Plant) building));
            } else {
                System.err.println("Can't find class");
            }
            ind = buildings.size() - 1;
        }

        if (building instanceof Restaurant || building instanceof Ride || building instanceof Toilet) {

            for (int k = 0; k <= building.getDetails().height - 1; k++) {
                for (int l = 0; l <= building.getDetails().length - 1; l++) {
                    try {
                        buildings.get(ind).getIndexes().add(new Point(iSubstitute + k, jSubstitute + l));
                        modelTiles[iSubstitute + k][jSubstitute + l].setType(building.getName());
                        modelTiles[iSubstitute + k][jSubstitute + l].setIndex(ind);
                        tiles[iSubstitute + k][jSubstitute + l].setImage(
                                ResourceLoader.loadImage("res/placeHolder.png"));
                        tiles[iSubstitute + k][jSubstitute + l].repaint();

                        if (buildings.get(ind) instanceof Ride) {
                            Ride ride = (Ride) buildings.get(ind);
                            ride.changeState(BuildingState.BUILDING);
                            buildings.set(ind, ride);
                        } else if (buildings.get(ind) instanceof Restaurant) {
                            Restaurant restaurant = (Restaurant) buildings.get(ind);
                            restaurant.changeState(BuildingState.BUILDING);
                            buildings.set(ind, restaurant);
                        } else if (buildings.get(ind) instanceof Toilet) {
                            Toilet toilet = (Toilet) buildings.get(ind);
                            toilet.changeState(BuildingState.BUILDING);
                            buildings.set(ind, toilet);
                        }

                    } catch (IOException ex) {
                        Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            final int index = ind;
            final Building relevant = building;

            Timer delay = new Timer(4000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    final int index2 = index;
                    for (int k = 0; k <= relevant.getDetails().height - 1; k++) {
                        for (int l = 0; l <= relevant.getDetails().length - 1; l++) {
                            try {
                                tiles[iSubstitute + k][jSubstitute + l].setImage(
                                        ResourceLoader.loadImage("res/" + buildings.get(index2).getDetails().image));
                                tiles[iSubstitute + k][jSubstitute + l].repaint();

                                if (buildings.get(index2) instanceof Ride) {
                                    Ride ride = (Ride) buildings.get(index2);
                                    ride.changeState(BuildingState.ACTIVE);
                                    buildings.set(index2, ride);
                                } else if (buildings.get(index2) instanceof Restaurant) {
                                    Restaurant restaurant = (Restaurant) buildings.get(index2);
                                    restaurant.changeState(BuildingState.ACTIVE);
                                    buildings.set(index2, restaurant);
                                } else if (buildings.get(index2) instanceof Toilet) {
                                    Toilet toilet = (Toilet) buildings.get(index2);
                                    toilet.changeState(BuildingState.ACTIVE);
                                    buildings.set(index2, toilet);
                                }

                            } catch (IOException ex) {
                                Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            });
            delay.setRepeats(false);
            delay.start();
        } else {
            for (int k = 0; k <= building.getDetails().height - 1; k++) {
                for (int l = 0; l <= building.getDetails().length - 1; l++) {
                    try {
                        buildings.get(ind).getIndexes().add(new Point(iSubstitute + k, jSubstitute + l));
                        modelTiles[iSubstitute + k][jSubstitute + l].setType(building.getName());
                        modelTiles[iSubstitute + k][jSubstitute + l].setIndex(ind);
                        tiles[iSubstitute + k][jSubstitute + l].setImage(
                                ResourceLoader.loadImage("res/" + buildings.get(ind).getDetails().image));
                        tiles[iSubstitute + k][jSubstitute + l].repaint();
                    } catch (IOException ex) {
                        Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    /**
     * Adds in a new mechanic type worker.
     *
     * @throws IOException, if the ResourceLoader can't find the picture
     */
    public void newMechanic() throws IOException {
        Details dt = new Details("res/mechanic.png", 10, 10);
        BufferedImage icon = (BufferedImage) ResourceLoader.loadImage(dt.image);
        Mechanic mc = new Mechanic(dt, icon);
        tiles[22][12].add(mc);
        workers.add(mc);

        getRandomBuild(mc);
    }

    /**
     * Timer for repairing
     */
    private class repairTimer implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            getRepairingBuilding();
        }
    }

    /**
     * Give back a random building from buildings arraylist.
     *
     * @param visitor
     */
    private void getRepairingBuilding() {
        Random rand = new Random();
        Building randBuild;
        do {
            randBuild = buildings.get(rand.nextInt(buildings.size()));
        } while (randBuild.getBUILDING_COST() < 60);
        wrongBuildings.add(randBuild);
    }

    /**
     * When the player builds one tile road, this function insert a new node to
     * the graph. The new node represents the road with a hashtable.
     *
     * @param iSubstitute
     * @param jSubstitute
     */
    private void insertRoadToGraph(int iSubstitute, int jSubstitute) {
        ArrayList<Integer> newNode = new ArrayList<>();
        newNode.add(iSubstitute);
        newNode.add(jSubstitute);
        hm.put(v, newNode);

        if (v == 1) {
            graph.add(new ArrayList<>());
            graph.add(new ArrayList<>());
            addEdge(graph, 0, v);
        } else {
            graph.add(new ArrayList<>());
            if (iSubstitute + 1 <= 22 && iSubstitute - 1 >= 0) {
                if ("road".equals(modelTiles[iSubstitute + 1][jSubstitute].getType())) {
                    parentRoadI = iSubstitute + 1;
                    parentRoadJ = jSubstitute;

                    hm.forEach((k, Pvalue) -> {
                        if (Pvalue.get(0).equals(parentRoadI) && Pvalue.get(1).equals(parentRoadJ)) {
                            parentRoadKey = k;
                        }
                    });

                    addEdge(graph, parentRoadKey, v);
                }

                if ("road".equals(modelTiles[iSubstitute - 1][jSubstitute].getType())) {
                    parentRoadI = iSubstitute - 1;
                    parentRoadJ = jSubstitute;

                    hm.forEach((k, Pvalue) -> {
                        if (Pvalue.get(0).equals(parentRoadI) && Pvalue.get(1).equals(parentRoadJ)) {
                            parentRoadKey = k;
                        }
                    });

                    addEdge(graph, parentRoadKey, v);
                }
            }

            if (jSubstitute + 1 <= 24 && jSubstitute - 1 >= 0) {
                if ("road".equals(modelTiles[iSubstitute][jSubstitute + 1].getType())) {
                    parentRoadI = iSubstitute;
                    parentRoadJ = jSubstitute + 1;

                    hm.forEach((k, Pvalue) -> {
                        if (Pvalue.get(0).equals(parentRoadI) && Pvalue.get(1).equals(parentRoadJ)) {
                            parentRoadKey = k;
                        }
                    });

                    addEdge(graph, parentRoadKey, v);
                }
                if ("road".equals(modelTiles[iSubstitute][jSubstitute - 1].getType())) {
                    parentRoadI = iSubstitute;
                    parentRoadJ = jSubstitute - 1;

                    hm.forEach((k, Pvalue) -> {
                        if (Pvalue.get(0).equals(parentRoadI) && Pvalue.get(1).equals(parentRoadJ)) {
                            parentRoadKey = k;
                        }
                    });

                    addEdge(graph, parentRoadKey, v);
                }
            }

            parentRoadI = 0;
            parentRoadJ = 0;
            parentRoadKey = 0;
        }
        v++;
    }

    /**
     * Works with a building's indexes. Changes the pictures in the tiles matrix
     * to grass and repaints. Changes the types and indexes back to grass and -1
     * in the modelTiles matrix. Sets the given index in the buildings arrayList
     * to null.
     *
     * @param iSubstitute, i index of matrixes.
     * @param jSubstitute, j index of matrixes.
     */
    private void removeBuilding(int iSubstitute, int jSubstitute) {
        int tempIndex = modelTiles[iSubstitute][jSubstitute].getIndex();
        for (Point i : buildings.get(modelTiles[iSubstitute][jSubstitute].getIndex()).getIndexes()) {
            try {
                tiles[(int) i.getX()][(int) i.getY()].setImage(ResourceLoader.loadImage("res/grass.png"));
            } catch (IOException ex) {
                Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
            tiles[(int) i.getX()][(int) i.getY()].repaint();
            modelTiles[(int) i.getX()][(int) i.getY()].setType("grass");
            modelTiles[(int) i.getX()][(int) i.getY()].setIndex(-1);
        }
        int refound = (int) (buildings.get(tempIndex).getBUILDING_COST()) / 2;
        money += refound;
        buildings.set(tempIndex, null);

        hm.forEach((k, Pvalue) -> {
            if (Pvalue.get(0).equals(iSubstitute) && Pvalue.get(1).equals(jSubstitute)) {
                hmDeleteIndex = k;
            }
        });
        for (int i = 0; i < graph.size(); i++) {
            if (i == hmDeleteIndex) {
                graph.get(i).clear();
            } else {
                graph.get(i).remove(new Integer(hmDeleteIndex));
            }
        }
    }

    /**
     * Adds in a new cleaner type worker
     *
     * @throws IOException, if the ResourceLoader cant't find the picture
     */
    public void newCleaner() throws IOException {
        Details dt = new Details("res/cleaner.png", 10, 10);
        BufferedImage icon = (BufferedImage) ResourceLoader.loadImage(dt.image);
        Cleaner cleaner = new Cleaner(dt, icon);
        tiles[22][12].add(cleaner);
        workers.add(cleaner);

        try {
            getRandomRoad(cleaner);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.print(workers.size());
    }

    /**
     * Removes a cleaner type worker
     */
    public void fireCleaner() {
        if (workers.size() > 0) {
            tiles[workers.get(0).i][workers.get(0).j].remove(workers.get(0));
            tiles[workers.get(0).i][workers.get(0).j].repaint();
            workers.remove(0);
        }
    }

    /**
     * Removes a mechanic type worker
     */
    public void fireMechanic() {
        if (workers.size() > 0) {
            if (workers.get(0).getRandBuilding() instanceof Ride) {
                Ride ride = (Ride) buildings.get(modelTiles[workers.get(0).getRandBuilding().getIndexes().get(0).x][workers.get(0).getRandBuilding().getIndexes().get(0).y].getIndex());
                ride.changeState(BuildingState.NEED_TO_REPAIR);
                buildings.set(modelTiles[workers.get(0).getRandBuilding().getIndexes().get(0).x][workers.get(0).getRandBuilding().getIndexes().get(0).y].getIndex(), ride);
            }
            tiles[workers.get(0).i][workers.get(0).j].remove(workers.get(0));
            tiles[workers.get(0).i][workers.get(0).j].repaint();
            workers.remove(0);
        }
        System.out.print(workers.size());
    }

    /**
     * Gives back the given building indexes.
     *
     * @param tiles
     * @param name
     * @param h, height
     * @param l, length
     */
    public int[] findBuilding(ModelTile[][] tiles, Building name, int h, int l) {
        int arr[] = new int[2];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (name.getIndexes().get(0).x == i && name.getIndexes().get(0).y == j) {
                    if (h == 1 && l == 1) {
                        arr[0] = i;
                        arr[1] = j;
                    }
                    if (h == 1 && l == 2) {
                        arr[0] = i;
                        arr[1] = j;
                    }
                    if (h == 2 && l == 1) {
                        arr[0] = i;
                        arr[1] = j;
                    }
                    if (h == 2 && l == 2) {
                        arr[0] = i;
                        arr[1] = j;
                    }
                }
            }
        }
        int retval[] = new int[2];
        for (int k = 0; k <= h - 1; k++) {
            for (int j = 0; j <= l - 1; j++) {
                if (k == 0 && arr[0] != 0) {
                    if ("road".equals(modelTiles[arr[0] + k - 1][arr[1] + j].getType())) {
                        retval[0] = arr[0] + k;
                        retval[1] = arr[1] + j;
                    }
                }
                if (k == h - 1 && arr[0] + k + 1 <= height - 1) {
                    if ("road".equals(modelTiles[arr[0] + k + 1][arr[1] + j].getType())) {
                        retval[0] = arr[0] + k;
                        retval[1] = arr[1] + j;
                    }
                }
                if (j == 0 && arr[1] != 0) {
                    if ("road".equals(modelTiles[arr[0] + k][arr[1] + j - 1].getType())) {
                        retval[0] = arr[0] + k;
                        retval[1] = arr[1] + j;
                    }
                }
                if (j == l - 1 && arr[1] + j + 1 <= width - 1) {
                    if ("road".equals(modelTiles[arr[0] + k][arr[1] + j + 1].getType())) {
                        retval[0] = arr[0] + k;
                        retval[1] = arr[1] + j;
                    }
                }
            }
        }
        return retval;
    }

    /**
     * Gives back a random road
     *
     * @param worker, binds the road to this worker
     */
    private void getRandomRoad(Worker worker) {
        Random rand = new Random();
        do {
            do {
                worker.randBuilding = buildings.get(rand.nextInt(buildings.size()));
            } while (worker.randBuilding == null);
        } while (!(worker.randBuilding instanceof Road) || (worker.prevBuild[0] == worker.randBuilding.getIndexes().get(0).x
                && worker.prevBuild[1] == worker.randBuilding.getIndexes().get(0).y));
        worker.prevBuild[0] = worker.randBuilding.getIndexes().get(0).x;
        worker.prevBuild[1] = worker.randBuilding.getIndexes().get(0).y;
    }

    /**
     * Gives back a random building
     *
     * @param worker, binds the building to this worker
     */
    private void getRandomBuild(Worker worker) {
        for (int i = 0; i < buildings.size(); i++) {
            if (buildings.get(i) instanceof Ride) {
                Ride ride = (Ride) buildings.get(i);
                if (ride.getCurrentState().equals(BuildingState.NEED_TO_REPAIR)) {
                    ride.changeState(BuildingState.REPAIRING);
                    buildings.set(i, ride);
                    worker.randBuilding = ride;
                    break;
                }
            }
        }
        if (worker.randBuilding == null) {
            Random rand = new Random();
            do {
                do {
                    worker.randBuilding = buildings.get(rand.nextInt(buildings.size()));
                } while (worker.randBuilding == null);
            } while (!(worker.randBuilding instanceof Road) || (worker.prevBuild[0] == worker.randBuilding.getIndexes().get(0).x
                    && worker.prevBuild[1] == worker.randBuilding.getIndexes().get(0).y
                    || worker.randBuilding.getIndexes().get(0).x == 22 && worker.randBuilding.getIndexes().get(0).y == 12));
            worker.prevBuild[0] = worker.randBuilding.getIndexes().get(0).x;
            worker.prevBuild[1] = worker.randBuilding.getIndexes().get(0).y;
        }
    }

    /**
     * Give back a random building from buildings arraylist.
     *
     * @param visitor
     */
    private void getRandomElement(Visitor visitor) {
        Random rand = new Random();
        do {
            do {
                visitor.randBuilding = buildings.get(rand.nextInt(buildings.size()));
            } while (visitor.randBuilding == null);
        } while ((visitor.getHunger() < 70 ? visitor.randBuilding.getBUILDING_COST() == 155 : visitor.randBuilding.getBUILDING_COST() != 155)
                || visitor.randBuilding.getBUILDING_COST() < 60
                || (visitor.prevBuild[0] == visitor.randBuilding.getIndexes().get(0).x
                && visitor.prevBuild[1] == visitor.randBuilding.getIndexes().get(0).y));
        visitor.prevBuild[0] = visitor.randBuilding.getIndexes().get(0).x;
        visitor.prevBuild[1] = visitor.randBuilding.getIndexes().get(0).y;
    }

    /**
     * Timer that moves the workers
     */
    private class workerTimer implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            for (Worker worker : workers) {
                if (worker instanceof Cleaner) {
                    Road i = (Road) buildings.get(modelTiles[worker.i][worker.j].getIndex());
                    boolean x = i.hasTrashOnIt();
                    if (x) {
                        i.setTrashOnIt(false);
                        try {
                            tiles[worker.i][worker.j].setImage(ResourceLoader.loadImage("res/road.png"));
                        } catch (IOException ex) {
                            Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    tiles[worker.i][worker.j].remove(worker);
                    tiles[worker.i][worker.j].repaint();

                    int[] buildingCoordinates = new int[2];
                    buildingCoordinates[0] = (int) worker.randBuilding.getIndexes().get(0).getX();
                    buildingCoordinates[1] = (int) worker.randBuilding.getIndexes().get(0).getY();

                    hm.forEach((k, Pvalue) -> {
                        if (Pvalue.get(0).equals(worker.i) && Pvalue.get(1).equals(worker.j)) {
                            worker.source = k;
                        }
                    });

                    hm.forEach((k, Pvalue) -> {
                        if (Pvalue.get(0).equals(buildingCoordinates[0]) && Pvalue.get(1).equals(buildingCoordinates[1])) {
                            worker.dest = k;
                        }
                    });

                    if (buildingCoordinates[0] != 0 && worker.arrived) {
                        worker.printShortestDistance(graph, worker.source, worker.dest, v);
                        worker.arrived = false;
                    }
                    if (!worker.arrived) {
                        if ((worker.path.size() - 1 - worker.pathIndex) >= 0) {
                            worker.posVis = worker.path.get(worker.path.size() - 1 - worker.pathIndex);
                            worker.i = hm.get(worker.posVis).get(0);
                            worker.j = hm.get(worker.posVis).get(1);
                            worker.pathIndex++;
                        }
                    }

                    if (worker.source == worker.dest) {
                        worker.pathIndex = 0;
                        worker.posVis = 0;
                        worker.arrived = true;
                        tiles[worker.i][worker.j].add(worker);
                        tiles[worker.i][worker.j].repaint();
                        getRandomRoad(worker);
                        worker.path.clear();
                    } else {
                        tiles[worker.i][worker.j].add(worker);
                        tiles[worker.i][worker.j].repaint();
                    }
                } else { // mechanic
                    tiles[worker.i][worker.j].remove(worker);
                    tiles[worker.i][worker.j].repaint();

                    int[] buildingCoordinates = new int[2];
                    buildingCoordinates[0] = (int) worker.randBuilding.getIndexes().get(0).getX();
                    buildingCoordinates[1] = (int) worker.randBuilding.getIndexes().get(0).getY();

                    hm.forEach((k, Pvalue) -> {
                        if (Pvalue.get(0).equals(worker.i) && Pvalue.get(1).equals(worker.j)) {
                            worker.source = k;
                        }
                    });

                    hm.forEach((k, Pvalue) -> {
                        if (Pvalue.get(0).equals(buildingCoordinates[0]) && Pvalue.get(1).equals(buildingCoordinates[1])) {
                            worker.dest = k;
                        }
                    });

                    if (buildingCoordinates[0] != 0 && worker.arrived) {
                        worker.printShortestDistance(graph, worker.source, worker.dest, v);
                        worker.arrived = false;
                    }
                    if (!worker.arrived) {
                        if ((worker.path.size() - 1 - worker.pathIndex) >= 0) {
                            worker.posVis = worker.path.get(worker.path.size() - 1 - worker.pathIndex);
                            worker.i = hm.get(worker.posVis).get(0);
                            worker.j = hm.get(worker.posVis).get(1);
                            worker.pathIndex++;
                        }
                    }

                    if (worker.source == worker.dest) {
                        tiles[worker.i][worker.j].add(worker);
                        tiles[worker.i][worker.j].repaint();

                        if (buildings.get(modelTiles[worker.i][worker.j].getIndex()) instanceof Ride) {
                            Ride ri = (Ride) buildings.get(modelTiles[worker.i][worker.j].getIndex());
                            ri.changeState(BuildingState.ACTIVE);
                            buildings.set(modelTiles[worker.i][worker.j].getIndex(), ri);

                            for (Point p : ri.getIndexes()) {
                                try {
                                    tiles[p.x][p.y].setImage(ResourceLoader.loadImage("res/" + ri.getDetails().image));
                                    tiles[p.x][p.y].repaint();
                                } catch (IOException ex) {
                                    Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            worker.pathIndex = 0;
                            worker.posVis = 0;
                            worker.arrived = true;
                            getRandomBuild(worker);
                            worker.path.clear();
                        } else {
                            worker.pathIndex = 0;
                            worker.posVis = 0;
                            worker.arrived = true;
                            getRandomBuild(worker);
                            worker.path.clear();
                        }

                    } else {
                        tiles[worker.i][worker.j].add(worker);
                        tiles[worker.i][worker.j].repaint();
                    }
                }

            }
        }
    }

    /**
     * The visitors moved by this timer. Szélére ne építs utat!
     */
    private class visitorTimer implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            ArrayList<Visitor> found = new ArrayList<Visitor>();
            ArrayList<Integer> toActivateInd = new ArrayList<Integer>();
            for (Visitor visitor : visitors) {
                if (visitor.tilesUntillTrash > 1 && "road".equals(modelTiles[visitor.i][visitor.j].getType())) {
                    visitor.tilesUntillTrash = visitor.tilesUntillTrash - 1;
                } else if (visitor.tilesUntillTrash == 1 && "road".equals(modelTiles[visitor.i][visitor.j].getType())) {
                    if (!"trash_bin".equals(modelTiles[visitor.i + 1][visitor.j].getType())
                            && !"trash_bin".equals(modelTiles[visitor.i - 1][visitor.j].getType())
                            && !"trash_bin".equals(modelTiles[visitor.i][visitor.j + 1].getType())
                            && !"trash_bin".equals(modelTiles[visitor.i][visitor.j - 1].getType())) {
                        Road temp = (Road) buildings.get(modelTiles[visitor.i][visitor.j].getIndex());
                        temp.setTrashOnIt(true);
                        buildings.set(modelTiles[visitor.i][visitor.j].getIndex(), temp);
                        visitor.tilesUntillTrash = visitor.tilesUntillTrash - 1;
                        try {
                            tiles[visitor.i][visitor.j].setImage(ResourceLoader.loadImage("res/garbage.png"));
                        } catch (IOException ex) {
                            Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                tiles[visitor.i][visitor.j].remove(visitor);
                tiles[visitor.i][visitor.j].repaint();

                int[] buildingCoordinates = findBuilding(modelTiles, visitor.randBuilding, visitor.randBuilding.getDetails().height, visitor.randBuilding.getDetails().length);

                hm.forEach((k, Pvalue) -> {
                    if (Pvalue.get(0).equals(visitor.i) && Pvalue.get(1).equals(visitor.j)) {
                        visitor.source = k;
                    }
                });

                hm.forEach((k, Pvalue) -> {
                    if (Pvalue.get(0).equals(buildingCoordinates[0]) && Pvalue.get(1).equals(buildingCoordinates[1])) {
                        visitor.dest = k;
                    }
                });

                if (buildingCoordinates[0] != 0 && visitor.arrived) {
                    if (visitor.getHappiness() < 35) {
                        visitor.printShortestDistance(graph, visitor.source, 0, v);
                        visitor.leaving = true;
                    } else {
                        visitor.printShortestDistance(graph, visitor.source, visitor.dest, v);
                    }

                    visitor.arrived = false;
                }
                if (!visitor.arrived) {
                    if ((visitor.path.size() - 1 - visitor.pathIndex) >= 0) {
                        int siVsop = visitor.path.get(visitor.path.size() - 1 - visitor.pathIndex);
                        ii = hm.get(siVsop).get(0);
                        jj = hm.get(siVsop).get(1);
                        hm.forEach((k, Pvalue) -> {
                            if (Pvalue.get(0).equals(ii) && Pvalue.get(1).equals(jj)) {
                                keresett = k;
                            }
                        });
                        if (keresett == visitor.dest) {
                            if (buildings.get(modelTiles[(int) visitor.randBuilding.getIndexes().get(0).getX()][(int) visitor.randBuilding.getIndexes().get(0).getY()]
                                    .getIndex()) instanceof Ride) {
                                Ride ride = (Ride) buildings.get(modelTiles[(int) visitor.randBuilding.getIndexes().get(0).getX()][(int) visitor.randBuilding.getIndexes().get(0).getY()]
                                        .getIndex());
                                if (ride.getCurrentState() == BuildingState.ACTIVE) {
                                    visitor.posVis = visitor.path.get(visitor.path.size() - 1 - visitor.pathIndex);
                                    visitor.i = hm.get(visitor.posVis).get(0);
                                    visitor.j = hm.get(visitor.posVis).get(1);
                                    visitor.pathIndex++;

                                    ride.incCurrentVisitors();

                                    if (ride.getCurrentVisitors() == ride.getMaxVisitors()
                                            && toActivateInd.contains(modelTiles[visitor.i][visitor.j].getIndex()) == false
                                            && activateInd.contains(modelTiles[visitor.i][visitor.j].getIndex()) == false) {
                                        ride.changeState(BuildingState.IN_USE);
                                        toActivateInd.add(modelTiles[visitor.i][visitor.j].getIndex());

                                        hm.forEach((k, Pvalue) -> {
                                            if (Pvalue.get(0).equals(visitor.i) && Pvalue.get(1).equals(visitor.j)) {
                                                visitor.source = k;
                                            }
                                        });
                                    } else {
                                        hm.forEach((k, Pvalue) -> {
                                            if (Pvalue.get(0).equals(visitor.i) && Pvalue.get(1).equals(visitor.j)) {
                                                visitor.source = k;
                                            }
                                        });
                                    }
                                    buildings.set(modelTiles[visitor.i][visitor.j].getIndex(), ride);
                                }
                            } else if (buildings.get(modelTiles[(int) visitor.randBuilding.getIndexes().get(0).getX()][(int) visitor.randBuilding.getIndexes().get(0).getY()]
                                    .getIndex()) instanceof Restaurant) {
                                Restaurant restaurant = (Restaurant) buildings.get(modelTiles[(int) visitor.randBuilding.getIndexes().get(0).getX()][(int) visitor.randBuilding.getIndexes().get(0).getY()]
                                        .getIndex());
                                if (restaurant.getCurrentState() == BuildingState.ACTIVE) {
                                    visitor.posVis = visitor.path.get(visitor.path.size() - 1 - visitor.pathIndex);
                                    visitor.i = hm.get(visitor.posVis).get(0);
                                    visitor.j = hm.get(visitor.posVis).get(1);
                                    visitor.pathIndex++;

                                    restaurant.changeState(BuildingState.IN_USE);
                                    toActivateInd.add(modelTiles[visitor.i][visitor.j].getIndex());

                                    buildings.set(modelTiles[visitor.i][visitor.j].getIndex(), restaurant);
                                }
                            } else if (buildings.get(modelTiles[(int) visitor.randBuilding.getIndexes().get(0).getX()][(int) visitor.randBuilding.getIndexes().get(0).getY()]
                                    .getIndex()) instanceof Toilet) {
                                Toilet toilet = (Toilet) buildings.get(modelTiles[(int) visitor.randBuilding.getIndexes().get(0).getX()][(int) visitor.randBuilding.getIndexes().get(0).getY()]
                                        .getIndex());
                                if (toilet.getCurrentState() == BuildingState.ACTIVE) {
                                    visitor.posVis = visitor.path.get(visitor.path.size() - 1 - visitor.pathIndex);
                                    visitor.i = hm.get(visitor.posVis).get(0);
                                    visitor.j = hm.get(visitor.posVis).get(1);
                                    visitor.pathIndex++;

                                    toilet.changeState(BuildingState.IN_USE);
                                    toActivateInd.add(modelTiles[visitor.i][visitor.j].getIndex());

                                    buildings.set(modelTiles[visitor.i][visitor.j].getIndex(), toilet);
                                }
                            }
                        } else {
                            visitor.posVis = visitor.path.get(visitor.path.size() - 1 - visitor.pathIndex);
                            visitor.i = hm.get(visitor.posVis).get(0);
                            visitor.j = hm.get(visitor.posVis).get(1);
                            visitor.pathIndex++;
                        }
                    }
                }
            }

            for (int i = 0; i < buildings.size(); i++) {
                if (buildings.get(i) instanceof Ride) {
                    Ride ride = (Ride) buildings.get(i);
                    if (ride.getCurrentVisitors() != 0
                            && ride.getCurrentVisitors() != ride.getMaxVisitors()
                            && ride.getTurnsTillStart() <= 0
                            && toActivateInd.contains(i) == false
                            && activateInd.contains(i) == false) {
                        ride.changeState(BuildingState.IN_USE);
                        toActivateInd.add(i);
                        buildings.set(i, ride);
                    }
                }
            }

            for (Visitor visitorAgain : visitors) {
                if (buildings.get(modelTiles[(int) visitorAgain.randBuilding.getIndexes().get(0).getX()][(int) visitorAgain.randBuilding.getIndexes().get(0).getY()]
                        .getIndex()) instanceof Ride) {
                    Ride ride = (Ride) buildings.get(modelTiles[(int) visitorAgain.randBuilding.getIndexes().get(0).getX()][(int) visitorAgain.randBuilding.getIndexes().get(0).getY()]
                            .getIndex());
                    if (visitorAgain.source == visitorAgain.dest && ride.getCurrentState() == BuildingState.ACTIVE) {
                        ride.decTurnsTillStart();
                        buildings.set(modelTiles[visitorAgain.i][visitorAgain.j].getIndex(), ride);
                    }
                    if (visitorAgain.source == visitorAgain.dest && ride.getCurrentState() == BuildingState.IN_USE) {
                        if (visitorAgain.getHunger() > 70) {
                            visitorAgain.eatSomething();
                        }
                        visitorAgain.pathIndex = 0;
                        visitorAgain.posVis = 0;
                        visitorAgain.arrived = true;
                        tiles[visitorAgain.i][visitorAgain.j].remove(visitorAgain);
                        tiles[visitorAgain.i][visitorAgain.j].repaint();
                        getRandomElement(visitorAgain);
                        visitorAgain.path.clear();
                        visitorAgain.changeHappiness(10 - (10 * visitorAgain.getHunger() / 100));

                        if (!freeGames.contains(modelTiles[visitorAgain.i][visitorAgain.j].getType())) {
                            visitorAgain.useRide(payment.getGamesFee());
                            money = money + payment.getGamesFee();
                        }

                        if ("buffet".equals(modelTiles[visitorAgain.i][visitorAgain.j].getType())
                                || "restaurant".equals(modelTiles[visitorAgain.i][visitorAgain.j].getType())) {
                            visitorAgain.tilesUntillTrash = 5;
                        }
                    } else {
                        tiles[visitorAgain.i][visitorAgain.j].add(visitorAgain);
                        tiles[visitorAgain.i][visitorAgain.j].repaint();
                    }
                } else {
                    if (visitorAgain.source == visitorAgain.dest) {
                        if (visitorAgain.getHunger() > 70) {
                            visitorAgain.eatSomething();
                        }
                        visitorAgain.pathIndex = 0;
                        visitorAgain.posVis = 0;
                        visitorAgain.arrived = true;
                        tiles[visitorAgain.i][visitorAgain.j].remove(visitorAgain);
                        tiles[visitorAgain.i][visitorAgain.j].repaint();
                        getRandomElement(visitorAgain);
                        visitorAgain.path.clear();
                        visitorAgain.changeHappiness(10 - (10 * visitorAgain.getHunger() / 100));

                        if (!freeGames.contains(modelTiles[visitorAgain.i][visitorAgain.j].getType())) {
                            visitorAgain.useRide(payment.getGamesFee());
                            money = money + payment.getGamesFee();
                        }

                        if ("buffet".equals(modelTiles[visitorAgain.i][visitorAgain.j].getType())
                                || "restaurant".equals(modelTiles[visitorAgain.i][visitorAgain.j].getType())) {
                            visitorAgain.tilesUntillTrash = 5;
                        }
                    } else {
                        tiles[visitorAgain.i][visitorAgain.j].add(visitorAgain);
                        tiles[visitorAgain.i][visitorAgain.j].repaint();
                    }
                }

                if (visitorAgain.leaving && visitorAgain.source == 0) {
                    found.add(visitorAgain);
                    tiles[visitorAgain.i][visitorAgain.j].remove(visitorAgain);
                    tiles[visitorAgain.i][visitorAgain.j].repaint();
                }
            }

            visitors.removeAll(found);
            found.clear();

            for (Integer ind : activateInd) {
                if (buildings.get(ind) instanceof Ride) {
                    Ride ride = (Ride) buildings.get(ind);
                    Random rand = new Random();
                    int rand_int = rand.nextInt(100);
                    if (rand_int < 30) {
                        ride.changeState(BuildingState.NEED_TO_REPAIR);
                        for (Point p : ride.getIndexes()) {
                            try {
                                tiles[p.x][p.y].setImage(ResourceLoader.loadImage("res/construction.png"));
                                tiles[p.x][p.y].repaint();
                            } catch (IOException ex) {
                                Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } else {
                        ride.changeState(BuildingState.ACTIVE);
                    }
                    ride.setCurrentVisitors(0);
                    ride.setTurnsTillStart(10);
                    buildings.set(ind, ride);
                } else if (buildings.get(ind) instanceof Restaurant) {
                    Restaurant restaurant = (Restaurant) buildings.get(ind);
                    restaurant.changeState(BuildingState.ACTIVE);
                    buildings.set(ind, restaurant);
                } else if (buildings.get(ind) instanceof Toilet) {
                    Toilet toilet = (Toilet) buildings.get(ind);
                    toilet.changeState(BuildingState.ACTIVE);
                    buildings.set(ind, toilet);
                }
            }
            activateInd.clear();
            for (Integer ind : toActivateInd) {
                activateInd.add(ind);
            }
            toActivateInd.clear();
        }

    }

    /**
     * Nested class for event handling
     */
    class mouseListener extends MouseAdapter {

        int iSubstitute, jSubstitute;

        public mouseListener(int iSubstitute, int jSubstitute) {
            this.iSubstitute = iSubstitute;
            this.jSubstitute = jSubstitute;
        }

        /**
         * Handles the validation of building placement. Handles the validation
         * of a building destruction. Forbids the destruction of the spawning
         * road.
         *
         * @param e
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            if (destroy && !"grass".equals(modelTiles[iSubstitute][jSubstitute].getType())) {
                if (iSubstitute != 22 || jSubstitute != 12) {
                    removeBuilding(iSubstitute, jSubstitute);
                }
            } else if (building != null) {

                if (money - building.getBUILDING_COST() >= 0) {
                    if (iSubstitute + building.getDetails().height < height + 1
                            && jSubstitute + building.getDetails().length < width + 1) {

                        boolean free = true;
                        for (int k = 0; k <= building.getDetails().height - 1; k++) {
                            for (int l = 0; l <= building.getDetails().length - 1; l++) {
                                if (!"grass".equals(modelTiles[iSubstitute + k][jSubstitute + l].getType())) {
                                    free = false;
                                }
                            }
                        }

                        if (free && nearRoad(iSubstitute, jSubstitute)) {
                            try {
                                createBuilding(iSubstitute, jSubstitute);
                            } catch (IOException ex) {
                                Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            money -= building.getBUILDING_COST();
                        }
                    }
                }
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (building != null) {
                try {
                    visualizePlacing(iSubstitute, jSubstitute, ResourceLoader.loadImage("res/placeHolder.png"));
                } catch (IOException ex) {
                    Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                tiles[iSubstitute][jSubstitute].setBorder(BorderFactory.createLineBorder(Color.black));
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (building != null) {
                try {
                    visualizePlacing(iSubstitute, jSubstitute, ResourceLoader.loadImage("res/grass.png"));
                } catch (IOException ex) {
                    Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                tiles[iSubstitute][jSubstitute].setBorder(BorderFactory.createEmptyBorder());
            }
        }
    }

    /**
     * Paints tiles accordingly to the mouseevent and the size of the building
     * that we want to place down.
     *
     * @param iSubstitute, i index of matrixes.
     * @param jSubstitute, j index of matrixes.
     * @param image, grass.png or placeHolder.png, depending on the mouseevent.
     */
    public void visualizePlacing(int iSubstitute, int jSubstitute, Image image) {
        for (int k = 0; k <= building.getDetails().height - 1; k++) {
            for (int l = 0; l <= building.getDetails().length - 1; l++) {
                if (iSubstitute + k < height && jSubstitute + l < width) {
                    if ("grass".equals(modelTiles[iSubstitute + k][jSubstitute + l].getType())) {
                        tiles[iSubstitute + k][jSubstitute + l].setImage(image);
                        tiles[iSubstitute + k][jSubstitute + l].repaint();
                    }
                }
            }
        }
    }

    /**
     * Validates if there is a road next to the tiles where we want to build.
     * True, if there is a road, false if there isnt.
     *
     * @param iSubstitute, i index of matrixes.
     * @param jSubstitute, j index of matrixes.
     * @return
     */
    public boolean nearRoad(int iSubstitute, int jSubstitute) {
        for (int k = 0; k <= building.getDetails().height - 1; k++) {
            for (int l = 0; l <= building.getDetails().length - 1; l++) {
                if (k == 0 && iSubstitute != 0) {
                    if ("road".equals(modelTiles[iSubstitute + k - 1][jSubstitute + l].getType())) {
                        return true;
                    }
                }
                if (k == building.getDetails().height - 1 && iSubstitute + k + 1 <= height - 1) {
                    if ("road".equals(modelTiles[iSubstitute + k + 1][jSubstitute + l].getType())) {
                        return true;
                    }
                }
                if (l == 0 && jSubstitute != 0) {
                    if ("road".equals(modelTiles[iSubstitute + k][jSubstitute + l - 1].getType())) {
                        return true;
                    }
                }
                if (l == building.getDetails().length - 1 && jSubstitute + l + 1 <= width - 1) {
                    if ("road".equals(modelTiles[iSubstitute + k][jSubstitute + l + 1].getType())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public void setDestroy(boolean destroy) {
        this.destroy = destroy;
    }

    public Building getBuilding() {
        return building;
    }

    public boolean isDestroy() {
        return destroy;
    }

    public Payment getPayment() {
        return payment;
    }

    public int getMoney() {
        return money;
    }

    public int getVisitorsCount() {
        return visitors.size();
    }

    public int getAvgHappiness() {
        if (visitors.isEmpty()) {
            return 100;
        }
        int sum = 0;
        for (Visitor visitor : visitors) {
            sum += visitor.getHappiness();
        }
        return sum / visitors.size();
    }

    public int getParkValue() {
        int sum = 0;
        for (Building build : buildings) {
            if (build != null) {
                sum += build.getBUILDING_COST();
            }
        }
        return sum;
    }

    public ArrayList<String> getFreeGames() {
        return freeGames;
    }

    /**
     * Function to form edge between two vertices, source and dest.
     *
     * @param adj, arraylist
     * @param i, heigth index
     * @param j, length index
     */
    private static void addEdge(ArrayList<ArrayList<Integer>> adj, int i, int j) {
        adj.get(i).add(j);
        adj.get(j).add(i);
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public ArrayList<Worker> getWorkers() {
        return workers;
    }

    public void setWorkers(ArrayList<Worker> workers) {
        this.workers = workers;
    }

    public ArrayList<Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(ArrayList<Building> buildings) {
        this.buildings = buildings;
    }

    public ArrayList<Visitor> getVisitors() {
        return visitors;
    }

    public void setVisitors(ArrayList<Visitor> visitors) {
        this.visitors = visitors;
    }

    public ModelTile[][] getModelTiles() {
        return modelTiles;
    }

    public void setModelTiles(ModelTile[][] modelTiles) {
        this.modelTiles = modelTiles;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public void setTiles(Tile[][] tiles) {
        this.tiles = tiles;
    }

    public Hashtable<Integer, ArrayList<Integer>> getHm() {
        return hm;
    }

    public void setHm(Hashtable<Integer, ArrayList<Integer>> hm) {
        this.hm = hm;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public ArrayList<ArrayList<Integer>> getGraph() {
        return graph;
    }

    public void setGraph(ArrayList<ArrayList<Integer>> graph) {
        this.graph = graph;
    }

    public int getParentRoadKey() {
        return parentRoadKey;
    }

    public void setParentRoadKey(int parentRoadKey) {
        this.parentRoadKey = parentRoadKey;
    }

    public int getParentRoadI() {
        return parentRoadI;
    }

    public void setParentRoadI(int parentRoadI) {
        this.parentRoadI = parentRoadI;
    }

    public int getParentRoadJ() {
        return parentRoadJ;
    }

    public void setParentRoadJ(int parentRoadJ) {
        this.parentRoadJ = parentRoadJ;
    }

    public boolean isIsOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public int getHmDeleteIndex() {
        return hmDeleteIndex;
    }

    public void setHmDeleteIndex(int hmDeleteIndex) {
        this.hmDeleteIndex = hmDeleteIndex;
    }

    public Timer getT() {
        return t;
    }

    public void setT(Timer t) {
        this.t = t;
    }

    public Timer getArrival() {
        return arrival;
    }

    public void setArrival(Timer arrival) {
        this.arrival = arrival;
    }

    public boolean isEdge() {
        return edge;
    }

    public void setEdge(boolean edge) {
        this.edge = edge;
    }

    public Timer getwT() {
        return wT;
    }

    public void setwT(Timer wT) {
        this.wT = wT;
    }

    public int getIi() {
        return ii;
    }

    public void setIi(int ii) {
        this.ii = ii;
    }

    public int getJj() {
        return jj;
    }

    public void setJj(int jj) {
        this.jj = jj;
    }

    public int getKeresett() {
        return keresett;
    }

    public void setKeresett(int keresett) {
        this.keresett = keresett;
    }

}
