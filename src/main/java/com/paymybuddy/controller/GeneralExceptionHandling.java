package com.paymybuddy.controller;

import com.paymybuddy.ExceptionHandler.AccountNotFoundException;
import com.paymybuddy.ExceptionHandler.CreationUserRequestNonValid;
import com.paymybuddy.ExceptionHandler.MailIsAlreadyUsed;
import com.paymybuddy.ExceptionHandler.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GeneralExceptionHandling {


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> userNotFoundException(UserNotFoundException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MailIsAlreadyUsed.class)
    public ResponseEntity<?> mailIsAlreadyUsed(MailIsAlreadyUsed ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(CreationUserRequestNonValid.class)
    public ResponseEntity<?> creationUserRequestIsNotValid(CreationUserRequestNonValid ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<?> accountNotFoundForThisUser(AccountNotFoundException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
