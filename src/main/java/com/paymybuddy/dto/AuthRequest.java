package com.paymybuddy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AuthRequest {

    String eMail;
    String password;

    public AuthRequest() {
    }
}
