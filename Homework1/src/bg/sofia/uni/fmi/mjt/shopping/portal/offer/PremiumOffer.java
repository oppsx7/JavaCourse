package bg.sofia.uni.fmi.mjt.shopping.portal.offer;

import java.time.LocalDate;

public class PremiumOffer implements Offer {
    private LocalDate date;
    private String productName;
    private String description;
    private double price;
    private double shippingPrice;
    private double totalPrice;
    private int discount;

    public PremiumOffer(String productName, LocalDate date, String description,
                        double price, double shippingPrice, int discount) {
        this.productName = productName;
        this.date = date;
        this.description = description;
        this.price = price;
        this.shippingPrice = shippingPrice;
        this.discount = discount;
        totalPrice = (price + shippingPrice)-(discount*(price + shippingPrice)/100);
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

    public double getDiscount() { return discount; }

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

    public void setDiscount(int discount) throws IllegalArgumentException{
        if(discount<0 || discount>100)
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        this.discount = discount;
    }
}
