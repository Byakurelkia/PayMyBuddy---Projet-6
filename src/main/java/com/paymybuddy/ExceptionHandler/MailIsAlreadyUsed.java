package com.paymybuddy.ExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class MailIsAlreadyUsed extends RuntimeException{

    public MailIsAlreadyUsed(String errorMsg){
        super(errorMsg);
    }


}
