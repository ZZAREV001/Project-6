package com.projet6.paymybuddy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DataNotExistException extends Throwable {

    private static final long serialVersionUID = -582616643185311224L;

    public DataNotExistException(String message){super(message);}
}
