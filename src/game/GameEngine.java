package game;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
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
                    tiles[i][j] = new Tile(ResourceLoader.loadImage("res/road.png"));
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
     * Gets called, if the placement is correct (no overlapping, no
     * outindexing). Searches in the buildings arrayList for a null element, if
     * there is none, adds a newly created element accordingly to the type. (if
     * there is a null element, overwrites the given element). Sets the
     * types(dojo, etc) and indexes(buildings arrayList index, showing the
     * building itself). Changes the pictures of the correct tiles in the Tiles
     * matrix & repaints them.
     *
     * @param iSubstitute, i index of matrixes
     * @param jSubstitute, j index of matrixes
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
            } else if (building instanceof Restaurant) {
                buildings.add(new Restaurant((Restaurant) building));
            } else if (building instanceof Toilet) {
                buildings.add(new Toilet((Toilet) building));
            } else if (building instanceof TrashBin) {
                buildings.add(new TrashBin((TrashBin) building));
            } else if (building instanceof Road) {
                buildings.add(new Road((Road) building));
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
     * Works with a building's indexes. Changes the pictures in the tiles matrix
     * to grass and repaints. Changes the types and indexes back to grass and -1
     * in the modelTiles matrix. Sets the given index in the buildings arrayList
     * to null.
     *
     * @param iSubstitute, i index of matrixes
     * @param jSubstitute, j index of matrixes
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
        buildings.set(tempIndex, null);
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
                        if (free) {
                            createBuilding(iSubstitute, jSubstitute);
                            money -= building.getBUILDING_COST();
                        }
                    }
                }
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            tiles[iSubstitute][jSubstitute].setBorder(BorderFactory.createLineBorder(Color.black));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            tiles[iSubstitute][jSubstitute].setBorder(BorderFactory.createEmptyBorder());
        }
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
}
