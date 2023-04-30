package com.projet6.paymybuddy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DataMissingException extends RuntimeException{

    private static final long serialVersionUID = 149743021862891028L;

    public DataMissingException(String message){super(message);}
}
