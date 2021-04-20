package model.building;

import model.Details;

public class Road extends Building {

    private boolean trashOnIt;

    public Road(Details details, int bcost, String name) {
        super(details, bcost, name);
        //trashOnIt = true;
    }

    public Road(Road road) {
        super(road.getDetails(), road.getBUILDING_COST(), road.getName());
        //trashOnIt = true;
    }

    public boolean hasTrashOnIt() {
        return trashOnIt;
    }

    public void setTrashOnIt(boolean trashOnIt) {
        this.trashOnIt = trashOnIt;
    }
}
