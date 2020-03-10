package bg.sofia.uni.fmi.mjt.virtualwallet.core.card;

public class StandardCard extends Card {

    public StandardCard(String name) {
        super(name);
    }

    @Override
    public boolean executePayment(double cost) {
        if (this.getAmount() - cost < 0) {
            return false;
        } else {
            this.setAmount(this.getAmount() - cost);
        }
        return true;
    }
}
