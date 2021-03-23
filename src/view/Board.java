/*
    The whole tiles matrix can be moved to the gameEngine!
 
package view;

import java.awt.Color;
import res.ResourceLoader;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.IOException;
import javax.swing.JPanel;
import model.Tile;

public class Board extends JPanel {

    
    

    public Board() throws IOException {
        grass = ResourceLoader.loadImage("res/grass.png");
        road = ResourceLoader.loadImage("res/road.png");
        tiles = new Tile[height][width];

        this.setBackground(Color.red);

        this.setLayout(new GridLayout(height, width));
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i == 22 && j == 12) {
                    tiles[i][j] = new Tile(road, 'R');
                } else {
                    tiles[i][j] = new Tile(grass, 'G');
                }

                this.add(tiles[i][j]);
            }
        }
    }
}
*/