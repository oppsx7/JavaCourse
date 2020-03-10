package bg.sofia.uni.fmi.mjt.shopping.portal.exceptions;

public class NoOfferFoundException extends RuntimeException{
    String message;

    public NoOfferFoundException(String message) {
        super(message);
    }
}
