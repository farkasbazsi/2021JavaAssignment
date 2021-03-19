package model.building;

import model.Details;

public class Trash extends Building{
    private int happinessFactor;

    public Trash(Details details) {
        super(details);
        happinessFactor = 5;
    }
    
    public int getHappinessFactor(){
        return happinessFactor;
    }
}
