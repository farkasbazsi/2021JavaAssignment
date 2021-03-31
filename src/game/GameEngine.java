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
import res.ResourceLoader;

public class GameEngine {

    private Timer timer;
    private Integer money;
    private ArrayList<Worker> workers;
    private ArrayList<Building> buildings;
    private ArrayList<Visitor> visitors;
    private Payment payment;

    private ModelTile[][] modelTiles;
    private Tile[][] tiles;
    private final int height = 23;
    private final int width = 25;
    private Building building;

    //gets called after the centerPanel in FfnProject.java
    public GameEngine(JPanel panel) throws IOException {
        buildings = new ArrayList<>();
        tiles = new Tile[height][width];
        modelTiles = new ModelTile[height][width];
        building = null;

        generateField(panel);
    }

    /**
     * Generates the tiles matrix, and fills up the modelTiles matrix
     * accordingly
     *
     * @param panel, the panel to which the tiles matrix gets added
     * (centerPanel)
     * @throws IOException , if the ResourceLoader can't find the pictures
     */
    private void generateField(JPanel panel) throws IOException {
        panel.setLayout(new GridLayout(height, width));
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i == 22 && j == 12) {
                    modelTiles[i][j] = new ModelTile("road");
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
     * Gets called, if the placement is correct (no overlapping, no outindexing)
     * Searches in the buildings arrayList for a null element, if there is none,
     * adds the bulding to the arrayList Sets the types(dojo, etc) and
     * indexes(buildings arrayList index, showing the building itself) Changes
     * the pictures of the correct tiles in the Tiles matrix & repaints them
     *
     * @param iSubstitute, i index of matrixes
     * @param jSubstitute, j index of matrixes
     */
    private void createBuilding(int iSubstitute, int jSubstitute) {
        boolean full = true;
        int ind = -1;
        for (int i = 0; i < buildings.size(); i++) {
            if (buildings.get(i) == null) {
                buildings.set(i, building);
                full = false;
                ind = i;
            }
        }
        if (full) {
            buildings.add(building);
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

    private void removeBuilding(int id) {

    }

    private void newGame() {

    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    private void exit() {

    }

    private void newWorker() {

    }

    private void removeWorker(int id) {

    }

    /**
     * Class for the event handling. Its main function is validating building
     * placements.
     */
    class mouseListener extends MouseAdapter {

        int iSubstitute, jSubstitute;

        public mouseListener(int iSubstitute, int jSubstitute) {
            this.iSubstitute = iSubstitute;
            this.jSubstitute = jSubstitute;
        }

        /**
         *Handles the validation of building placement
         * @param e
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            //System.out.println(modelTiles[iSubstitute][jSubstitute].getIndex() + " " + modelTiles[iSubstitute][jSubstitute].getType());
            if (building != null) {

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
}
