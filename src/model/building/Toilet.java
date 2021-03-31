package model.building;

import model.Details;

public class Toilet extends Building {

    private boolean free;

    public Toilet(Details details, int bcost, String name) {
        super(details, bcost, name);
        free = true;
    }

    public Toilet(Toilet toilet) {
        super(toilet.getDetails(), toilet.getBUILDING_COST(), toilet.getName());
    }

    public boolean isFree() {
        return free;
    }

    public void usedByVisitor() {

    }

}
