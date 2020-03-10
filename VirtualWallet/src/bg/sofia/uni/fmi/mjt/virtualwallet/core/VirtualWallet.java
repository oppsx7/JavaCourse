package bg.sofia.uni.fmi.mjt.virtualwallet.core;

import bg.sofia.uni.fmi.mjt.virtualwallet.core.card.Card;
import bg.sofia.uni.fmi.mjt.virtualwallet.core.payment.PaymentInfo;

import java.time.LocalDate;
import java.util.*;

public class VirtualWallet implements VirtualWalletAPI {

    private static final int MAX_CARDS = 5;
    private static final int MAX_TRANSACTIONS = 10;

    private Map<String, Card> cards;
    private Queue<Transaction> transactions;

    public VirtualWallet() {
        cards = new LinkedHashMap<>();
        transactions = new LinkedList<>();
    }

    @Override
    public boolean registerCard(Card card) {
        if (card == null || card.getName() == null || cards.containsKey(card.getName()) || cards.size() >= MAX_CARDS) {
            System.out.println("Maximum limit for registered cards reached ! ");
            return false;
        } else {
            cards.put(card.getName(), card);
            return true;
        }
    }

    @Override
    public boolean executePayment(Card card, PaymentInfo paymentInfo) {
        if (card != null && paymentInfo != null && card.executePayment(paymentInfo.getCost())
                && paymentInfo.getLocation() != null
                && paymentInfo.getReason() != null
                && paymentInfo.getCost() >= 0) {
            if (transactions.size() >= MAX_TRANSACTIONS) {
                transactions.poll();
            }
            transactions.add(new Transaction(card.getName(), LocalDate.now(), paymentInfo));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean feed(Card card, double amount) {
        if (card == null || amount < 0 || !cards.containsKey(card.getName())) {
            return false;
        } else {
            cards.get(card.getName()).setAmount(card.getAmount() + amount);
            return true;
        }
    }

    @Override
    public Card getCardByName(String name) {
        return cards.get(name);
    }

    @Override
    public int getTotalNumberOfCards() {
        if (!cards.isEmpty()) {
            return cards.size();
        } else {
            return 0;
        }

    }

}
