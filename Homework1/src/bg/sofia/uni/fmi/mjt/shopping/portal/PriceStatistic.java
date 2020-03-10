package bg.sofia.uni.fmi.mjt.shopping.portal;

import java.time.LocalDate;

public class PriceStatistic {
    private LocalDate date;
    private double lowestPrice;
    private double averagePrice;

    public PriceStatistic(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public double getLowestPrice() {
        return this.lowestPrice;
    }

    public double getAveragePrice() {
        return this.averagePrice;
    }

    public void setLowestPrice(double lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public void print() {
        System.out.println(this.getDate() + " " + this.getAveragePrice() + " " + this.getLowestPrice());
    }
}
