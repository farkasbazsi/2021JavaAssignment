package model.building;

import model.Details;

public class Plant extends Building{
    private int happinessFactor;

    public Plant(Details details, int amount) {
        super(details);
        happinessFactor = amount;
    }
    
    public int getHappinessFactor(){
        return happinessFactor;
    }
    
}
