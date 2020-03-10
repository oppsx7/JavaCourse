package bg.sofia.uni.fmi.mjt.virtualwallet.core;

import bg.sofia.uni.fmi.mjt.virtualwallet.core.payment.PaymentInfo;

import java.time.LocalDate;

public class Transaction {

    private String cardName;
    private LocalDate date;
    private PaymentInfo paymentInfo;


    public Transaction(String cardName, LocalDate date, PaymentInfo paymentInfo) {
        this.cardName = cardName;
        this.date = date;
        this.paymentInfo = paymentInfo;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }
}
