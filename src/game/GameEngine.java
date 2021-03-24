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
                        System.out.printf("'" + building.getName() + "'" + "\n");
                        if (building != null && tiles[iSubstitute][jSubstitute].type == 'G') {
                            int height = building.getDetails().height;
                            int width = building.getDetails().length;

                            String name = building.getName();
                            //MINDIG AZ ELSE AGBA FUT
                            if (name.equals("restaurant")) {
                                System.out.printf("HERE\n");
                                if(tiles[iSubstitute+1][jSubstitute].type=='G' &&
                                  tiles[iSubstitute][jSubstitute+1].type=='G' &&
                                  tiles[iSubstitute+1][jSubstitute+1].type=='G'){
                                    Image image;
                                    image = grass;
                                    try {
                                        image = ResourceLoader.loadImage("res/" + building.getDetails().image);
                                    } catch (IOException ex) {
                                        Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    tiles[iSubstitute][jSubstitute].image=image;
                                    tiles[iSubstitute][jSubstitute].repaint();
                                    tiles[iSubstitute+1][jSubstitute].image=image;
                                    tiles[iSubstitute+1][jSubstitute].repaint();
                                    tiles[iSubstitute][jSubstitute+1].image=image;
                                    tiles[iSubstitute][jSubstitute+1].repaint();
                                    tiles[iSubstitute+1][jSubstitute+1].image=image;
                                    tiles[iSubstitute+1][jSubstitute+1].repaint();
                                }
                            }else{
                                try {
                                    tiles[iSubstitute][jSubstitute].image=
                                            ResourceLoader.loadImage("res/" + building.getDetails().image);
                                    tiles[iSubstitute][jSubstitute].repaint();
                                } catch (IOException ex) {
                                    Logger.getLogger(GameEngine.class.getName()).log(Level.SEVERE, null, ex);
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
