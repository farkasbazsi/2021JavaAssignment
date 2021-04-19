package model;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.JPanel;

public class Tile extends JPanel {

    private Image image;

    public Tile(Image m) {
        image = m;

        //if you dont set the size here the whole center panel gets fucked up
        this.setPreferredSize(new Dimension(32, 32));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D gr = (Graphics2D) g;
        g.drawImage(image, 0, 0, 32, 32, this);
    }

    public void setImage(Image image) {
        this.image = image;
        this.setPreferredSize(new Dimension(32, 32));
    }
    
    public Image getImage() {
        return image;
    }

}
