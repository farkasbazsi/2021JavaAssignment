package model;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.JPanel;

public class Tile extends JPanel {

    //public, for now
    public Image image;
    public char type;

    public Tile(Image m, char c) {
        image = m;
        type = c;

        //if you dont set the size here the whole center panel gets fucked up
        this.setPreferredSize(new Dimension(32, 32));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D gr = (Graphics2D) g;
        g.drawImage(image, 0, 0, 32, 32, this);
    }
}
