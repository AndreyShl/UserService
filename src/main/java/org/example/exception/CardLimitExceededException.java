package org.example.exception;

public class CardLimitExceededException extends RuntimeException {
    public CardLimitExceededException(Integer userId) {
        super("User with id " + userId + " exceeded card limit more than 5 cards");
    }
}
