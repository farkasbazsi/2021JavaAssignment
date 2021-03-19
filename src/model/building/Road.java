package model.building;

import model.Details;

public class Road extends Building{
    private boolean trashOnIt;

    public Road(Details details){
        super(details);
    }
    
    public boolean hasTrashOnIt() {
        return trashOnIt;
    }
    
}
