package com.paymybuddy.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTransaction;

    private LocalDateTime dateTransaction;
    private double amount;
    private String description;

    @ManyToOne
    @JoinColumn(name = "receiver_user_account")
    private Account receiverAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_user_account")
    private Account senderUserAccount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;


    public Transaction() {
    }

    public Transaction(LocalDateTime dateTransaction, double amount, String description, Account receiverAccount, Account senderUserAccount, TransactionType transactionType) {
        this.dateTransaction = dateTransaction;
        this.amount = amount;
        this.description = description;
        this.receiverAccount= receiverAccount;
        this.senderUserAccount = senderUserAccount;
        this.transactionType = transactionType;
    }

    public Transaction(Long idTransaction, LocalDateTime dateTransaction, double amount, String description, Account receiverAccount) {
        this.idTransaction = idTransaction;
        this.dateTransaction = dateTransaction;
        this.amount = amount;
        this.description = description;
        this.receiverAccount = receiverAccount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "idTransaction=" + idTransaction +
                ", dateTransaction=" + dateTransaction +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", receiverAccount=" + receiverAccount.toString() +
                ", senderUserAccount=" + senderUserAccount.toString() +
                '}';
    }
}
