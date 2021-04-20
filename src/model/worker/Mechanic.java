package model.worker;

import java.awt.image.BufferedImage;
import model.Details;

public class Mechanic extends Worker {

    @Override
    void findPath() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Mechanic(Details details, BufferedImage image) {
        super(details, image);
    }

    public void repairRide() {

    }
}
