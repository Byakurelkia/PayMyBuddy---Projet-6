package com.paymybuddy.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(){
        super("Invalid Mail, User Doesnt Exist With This Mail");
        log.error("Invalid Mail, User Doesnt Exist With This Mail");
    }

    public UserNotFoundException(String errorMsg){
        super(errorMsg);
    }


}
