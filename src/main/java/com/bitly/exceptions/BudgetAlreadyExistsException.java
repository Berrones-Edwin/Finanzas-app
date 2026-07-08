package com.bitly.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BudgetAlreadyExistsException extends RuntimeException {

    public BudgetAlreadyExistsException(String message) {
        super(message);
    }
}
