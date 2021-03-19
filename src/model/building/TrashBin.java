package model.building;

import model.Details;

public class TrashBin extends Building{
    private int fullness;

    public TrashBin(Details details) {
        super(details);
    }
    
    public boolean isFull(){
        return fullness == 100;
    }
    
    public void empty(){
        fullness = 0;
    }
}
