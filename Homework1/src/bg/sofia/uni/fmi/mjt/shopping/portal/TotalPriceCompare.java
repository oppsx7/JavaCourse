package bg.sofia.uni.fmi.mjt.shopping.portal;

import bg.sofia.uni.fmi.mjt.shopping.portal.offer.Offer;

import java.util.Comparator;

public class TotalPriceCompare implements Comparator<Offer> {
    @Override
    public int compare(Offer o1, Offer o2) {
        if (o1.getTotalPrice() < o2.getTotalPrice())
            return -1;
        if (o1.getTotalPrice() > o2.getTotalPrice())
            return 1;
        else

            return 0;
    }
}
