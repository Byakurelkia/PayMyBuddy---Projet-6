package com.paymybuddy.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;


@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Account {


    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "account_id_sequence"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1000000"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    private Long idAccount;
    private double balance;



    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "receiverAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<Transaction> receivedTransactions;

    @OneToMany(mappedBy = "senderUserAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<Transaction> sentTransactions;

    public Account(double balance, User user) {
        this.balance = balance;
        this.user = user;
    }

    public Account(int i) {
        this.balance += i;
    }

    public Account(Long idAccount, User userToCreate, int i) {
        this.idAccount = idAccount;
        this.user = userToCreate;
        this.balance = i;
    }

    public Long getIdAccount() {
        return idAccount;
    }

    public void setIdAccount(Long idAccount) {
        this.idAccount = idAccount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Account{" +
                "idAccount=" + idAccount +
                ", balance=" + balance +
                ", receivedTransactions=" + receivedTransactions +
                ", sentTransactions=" + sentTransactions +
                '}';
    }
}
