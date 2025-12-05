package com.example.national_bank_of_egypt.Models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class User {
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty email;
    private final StringProperty phoneNumber;
    private final StringProperty address;
    private final StringProperty userName;
    private final StringProperty password;
    private final ObjectProperty<LocalDate> dateCreated;
    private final ObservableList<BankAccount> bankAccounts;
    private final StringProperty twoFactorEnabled;

    public User(String firstName, String lastName, String email, String phoneNumber, 
                String address, String userName, String password, LocalDate dateCreated) {
        this.firstName = new SimpleStringProperty(this, "firstName", firstName);
        this.lastName = new SimpleStringProperty(this, "lastName", lastName);
        this.email = new SimpleStringProperty(this, "email", email);
        this.phoneNumber = new SimpleStringProperty(this, "phoneNumber", phoneNumber);
        this.address = new SimpleStringProperty(this, "address", address);
        this.userName = new SimpleStringProperty(this, "userName", userName);
        this.password = new SimpleStringProperty(this, "password", password);
        this.dateCreated = new SimpleObjectProperty<>(this, "dateCreated", dateCreated);
        this.bankAccounts = FXCollections.observableArrayList();
        this.twoFactorEnabled = new SimpleStringProperty(this, "twoFactorEnabled", "false");
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public StringProperty addressProperty() {
        return address;
    }

    public StringProperty userNameProperty() {
        return userName;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public ObjectProperty<LocalDate> dateCreatedProperty() {
        return dateCreated;
    }

    public ObservableList<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    public StringProperty twoFactorEnabledProperty() {
        return twoFactorEnabled;
    }

    public String getFirstName() {
        return firstName.get();
    }

    public String getLastName() {
        return lastName.get();
    }

    public String getEmail() {
        return email.get();
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public String getUserName() {
        return userName.get();
    }

    public String getPassword() {
        return password.get();
    }
}

