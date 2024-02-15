package com.paymybuddy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Size(min = 14, max = 34, message = "IBAN must be between 14 and 34 characters")
    private String IBAN;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public BankAccount() {
    }

    public BankAccount(String IBAN, User user) {
        this.IBAN = IBAN;
        this.user = user;
    }

    public String getIBAN() {
        return IBAN;
    }

    public void setIBAN(String IBAN) {
        this.IBAN = IBAN;
    }
}
