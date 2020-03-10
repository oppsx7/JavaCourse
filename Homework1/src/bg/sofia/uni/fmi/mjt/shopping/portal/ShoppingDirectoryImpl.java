package bg.sofia.uni.fmi.mjt.shopping.portal;

import bg.sofia.uni.fmi.mjt.shopping.portal.exceptions.NoOfferFoundException;
import bg.sofia.uni.fmi.mjt.shopping.portal.exceptions.OfferAlreadySubmittedException;
import bg.sofia.uni.fmi.mjt.shopping.portal.exceptions.ProductNotFoundException;
import bg.sofia.uni.fmi.mjt.shopping.portal.offer.Offer;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.time.*;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.abs;

public class ShoppingDirectoryImpl implements ShoppingDirectory {
    private Map<String, List<Offer>> products = new LinkedHashMap<>();
    private LocalDate currentDate = LocalDate.now();
    private Map<String, List<PriceStatistic>> productsPriceStatistics = new LinkedHashMap<>();

    public ShoppingDirectoryImpl() {

    }

    public void addOffers(String productName, List<Offer> offers) {
        products.put(productName, offers);

    }

    /**
     * Returns a collection of offers submitted in the last 30 days
     * for the product with name @productName sorted by total price
     * in ascending order.
     *
     * @throws ProductNotFoundException if there is no product with name @productName
     * @throws IllegalArgumentException if @productName is null
     */
    @Override
    public Collection<Offer> findAllOffers(String productName) throws ProductNotFoundException {
        List<Offer> upToDateOffers = new ArrayList<>();
        if (productName == null)
            throw new IllegalArgumentException("Product name is null !");

        if (!products.containsKey(productName))
            throw new ProductNotFoundException("Product does not exist");


        for (Offer offer : products.get(productName)) {
            final long days = ChronoUnit.DAYS.between(currentDate, offer.getDate());
            if (abs(days) < 30)
                upToDateOffers.add(offer);
        }

        Collections.sort(upToDateOffers, new TotalPriceCompare());

        return upToDateOffers;
    }

    /**
     * Returns the most favorable offer for the product with name @productName
     * submitted in the last 30 days - the one with lowest total price.
     *
     * @throws ProductNotFoundException if there is no product with name @productName
     * @throws NoOfferFoundException    if there is no offer submitted in the last 30
     *                                  days for the product with name @productName
     * @throws IllegalArgumentException if @productName is null
     */
    @Override
    public Offer findBestOffer(String productName) throws ProductNotFoundException,
            NoOfferFoundException, IllegalArgumentException {
        if (productName == null)
            throw new IllegalArgumentException("Product name is null !");

        if (!products.containsKey(productName))
            throw new ProductNotFoundException("Product does not exist");

        if (findAllOffers(productName).size() == 0 && products.get(productName).size() > 0)
            throw new NoOfferFoundException("Offer is too old");

        Offer offer = findAllOffers(productName).iterator().next();

        return offer;
    }

    /**
     * Returns a collection of price statistics for the product with name @productName
     * sorted by date in descending order.
     *
     * @throws ProductNotFoundException if there is no product with name @productName
     * @throws IllegalArgumentException if @productName is null
     */
    @Override
    public Collection<PriceStatistic> collectProductStatistics(String productName) throws ProductNotFoundException {
        if (productName == null)
            throw new IllegalArgumentException("Product name should not be null");
        if (!products.containsKey(productName))
            throw new ProductNotFoundException("Product does not exist");

        return productsPriceStatistics.get(productName);
    }

    /**
     * Submits a new offer.
     *
     * @throws OfferAlreadySubmittedException if an identical @offer has already been submitted
     * @throws IllegalArgumentException       if @offer is null
     */
    @Override
    public void submitOffer(Offer offer) throws OfferAlreadySubmittedException {

        List<Offer> offers = new ArrayList<>();
        if (offer == null) {
            throw new IllegalArgumentException("Null Offer !");
        }


        if (products.containsKey(offer.getProductName())){
            if (products.get(offer.getProductName()).contains(offer)) {
                throw new OfferAlreadySubmittedException("Offer already submitted!");
            }
        products.get(offer.getProductName()).add(offer);
    }
        else {
            offers.add(offer);
            products.put(offer.getProductName(), offers);
        }
        updatePriceStatistics(offer.getProductName());


    }

    public void updatePriceStatistics(String productName) throws NoOfferFoundException {
        if ((findAllOffers(productName).size() != 0 && products.get(productName).size() > 1)) {
            PriceStatistic priceStatistic = new PriceStatistic(currentDate);
            priceStatistic.setLowestPrice(findBestOffer(productName).getTotalPrice());
            int sum = 0;
            for (Offer offer : products.get(productName)) {
                if (offer.getDate().equals(currentDate))
                    sum += offer.getTotalPrice();
            }
            priceStatistic.setAveragePrice(sum / (products.get(productName).size()));
            if (productsPriceStatistics.containsKey(productName))
                productsPriceStatistics.get(productName).add(priceStatistic);
            else {
                List<PriceStatistic> list = new ArrayList<>();
                productsPriceStatistics.put(productName, list);
                productsPriceStatistics.get(productName).add(priceStatistic);
            }
        }

    }

}
