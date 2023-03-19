package com.projet6.paymybuddy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DataNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -8196904668947289220L;

    public DataNotFoundException(String message){super(message);}
}
