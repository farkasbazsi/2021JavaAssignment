package model.worker;

import java.awt.image.BufferedImage;
import model.Details;

public class Cleaner extends Worker{
    @Override
    void findPath() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Cleaner(Details details, BufferedImage image){
        super(details,image);
    }
    
    public void removeTrash(){
        
    }
}
