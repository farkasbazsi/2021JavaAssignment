package model.building;

import model.Details;

public class TrashBin extends Building{
    private int fullness;

    public TrashBin(Details details, int bcost, String name) {
        super(details, bcost, name);
    }
    
    public TrashBin(TrashBin trashbin) {
        super(trashbin.getDetails(), trashbin.getBUILDING_COST(), trashbin.getName());
    }
    
    public boolean isFull(){
        return fullness == 100;
    }
    
    public void empty(){
        fullness = 0;
    }
}
