/**
 * 2020/2021/2
 * Szoftvertechnológia
 *
 * FFN project
 * Nagy Gergő, Falusi Gergő Gábor, Farkas Balázs
 *
 * 2021.05.12.
 */
package model;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Hexa
 */
public class VisitorTest {

    @Test
    public void testConstructor1() {
        Visitor visitor = new Visitor(new Details("img.jpg", 100, 100));
        assertFalse(visitor.leaving);
    }

    @Test
    public void testConstructor2() {
        Visitor visitor = new Visitor(new Details("img.jpg", 100, 100));
        assertTrue(visitor.getMoney() <= 1000);
    }

    @Test
    public void testConstructor3() {
        Visitor visitor = new Visitor(new Details("img.jpg", 100, 100));
        assertTrue(visitor.getMoney() >= 500);
    }

    @Test
    public void testConstructor4() {
        Visitor visitor = new Visitor(new Details("img.jpg", 100, 100));
        assertTrue(visitor.getHappiness() <= 100);
    }

    @Test
    public void testConstructor5() {
        Visitor visitor = new Visitor(new Details("img.jpg", 100, 100));
        assertTrue(visitor.getHappiness() >= 60);
    }

    @Test
    public void testConstructor6() {
        Visitor visitor = new Visitor(new Details("img.jpg", 100, 100));
        assertTrue(visitor.getHunger() >= 0);
    }

    @Test
    public void testConstructor7() {
        Visitor visitor = new Visitor(new Details("img.jpg", 100, 100));
        assertTrue(visitor.getHunger() <= 30);
    }

    @Test
    public void testUseRide1() {
        Visitor visitor = new Visitor(new Details("img.jpg", 100, 100));
        int currMoney = visitor.getMoney();
        visitor.useRide(10);
        assertEquals(visitor.getMoney(), currMoney - 10);
        visitor.setMoney(10);
        visitor.useRide(100);
        assertEquals(visitor.getMoney(), 0);
    }

    @Test
    public void testUseRide2() {
        Visitor visitor = new Visitor(new Details("img.jpg", 100, 100));
        visitor.setMoney(10);
        visitor.useRide(100);
        assertEquals(visitor.getMoney(), 0);
    }

    @Test
    public void testChangeHappiness1() {
        Visitor visitor = new Visitor(new Details("img.jpg", 100, 100));
        int currHappiness = visitor.getHappiness();
        visitor.changeHappiness(-1);
        assertEquals(visitor.getHappiness(), currHappiness - 1);
        visitor.changeHappiness(1000);
        assertEquals(visitor.getHappiness(), 100);
    }

    @Test
    public void testChangeHappiness2() {
        Visitor visitor = new Visitor(new Details("img.jpg", 100, 100));
        visitor.changeHappiness(1000);
        assertEquals(visitor.getHappiness(), 100);
    }

}
