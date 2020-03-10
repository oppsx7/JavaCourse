package test;

import bg.sofia.uni.fmi.mjt.shopping.portal.PriceStatistic;
import bg.sofia.uni.fmi.mjt.shopping.portal.ShoppingDirectory;
import bg.sofia.uni.fmi.mjt.shopping.portal.ShoppingDirectoryImpl;
import bg.sofia.uni.fmi.mjt.shopping.portal.exceptions.NoOfferFoundException;
import bg.sofia.uni.fmi.mjt.shopping.portal.exceptions.OfferAlreadySubmittedException;
import bg.sofia.uni.fmi.mjt.shopping.portal.exceptions.ProductNotFoundException;
import bg.sofia.uni.fmi.mjt.shopping.portal.offer.Offer;
import bg.sofia.uni.fmi.mjt.shopping.portal.offer.RegularOffer;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class ShoppingDirectoryTest {
    private static final String PRODUCT_NAME = "product";
    private static final String PRODUCT_DESCRIPTION = "description";
    private static final int PRODUCT_PRICE = 10;
    private static final int PRODUCT_SHIPPING_PRICE = 10;
    private static final double DELTA = 0.00001;
    private static final int EXPECTED_SIZE = 30;
    private static final int DAYS_IN_MONTH = 31;
    private static final int SIZE_OF_STATISTICS = 50;
    private static final int FORTY_NINE = 49;
    private static final int ZERO = 0;

    private ShoppingDirectory shoppingDirectory;
    private Offer offer1;
    private Offer offer2;

    @Before
    public void init() {
        shoppingDirectory = new ShoppingDirectoryImpl();
        offer1 = new RegularOffer(PRODUCT_NAME, LocalDate.now().minusDays(DAYS_IN_MONTH), PRODUCT_DESCRIPTION,
                PRODUCT_PRICE, PRODUCT_SHIPPING_PRICE);
        offer2 = new RegularOffer(PRODUCT_NAME, LocalDate.now().minusDays(DAYS_IN_MONTH), PRODUCT_DESCRIPTION,
                PRODUCT_SHIPPING_PRICE, PRODUCT_PRICE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubmitNullOffer() throws OfferAlreadySubmittedException {
        shoppingDirectory.submitOffer(null);
    }

    @Test(expected = OfferAlreadySubmittedException.class)
    public void testSubmitContainingOffer() throws OfferAlreadySubmittedException {
        shoppingDirectory.submitOffer(offer1);
        shoppingDirectory.submitOffer(offer1);
    }

    @Test(expected = OfferAlreadySubmittedException.class)
    public void testSubmitEqualOffers() throws OfferAlreadySubmittedException {
        shoppingDirectory.submitOffer(offer1);
        shoppingDirectory.submitOffer(offer2);
    }

    @Test(expected = ProductNotFoundException.class)
    public void testFindAllOffersOfNonExistentProduct() throws ProductNotFoundException {
        shoppingDirectory.findAllOffers(PRODUCT_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAllOffersByNullName() throws ProductNotFoundException {
        shoppingDirectory.findAllOffers(null);
    }

    @Test
    public void testFindAllOffersOfProduct() throws OfferAlreadySubmittedException, ProductNotFoundException {
        initializeOffers();

        Collection<Offer> allOffers = shoppingDirectory.findAllOffers(offer1.getProductName());

        assertEquals(EXPECTED_SIZE, allOffers.size());
    }

    @Test(expected = ProductNotFoundException.class)
    public void testFindBestOfferOfNonExistent() throws ProductNotFoundException, NoOfferFoundException {
        shoppingDirectory.findBestOffer(PRODUCT_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindBestOfferByNullName() throws ProductNotFoundException, NoOfferFoundException {
        shoppingDirectory.findBestOffer(null);
    }

    @Test(expected = NoOfferFoundException.class)
    public void testFindBestOfferWithNoOfferInLastThirtyDays() throws OfferAlreadySubmittedException,
            ProductNotFoundException, NoOfferFoundException {
        shoppingDirectory.submitOffer(offer1);

        shoppingDirectory.findBestOffer(offer1.getProductName());
    }

    @Test
    public void testFindBestOffer() throws OfferAlreadySubmittedException, ProductNotFoundException,
            NoOfferFoundException {
        initializeOffers();

        Offer bestOffer = shoppingDirectory.findBestOffer(PRODUCT_NAME);

        assertEquals(PRODUCT_PRICE + PRODUCT_SHIPPING_PRICE, bestOffer.getTotalPrice(), DELTA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCollectStatisticsByNullName() throws ProductNotFoundException {
        shoppingDirectory.collectProductStatistics(null);
    }

    @Test(expected = ProductNotFoundException.class)
    public void testCollectStatisticsByNonExistentOffer() throws ProductNotFoundException {
        shoppingDirectory.collectProductStatistics(PRODUCT_NAME);
    }

    @Test
    public void testCollectPriceStatistics() throws OfferAlreadySubmittedException, ProductNotFoundException {
        initializeOffers();

        Collection<PriceStatistic> priceStatistics = shoppingDirectory.collectProductStatistics(PRODUCT_NAME);
        PriceStatistic[] statistics = new PriceStatistic[SIZE_OF_STATISTICS];
        statistics = priceStatistics.toArray(statistics);

        assertEquals(SIZE_OF_STATISTICS, priceStatistics.size());
        assertEquals(statistics[ZERO].getAveragePrice(), PRODUCT_PRICE + PRODUCT_SHIPPING_PRICE, DELTA);
    }

    private void initializeOffers() throws OfferAlreadySubmittedException {
        LocalDate now = LocalDate.now();
        for (int i = FORTY_NINE; i >= ZERO; i--) {
            Offer offer = new RegularOffer(PRODUCT_NAME, now.minusDays(i), PRODUCT_DESCRIPTION, PRODUCT_PRICE + i,
                    PRODUCT_SHIPPING_PRICE + i);
            shoppingDirectory.submitOffer(offer);
        }
    }
}
