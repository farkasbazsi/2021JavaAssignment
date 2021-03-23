package model.building;

import model.Details;

public class Trash extends Details{
    private int happinessFactor;

    public Trash(String img, int height, int length) {
        super(img, height, length);
        happinessFactor = 5;
    }
    
    public int getHappinessFactor(){
        return happinessFactor;
    }
}
