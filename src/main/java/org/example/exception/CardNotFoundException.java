package org.example.exception;

public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException(Integer Id) {
        super("Card with Id "+Id+" not found");
    }
    public CardNotFoundException(String message) {
        super(message);
    }
}
