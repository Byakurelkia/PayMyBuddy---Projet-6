package com.paymybuddy.controller.dto;

public class TransferDTO {
    private String description;
    private double amount;
    private String friendMail;

    public TransferDTO(String description, double amount, String friendMail) {
        this.description = description;
        this.amount = amount;
        this.friendMail = friendMail;
    }

    public TransferDTO() {
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

    public String getFriendMail() {
        return friendMail;
    }

    public void setFriendMail(String friendMail) {
        this.friendMail = friendMail;
    }
}
