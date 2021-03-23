package game;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
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
    
    //constructed with the centerPanel
    public GameEngine(JPanel panel) throws IOException{
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
    
    private void newWorker(){
        
    }
    
    private void removeWorker(int id) {
        
    }
    
    private void createBuilding() {
        
    }
    
    private void removeBuilding(int id) {
        
    }
    
    
    
}
