package model.building;

import model.Details;

public class Ride extends Building{
    private int cost;
    private int maxWorkers;
    private int currentVisitors;
    private int maxVisitors;
    private int turnsTillStart;
    private BuildingState currentState;
    
    public Ride(Details details, int bcost, String name){
        super(details, bcost, name);
        currentState = BuildingState.ACTIVE;
        maxVisitors = 3;
        currentVisitors = 0;
        turnsTillStart = 10;
    }
    
    public Ride(Ride ride) {
        super(ride.getDetails(), ride.getBUILDING_COST(), ride.getName());
        currentState = BuildingState.ACTIVE;
        maxVisitors = 3;
        currentVisitors = 0;
        turnsTillStart = 10;
    }

    public int getTurnsTillStart() {
        return turnsTillStart;
    }

    public void setTurnsTillStart(int turnsTillStart) {
        this.turnsTillStart = turnsTillStart;
    }
    
    public void decTurnsTillStart(){
        this.turnsTillStart--;
    }
    
    public void incCurrentVisitors(){
        this.currentVisitors++;
    }

    public int getCurrentVisitors() {
        return currentVisitors;
    }

    public void setCurrentVisitors(int currentVisitors) {
        this.currentVisitors = currentVisitors;
    }

    public int getMaxVisitors() {
        return maxVisitors;
    }

    public void setMaxVisitors(int maxVisitors) {
        this.maxVisitors = maxVisitors;
    }
      
    public void changeState(BuildingState state) {
        currentState = state;
    }

    public BuildingState getCurrentState() {
        return currentState;
    }
}
