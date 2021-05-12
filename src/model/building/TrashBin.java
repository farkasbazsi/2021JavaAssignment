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

public class TrashBin extends Building {

    public TrashBin(Details details, int bcost, String name) {
        super(details, bcost, name);
    }

    public TrashBin(TrashBin trashbin) {
        super(trashbin.getDetails(), trashbin.getBUILDING_COST(), trashbin.getName());
    }
}
