package model.building;

import model.Details;

public class Plant extends Building{
    private int happinessFactor;

    public Plant(Details details, int bcost, String name, int amount) {
        super(details, bcost, name);
        happinessFactor = amount;
    }
    
    public int getHappinessFactor(){
        return happinessFactor;
    }
    
}
