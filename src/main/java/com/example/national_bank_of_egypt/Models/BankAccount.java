package com.example.national_bank_of_egypt.Models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BankAccount {
    private final StringProperty owner;
    private final StringProperty accountNumber;
    private final StringProperty bankName;
    private final DoubleProperty balance;
    private final StringProperty accountType;

    public BankAccount(String owner, String accountNumber, String bankName, double balance, String accountType) {
        this.owner = new SimpleStringProperty(this, "owner", owner);
        this.accountNumber = new SimpleStringProperty(this, "accountNumber", accountNumber);
        this.bankName = new SimpleStringProperty(this, "bankName", bankName);
        this.balance = new SimpleDoubleProperty(this, "balance", balance);
        this.accountType = new SimpleStringProperty(this, "accountType", accountType);
    }

    public StringProperty ownerProperty() {
        return owner;
    }

    public StringProperty accountNumberProperty() {
        return accountNumber;
    }

    public StringProperty bankNameProperty() {
        return bankName;
    }

    public DoubleProperty balanceProperty() {
        return balance;
    }

    public StringProperty accountTypeProperty() {
        return accountType;
    }

    public String getAccountNumber() {
        return accountNumber.get();
    }

    public double getBalance() {
        return balance.get();
    }

    public void setBalance(double balance) {
        this.balance.set(balance);
    }

    public String getOwner() {
        return owner.get();
    }

    public String getBankName() {
        return bankName.get();
    }

    public String getAccountType() {
        return accountType.get();
    }

    @Override
    public String toString() {
        return bankName.get() + " - " + accountNumber.get();
    }
}

