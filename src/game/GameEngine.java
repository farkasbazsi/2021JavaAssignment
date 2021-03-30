package game;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;
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

    public Tile[][] tiles;
    private final Image grass;
    private final Image road;
    private final int height = 23;
    private final int width = 25;
    public Building building;

    //constructed with the centerPanel
    public GameEngine(JPanel panel) throws IOException {
        building = null;
        grass = ResourceLoader.loadImage("res/grass.png");
        road = ResourceLoader.loadImage("res/road.png");
        tiles = new Tile[height][width];

        panel.setBackground(Color.red);

        panel.setLayout(new GridLayout(height, width));
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i == 22 && j == 12) {
                    tiles[i][j] = new Tile(road, 'R');
                } else {
                    tiles[i][j] = new Tile(grass, 'G');
                }

                int iSubstitute = i;
                int jSubstitute = j;
                tiles[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (building != null) {
                            int buildingHeight = building.getDetails().height;
                            int buildingWidth = building.getDetails().length;

                            if (iSubstitute + buildingHeight < height + 1
                                    && jSubstitute + buildingWidth < width + 1) {
                                //System.out.println(iSubstitute + " " + jSubstitute + " : " + buildingHeight + " " + buildingWidth);

                                boolean free = true;
                                for (int k = 0; k <= buildingHeight - 1; k++) {
                                    for (int l = 0; l <= buildingWidth - 1; l++) {
                                        if (tiles[iSubstitute + k][jSubstitute + l].type != 'G') {
                                            free = false;
                                        }
                                    }
                                }
                                if (free) {
                                    for (int k = 0; k <= buildingHeight - 1; k++) {
                                        for (int l = 0; l <= buildingWidth - 1; l++) {
                                            try {
                                                tiles[iSubstitute + k][jSubstitute + l].image
                                                        = ResourceLoader.loadImage("res/" + building.getDetails().image);
                                                tiles[iSubstitute + k][jSubstitute + l].repaint();
                                            } catch (IOException ex) {
                                                Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
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
                });

                panel.add(tiles[i][j]);
            }
        }
    }

    private void newGame() {

    }

    private void generateField() {

    }

    private void exit() {

    }

    private void newWorker() {

    }

    private void removeWorker(int id) {

    }

    private void createBuilding() {

    }

    private void removeBuilding(int id) {

    }

}
