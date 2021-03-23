package model.building;

import model.Details;

public class Road extends Building{
    private boolean trashOnIt;

    public Road(Details details, int bcost, String name){
        super(details, bcost, name);
    }
    
    public boolean hasTrashOnIt() {
        return trashOnIt;
    }
    
}
