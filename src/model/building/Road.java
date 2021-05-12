/**
 * 2020/2021/2
 * Szoftvertechnológia
 *
 * FFN project
 * Nagy Gergő, Falusi Gergő Gábor, Farkas Balázs
 *
 * 2021.05.12.
 */
package model.building;

import model.Details;

public class Road extends Building {

    private boolean trashOnIt;

    public Road(Details details, int bcost, String name) {
        super(details, bcost, name);
    }

    public Road(Road road) {
        super(road.getDetails(), road.getBUILDING_COST(), road.getName());
    }

    public boolean hasTrashOnIt() {
        return trashOnIt;
    }

    public void setTrashOnIt(boolean trashOnIt) {
        this.trashOnIt = trashOnIt;
    }
}
