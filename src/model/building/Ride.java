package model.building;

import model.Details;

public class Ride extends Building{
    private int cost;
    private int maxWorkers;
    private int maxVisitors;
    private BuildingState currentState;
    
    public Ride(Details details){
        super(details);
    }
    
    private void changeState() {
        currentState = BuildingState.BUILDING;
    }
}
