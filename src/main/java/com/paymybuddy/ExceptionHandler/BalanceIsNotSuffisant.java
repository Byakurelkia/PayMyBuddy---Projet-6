package com.paymybuddy.ExceptionHandler;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class BalanceIsNotSuffisant extends RuntimeException{

    public BalanceIsNotSuffisant(String errorMsg){
        super(errorMsg);
    }
}
