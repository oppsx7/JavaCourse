package bg.sofia.uni.fmi.mjt.virtualwallet.core.payment;

public class PaymentInfo {

    private double cost;
    private String reason;
    private String location;

    public PaymentInfo(String reason, String location, double cost) {
        this.reason = reason;
        this.location = location;
        this.cost = cost;
    }

    public double getCost() {
        return this.cost;
    }

    public String getReason() {
        return this.reason;
    }

    public String getLocation() {
        return this.location;
    }

}
