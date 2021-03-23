package model.building;

import model.Details;

public abstract class Building {

    private Details details;
    protected int BUILDING_COST;
    protected String name;

    public Building(Details details, int bcost, String name) {
        this.details = details;
        this.BUILDING_COST = bcost;
        this.name = name;
    }

    public Details getDetails() {
        return details;
    }

    public String getName() {
        return name;
    }

    public int getBUILDING_COST() {
        return BUILDING_COST;
    }

}
