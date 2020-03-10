package bg.sofia.uni.fmi.mjt.shopping.portal.offer;

import java.time.LocalDate;

public class RegularOffer implements Offer {

    private LocalDate date;
    private String productName;
    private String description;
    private double price;
    private double shippingPrice;
    private double totalPrice;


    public RegularOffer(String productName, LocalDate date, String description, double price, double shippingPrice) {
        this.productName = productName;
        this.date = date;
        this.description = description;
        this.price = price;
        this.shippingPrice = shippingPrice;
        totalPrice = price + shippingPrice;
    }


    @Override
    public String getProductName() {
        return productName;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public double getShippingPrice() {
        return shippingPrice;
    }

    @Override
    public double getTotalPrice() {
        return totalPrice;
    }

    @Override
    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public void setShippingPrice(double shippingPrice) {
        this.shippingPrice = shippingPrice;
    }

    @Override
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Offer))
            return false;
        Offer offer = (Offer) obj;

        return this.getDate().compareTo(offer.getDate()) == 0 &&
                this.getPrice() == offer.getPrice() &&
                this.getShippingPrice() == offer.getShippingPrice() &&
                this.getTotalPrice() == offer.getTotalPrice() &&
                this.getProductName().equalsIgnoreCase(offer.getProductName()) &&
                this.getDescription().equalsIgnoreCase(offer.getDescription());
    }
}
