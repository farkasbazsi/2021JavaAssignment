package model.building;

import model.Details;

public class Road extends Building{
    private boolean trashOnIt;

    public Road(Details details, int bcost, String name){
        super(details, bcost, name);
    }
    
    public Road(Road road) {
        super(road.getDetails(), road.getBUILDING_COST(), road.getName());
    }
    
    public boolean hasTrashOnIt() {
        return trashOnIt;
    }
    
}
