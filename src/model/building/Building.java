package model.building;

import java.awt.Point;
import java.util.ArrayList;
import model.Details;

public abstract class Building {

    private Details details;
    protected int BUILDING_COST;
    protected String name;
    protected ArrayList<Point> indexes;

    public Building(Details details, int bcost, String name) {
        this.details = details;
        this.BUILDING_COST = bcost;
        this.name = name;
        this.indexes = new ArrayList<>();
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

    public ArrayList<Point> getIndexes() {
        return indexes;
    }
}
