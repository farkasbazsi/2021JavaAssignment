package game;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;
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
    private ArrayList<Building> buildings;
    private ArrayList<Visitor> visitors = new ArrayList<>();
    private Payment payment;

    private ModelTile[][] modelTiles;
    private Tile[][] tiles;
    private final int height = 23;
    private final int width = 25;
    private Building building;
    private boolean destroy;
    private Visitor visitor;

    private Hashtable<Integer, ArrayList<Integer>> hm = new Hashtable<>();
    private int v = 1;
    ArrayList<ArrayList<Integer>> graph
            = new ArrayList<ArrayList<Integer>>();
    int parentRoadKey = 0;
    int parentRoadI = 0;
    int parentRoadJ = 0;
    LinkedList<Integer> path = new LinkedList<Integer>();
    boolean arrived = true;
    private boolean isOpen = false;
    int posVis = 0;
    int pathIndex = 0;
    int hmDeleteIndex;
    Timer t = new Timer(1000, new visitorTimer());
    private Building randBuilding;
    private boolean edge = false;
    private int[] prevBuild = new int[2];

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
        // DEBUG
        // BEGIN

        // END
    }

    public void openPark() {
        isOpen = true;
        visitor = new Visitor();
        visitor.setBackground(Color.blue);
        tiles[22][12].add(visitor);
        t.start();
        getRandomElement();
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
                        arr[1] = j - 1;
                    }
                    if (h == 2 && l == 1) {
                        arr[0] = i - 1;
                        arr[1] = j;
                    }
                    if (h == 2 && l == 2) {
                        arr[0] = i - 1;
                        arr[1] = j - 1;
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

    private void getRandomElement() {
        Random rand = new Random();
        do {
            randBuilding = buildings.get(rand.nextInt(buildings.size()));
        } while (randBuilding.getBUILDING_COST() < 60 || (prevBuild[0] == randBuilding.getIndexes().get(0).x
                && prevBuild[1] == randBuilding.getIndexes().get(0).y));
        prevBuild[0] = randBuilding.getIndexes().get(0).x;
        prevBuild[1] = randBuilding.getIndexes().get(0).y;
    }

    private class visitorTimer implements ActionListener {

        int i, j;
        int source;
        int dest;

        public visitorTimer() {
            i = 22;
            j = 12;
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            //i -> fel-le
            //j -> jobbra-balra
            // meg kell nézni hol van a legközelebbi ilyen indexű épület
            // oda elmenni
            tiles[i][j].remove(visitor);
            tiles[i][j].repaint();
            
            int[] buildingCoordinates = findBuilding(modelTiles, randBuilding, randBuilding.getDetails().height, randBuilding.getDetails().length);
            //System.out.println(buildingCoordinates[0] + " " + buildingCoordinates[1]);
            //System.out.println(graph);
            //System.out.println(randBuilding.getName());

            hm.forEach((k, Pvalue) -> {
                if (Pvalue.get(0).equals(i) && Pvalue.get(1).equals(j)) {
                    source = k;
                }
            });

            hm.forEach((k, Pvalue) -> {
                if (Pvalue.get(0).equals(buildingCoordinates[0]) && Pvalue.get(1).equals(buildingCoordinates[1])) {
                    dest = k;
                }
            });

            if (buildingCoordinates[0] != 0 && arrived) {
                //path.clear();
                printShortestDistance(graph, source, dest, v);
                //System.out.println(randBuilding.getName());
                //System.out.println(graph);
                //System.out.println(path);
                arrived = false;
            }
            if (!arrived) {
                if ((path.size() - 1 - pathIndex) >= 0) {
                    posVis = path.get(path.size() - 1 - pathIndex);
                    i = hm.get(posVis).get(0);
                    j = hm.get(posVis).get(1);
                    pathIndex++;
                }
            }

            if (source == dest) {
                pathIndex = 0;
                posVis = 0;
                arrived = true;
                tiles[i][j].remove(visitor);
                tiles[i][j].repaint();
                getRandomElement();
                path.clear();
            } else {
                tiles[i][j].add(visitor);
                tiles[i][j].repaint();
            }
            if(edge) {
                getRandomElement();
                edge = false;
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

    // function to form edge between two vertices
    // source and dest
    private static void addEdge(ArrayList<ArrayList<Integer>> adj, int i, int j) {
        adj.get(i).add(j);
        adj.get(j).add(i);
    }

    // function to print the shortest distance and path
    // between source vertex and destination vertex
    private void printShortestDistance(
            ArrayList<ArrayList<Integer>> adj,
            int s, int dest, int v) {
        // predecessor[i] array stores predecessor of
        // i and distance array stores distance of i
        // from s
        int pred[] = new int[v];
        int dist[] = new int[v];

        if (BFS(adj, s, dest, v, pred, dist) == false) {
            //System.out.println("Given source and destination"
            //        + "are not connected");
            edge = true;
            return;
        }

        int crawl = dest;
        path.add(crawl);
        while (pred[crawl] != -1) {
            path.add(pred[crawl]);
            crawl = pred[crawl];
        }

        // Print distance
        //System.out.println("Shortest path length is: " + dist[dest]);

        // Print path
        //System.out.println("Path is ::");
        //for (int i = path.size() - 1; i >= 0; i--) {
        //    System.out.print(path.get(i) + " ");
        //}
        //path.clear();
    }

    // a modified version of BFS that stores predecessor
    // of each vertex in array pred
    // and its distance from source in array dist
    private static boolean BFS(ArrayList<ArrayList<Integer>> adj, int src,
            int dest, int v, int pred[], int dist[]) {
        // a queue to maintain queue of vertices whose
        // adjacency list is to be scanned as per normal
        // BFS algorithm using LinkedList of Integer type
        LinkedList<Integer> queue = new LinkedList<Integer>();

        // boolean array visited[] which stores the
        // information whether ith vertex is reached
        // at least once in the Breadth first search
        boolean visited[] = new boolean[v];

        // initially all vertices are unvisited
        // so v[i] for all i is false
        // and as no path is yet constructed
        // dist[i] for all i set to infinity
        for (int i = 0; i < v; i++) {
            visited[i] = false;
            dist[i] = Integer.MAX_VALUE;
            pred[i] = -1;
        }

        // now source is first to be visited and
        // distance from source to itself should be 0
        visited[src] = true;
        dist[src] = 0;
        queue.add(src);

        // bfs Algorithm
        while (!queue.isEmpty()) {
            int u = queue.remove();
            for (int i = 0; i < adj.get(u).size(); i++) {
                if (visited[adj.get(u).get(i)] == false) {
                    visited[adj.get(u).get(i)] = true;
                    dist[adj.get(u).get(i)] = dist[u] + 1;
                    pred[adj.get(u).get(i)] = u;
                    queue.add(adj.get(u).get(i));

                    // stopping condition (when we find
                    // our destination)
                    if (adj.get(u).get(i) == dest) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
