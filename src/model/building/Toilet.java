package model.building;

import model.Details;

public class Toilet extends Building {

    //private boolean free;
    private BuildingState currentState;

    public Toilet(Details details, int bcost, String name) {
        super(details, bcost, name);
        currentState = BuildingState.ACTIVE;
        //free = true;
    }

    public Toilet(Toilet toilet) {
        super(toilet.getDetails(), toilet.getBUILDING_COST(), toilet.getName());
        currentState = BuildingState.ACTIVE;
    }

    public void changeState(BuildingState state) {
        currentState = state;
    }

    public BuildingState getCurrentState() {
        return currentState;
    }

    public void usedByVisitor() {

    }

}
