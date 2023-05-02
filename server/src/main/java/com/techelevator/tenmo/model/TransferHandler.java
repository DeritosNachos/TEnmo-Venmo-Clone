package com.techelevator.tenmo.model;

import java.math.BigDecimal;



public class TransferHandler {
    private String recipient;
    private String sender;
    private BigDecimal amount;

    public TransferHandler(String recipient, String sender, BigDecimal amount) {
        this.recipient = recipient;
        this.sender = sender;
        this.amount = amount;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSender() { return sender; }

    public void setSender(String sender) { this.sender = sender; }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
