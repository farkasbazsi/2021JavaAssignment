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

public class Plant extends Building {

    private int happinessFactor;

    public Plant(Details details, int bcost, String name, int amount) {
        super(details, bcost, name);
        happinessFactor = amount;
    }

    public Plant(Plant plant) {
        super(plant.getDetails(), plant.getBUILDING_COST(), plant.getName());
        this.happinessFactor = plant.getHappinessFactor();
    }

    public int getHappinessFactor() {
        return happinessFactor;
    }

}
