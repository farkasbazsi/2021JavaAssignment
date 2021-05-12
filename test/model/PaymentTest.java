package model;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Hexa
 */
public class PaymentTest {

    @Test
    public void testConstruct1() {
        Payment payment = new Payment();
        assertEquals(payment.getToiletFee(), 5);
    }

    @Test
    public void testConstruct2() {
        Payment payment = new Payment();
        assertEquals(payment.getWorkerFee(), 15);
    }

    @Test
    public void testConstruct3() {
        Payment payment = new Payment();
        assertEquals(payment.getGamesFee(), 30);
    }

    @Test
    public void testConstruct4() {
        Payment payment = new Payment();
        assertEquals(payment.getRestaurantFee(), 25);
    }

    @Test
    public void testConstruct5() {
        Payment payment = new Payment();
        assertEquals(payment.getEntranceFee(), 50);
    }

}
