package com.paymybuddy.dto;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionDTO)) return false;
        TransactionDTO that = (TransactionDTO) o;
        return Double.compare(that.amount, amount) == 0 && Objects.equals(receiverUserName, that.receiverUserName) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(receiverUserName, description, amount);
    }

    @Override
    public String toString() {
        return "TransactionDTO{" +
                "receiverUserName='" + receiverUserName + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                '}';
    }
}
