package model.building;

import model.Details;

public abstract class Building {
    private Details details;
    private int BUILDING_COST;
    
    public Building(Details details){
        this.details = details;
    }

    public Details getDetails() {
        return details;
    }
    
}
