package com.bitly.exceptions;

public class TransactionTypeMismatchException extends RuntimeException {

    public TransactionTypeMismatchException(String message) {
        super(message);
    }

}
