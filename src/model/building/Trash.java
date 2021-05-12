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

public class Trash extends Details {

    private final int happinessFactor;

    public Trash(String img, int height, int length) {
        super(img, height, length);
        happinessFactor = 5;
    }

    public int getHappinessFactor() {
        return happinessFactor;
    }
}
