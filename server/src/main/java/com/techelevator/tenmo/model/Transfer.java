package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {

    private Integer transferId;
    private Integer sender;
    private String senderUsername;
    private Integer recipient;
    private String recipientUsername;
    private BigDecimal amount;
    private String transferType;
    private Boolean requestApproved;
    private String transferStatus;

    public Transfer(Integer transferId, Integer sender, Integer recipient, BigDecimal amount, String transferType, Boolean requestApproved, String transferStatus) {

        this.transferId = transferId;
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.transferType = transferType;
        this.requestApproved = requestApproved;
        this.transferStatus = transferStatus;
}

    public Transfer() {

    }

    public Integer getTransferId() {
        return transferId;
    }

    public void setTransferId(Integer transferId) {
        this.transferId = transferId;
    }

    public Integer getSender() {
        return sender;
    }

    public void setSender(Integer sender) {
        this.sender = sender;
    }

    public Integer getRecipient() {
        return recipient;
    }

    public void setRecipient(Integer recipient) {
        this.recipient = recipient;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public Boolean getRequestApproved() {
        return requestApproved;
    }

    public void setRequestApproved(Boolean requestApproved) {
        this.requestApproved = requestApproved;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getRecipientUsername() {
        return recipientUsername;
    }

    public void setRecipientUsername(String recipientUsername) {
        this.recipientUsername = recipientUsername;
    }
}
