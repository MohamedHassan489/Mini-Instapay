package com.example.national_bank_of_egypt.Models;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Transaction {
    private final StringProperty transactionId;
    private final StringProperty sender;
    private final StringProperty receiver;
    private final StringProperty senderAccount;
    private final StringProperty receiverAccount;
    private final DoubleProperty amount;
    private final ObjectProperty<LocalDate> date;
    private final StringProperty message;
    private final StringProperty status;
    private final StringProperty transactionType;

    public Transaction(String transactionId, String sender, String receiver, 
                      String senderAccount, String receiverAccount, double amount, 
                      LocalDate date, String message, String status, String transactionType) {
        this.transactionId = new SimpleStringProperty(this, "transactionId", transactionId);
        this.sender = new SimpleStringProperty(this, "sender", sender);
        this.receiver = new SimpleStringProperty(this, "receiver", receiver);
        this.senderAccount = new SimpleStringProperty(this, "senderAccount", senderAccount);
        this.receiverAccount = new SimpleStringProperty(this, "receiverAccount", receiverAccount);
        this.amount = new SimpleDoubleProperty(this, "amount", amount);
        this.date = new SimpleObjectProperty<>(this, "date", date);
        this.message = new SimpleStringProperty(this, "message", message);
        this.status = new SimpleStringProperty(this, "status", status);
        this.transactionType = new SimpleStringProperty(this, "transactionType", transactionType);
    }

    public StringProperty transactionIdProperty() {
        return transactionId;
    }

    public StringProperty senderProperty() {
        return sender;
    }

    public StringProperty receiverProperty() {
        return receiver;
    }

    public StringProperty senderAccountProperty() {
        return senderAccount;
    }

    public StringProperty receiverAccountProperty() {
        return receiverAccount;
    }

    public DoubleProperty amountProperty() {
        return amount;
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public StringProperty messageProperty() {
        return message;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public StringProperty transactionTypeProperty() {
        return transactionType;
    }

    public String getTransactionId() {
        return transactionId.get();
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public double getAmount() {
        return amount.get();
    }

    public String getSender() {
        return sender.get();
    }

    public String getReceiver() {
        return receiver.get();
    }

    public LocalDate getDate() {
        return date.get();
    }

    public String getReceiverAccount() {
        return receiverAccount.get();
    }

    public String getMessage() {
        return message.get();
    }

    public String getTransactionType() {
        return transactionType.get();
    }

    public String getSenderAccount() {
        return senderAccount.get();
    }
}

