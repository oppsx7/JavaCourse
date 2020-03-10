package test;

import bg.sofia.uni.fmi.mjt.shopping.portal.offer.Offer;
import bg.sofia.uni.fmi.mjt.shopping.portal.offer.PremiumOffer;
import bg.sofia.uni.fmi.mjt.shopping.portal.offer.RegularOffer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class PremiumOfferTest {

    private static final String PRODUCT = "product";
    private static final String DESCRIPTION = "desr";
    private static final int PRICE = 60;
    private static final int SHIPPING_PRICE = 40;
    private static final int DISCOUNT = 5;
    private static final int EXPECTED = 95;
    private static final double DELTA = 0.00001;
    private static final int MINUS_ONE = -1;
    private static final int OVER_HUNDRED = 101;
    private static final int ZERO = 0;
    private static final LocalDate NOW = LocalDate.now();

    private PremiumOffer offer;

    @Before
    public void init() {
        offer = new PremiumOffer(PRODUCT, NOW, DESCRIPTION, PRICE, SHIPPING_PRICE, DISCOUNT);
    }

    @Test
    public void testDiscount() {
        assertEquals(EXPECTED, offer.getTotalPrice(), DELTA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeDiscount() {
        offer.setDiscount(MINUS_ONE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDiscountOverHundred() {
        offer.setDiscount(OVER_HUNDRED);
    }

    @Test
    public void testZeroDiscount() {
        offer.setDiscount(ZERO);
        Offer offer1 = new RegularOffer(PRODUCT, NOW, DESCRIPTION, PRICE, SHIPPING_PRICE);

        assertEquals(offer, offer1);
        assertEquals(offer1, offer);
    }

}
