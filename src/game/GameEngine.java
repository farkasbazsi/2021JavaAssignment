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
import model.building.Building;
import model.Visitor;
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

    private ModelTile[][] modelTiles;
    private Tile[][] tiles;
    private final int height = 23;
    private final int width = 25;
    private Building building;
    private boolean destroy;

    private Hashtable<Integer, ArrayList<Integer>> hm = new Hashtable<>();
    private int v = 1;
    ArrayList<ArrayList<Integer>> graph
            = new ArrayList<ArrayList<Integer>>();
    int parentRoadKey = 0;
    int parentRoadI = 0;
    int parentRoadJ = 0;

    private boolean isOpen = false;

    int hmDeleteIndex;
    Timer t = new Timer(700, new visitorTimer());
    Timer arrival = new Timer(5000, new arrivalTimer());

    private boolean edge = false;

    //gets called after the centerPanel in FfnProject.java
    public GameEngine(JPanel panel, Building spawnRoad) throws IOException {
        this.money = 10000;
        buildings = new ArrayList<>();
        tiles = new Tile[height][width];
        modelTiles = new ModelTile[height][width];
        building = null;
        destroy = false;

        generateField(panel, spawnRoad);
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
                    tiles[i][j] = new Tile(ResourceLoader.loadImage("res/grass.png"));
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
    }

    /**
     * Insert new visitor into the park.
     *
     * @throws IOException
     */
    private void newVisitor() throws IOException {
        Details dt = new Details("res/happy_man.png", 10, 10);
        BufferedImage icon = (BufferedImage) ResourceLoader.loadImage("res/happy_man.png");

        Visitor visitor = new Visitor(dt, icon);
        tiles[22][12].add(visitor);
        visitors.add(visitor);
        t.start();
        arrival.start();
        getRandomElement(visitor);
        
        //a belépődíjból lejön a fenntartási költség (kezdetleges)
        int sum = 0;
        for(Building b : buildings){
            if(b !=null && !(b instanceof Road)) {sum+=(int)b.getBUILDING_COST()/20;}}
        money += payment.getEntranceFee()-sum;
    }

    /**
     * Insert new visitor in every five seconds, until the visitors number
     * less than 5.
     *
     */
    private class arrivalTimer implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (visitors.size() < 5) {
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
    private void createBuilding(int iSubstitute, int jSubstitute) {
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

                insertRoadToGraph(iSubstitute, jSubstitute);

            } else if (building instanceof Plant) {
                buildings.add(new Plant((Plant) building));
            } else {
                System.err.println("Can't find class");
            }
            ind = buildings.size() - 1;
        }
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

    /**
     * When the player builds one tile road, this function
     * insert a new node to the graph. The new node represents
     * the road with a hashtable.
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
            graph.add(new ArrayList<Integer>());
            graph.add(new ArrayList<Integer>());
            addEdge(graph, 0, v);
        } else {
            graph.add(new ArrayList<Integer>());
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
        //System.out.println(graph);
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
        /*for (Point i : buildings.get(modelTiles[iSubstitute][jSubstitute].getIndex()).getIndexes()) {
                        System.out.println(i.toString() + "\n");
                    }*/
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

    private void newGame() {

    }

    private void exit() {

    }

    private void newWorker() {

    }

    private void removeWorker(int id) {

    }

    public void printModel(ModelTile[][] tiles) {
        for (ModelTile[] tile : tiles) {
            for (ModelTile modelTile : tile) {
                if (!modelTile.getType().equals("grass")) {
                    System.out.println(modelTile.getType());
                }
            }
        }
    }

    /**
     * Gives back the given building indexes.
     *
     * @param tiles
     * @param name
     * @param h, heigth
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
        } while (visitor.randBuilding.getBUILDING_COST() < 60 || (visitor.prevBuild[0] == visitor.randBuilding.getIndexes().get(0).x
                && visitor.prevBuild[1] == visitor.randBuilding.getIndexes().get(0).y));
        visitor.prevBuild[0] = visitor.randBuilding.getIndexes().get(0).x;
        visitor.prevBuild[1] = visitor.randBuilding.getIndexes().get(0).y;
    }

    /**
     * The visitors moved by this timer.
     */
    private class visitorTimer implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            for (Visitor visitor : visitors) {
                tiles[visitor.i][visitor.j].remove(visitor);
                tiles[visitor.i][visitor.j].repaint();

                int[] buildingCoordinates = findBuilding(modelTiles, visitor.randBuilding, visitor.randBuilding.getDetails().height, visitor.randBuilding.getDetails().length);
                //System.out.println(buildingCoordinates[0] + " " + buildingCoordinates[1]);
                //System.out.println(graph);
                //System.out.println(randBuilding.getName());

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
                    //path.clear();
                    visitor.printShortestDistance(graph, visitor.source, visitor.dest, v);
                    //System.out.println(randBuilding.getName());
                    //System.out.println(graph);
                    //System.out.println(dest);
                    visitor.arrived = false;
                }
                if (!visitor.arrived) {
                    if ((visitor.path.size() - 1 - visitor.pathIndex) >= 0) {
                        visitor.posVis = visitor.path.get(visitor.path.size() - 1 - visitor.pathIndex);
                        visitor.i = hm.get(visitor.posVis).get(0);
                        visitor.j = hm.get(visitor.posVis).get(1);
                        visitor.pathIndex++;
                    }
                }

                if (visitor.source == visitor.dest) {
                    visitor.pathIndex = 0;
                    visitor.posVis = 0;
                    visitor.arrived = true;
                    tiles[visitor.i][visitor.j].remove(visitor);
                    //várni kellene egy kicsit maybe
                    tiles[visitor.i][visitor.j].repaint();
                    getRandomElement(visitor);
                    visitor.path.clear();
                    visitor.changeHappiness(10-(10*visitor.getHunger()/100));
                    visitor.useRide(payment.getGamesFee());
                } else {
                    tiles[visitor.i][visitor.j].add(visitor);
                    tiles[visitor.i][visitor.j].repaint();
                }
                /*
            if (edge) {
                getRandomElement();
                edge = false;
            }
                 */
            }

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
            /*for(Building i : buildings){
                if(i!=null){
                    System.out.println(i.getName()+"\n");
                }else{
                    System.out.println("NULL\n");
                }
            }
            System.out.println("----------");*/
            //System.out.println(modelTiles[iSubstitute][jSubstitute].getIndex() + " " + modelTiles[iSubstitute][jSubstitute].getType());
            if (destroy && !"grass".equals(modelTiles[iSubstitute][jSubstitute].getType())) {
                if (iSubstitute != 22 || jSubstitute != 12) {
                    removeBuilding(iSubstitute, jSubstitute);
                }
            } else if (building != null) {

                if (money - building.getBUILDING_COST() >= 0) {
                    if (iSubstitute + building.getDetails().height < height + 1
                            && jSubstitute + building.getDetails().length < width + 1) {
                        //System.out.println(iSubstitute + " " + jSubstitute + " : " + buildingHeight + " " + buildingWidth);

                        boolean free = true;
                        for (int k = 0; k <= building.getDetails().height - 1; k++) {
                            for (int l = 0; l <= building.getDetails().length - 1; l++) {
                                if (!"grass".equals(modelTiles[iSubstitute + k][jSubstitute + l].getType())) {
                                    free = false;
                                }
                            }
                        }

                        if (free && nearRoad(iSubstitute, jSubstitute)) {
                            createBuilding(iSubstitute, jSubstitute);
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
                //if (iSubstitute + k < height + 1 && jSubstitute + l < width + 1) {
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

}
