package bg.sofia.uni.fmi.mjt.virtualwallet.core.card;

public class GoldenCard extends Card {
    public GoldenCard(String name) {
        super(name);
    }

    @Override
    public boolean executePayment(double cost) {
        if (cost < 0 || this.getAmount() - cost < 0){
            return false;
        } else{
            double finalCost = cost - ((15 / 100D) * cost);
            this.setAmount(this.getAmount() - finalCost);
        }
        return true;
    }
}
