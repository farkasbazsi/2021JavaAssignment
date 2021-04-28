package model.building;

import model.Details;

public class Restaurant extends Building {

    private int cost;
    private int maxWorkers;
    private int maxVisitors;
    private BuildingState currentState;

    public Restaurant(Details details, int bcost, String name) {
        super(details, bcost, name);
        currentState = BuildingState.ACTIVE;
    }

    public Restaurant(Restaurant restaurant) {
        super(restaurant.getDetails(), restaurant.getBUILDING_COST(), restaurant.getName());
        currentState = BuildingState.ACTIVE;
    }

    public void orderByVisitor() {

    }

    public void changeState(BuildingState state) {
        currentState = state;
    }

    public BuildingState getCurrentState() {
        return currentState;
    }
}
