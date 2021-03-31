package model.building;

import model.Details;

public class Ride extends Building{
    private int cost;
    private int maxWorkers;
    private int maxVisitors;
    private BuildingState currentState;
    
    public Ride(Details details, int bcost, String name){
        super(details, bcost, name);
    }
    
    public Ride(Ride ride) {
        super(ride.getDetails(), ride.getBUILDING_COST(), ride.getName());
    }
      
    private void changeState() {
        currentState = BuildingState.BUILDING;
    }
}
