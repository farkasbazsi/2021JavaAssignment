package model.building;

import model.Details;

public class Toilet extends Building{
    private boolean free;

    public Toilet(Details details) {
        super(details);
        free = true;
    }
    
    public boolean isFree(){
        return free;
    }
    
    public void usedByVisitor(){
        
    }
    
}


