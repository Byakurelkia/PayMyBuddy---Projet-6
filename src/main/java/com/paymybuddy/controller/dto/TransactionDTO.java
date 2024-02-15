package com.paymybuddy.controller.dto;

public class TransactionDTO {

    private String receiverUserName;
    private String description;
    private double amount;

    public TransactionDTO() {
    }

    public TransactionDTO(String receiverUserName, String description, double amount) {
        this.receiverUserName = receiverUserName;
        this.description = description;
        this.amount = amount;
    }

    public String getReceiverUserName() {
        return receiverUserName;
    }

    public void setReceiverUserName(String receiverUserName) {
        this.receiverUserName = receiverUserName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
