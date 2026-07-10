package com.bitly.exceptions;
public class AccountConflictException extends RuntimeException {

    public AccountConflictException(String message){
        super(message);
    }

}
