package com.example.national_bank_of_egypt.Models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TransactionLimit {
    private final StringProperty userName;
    private final DoubleProperty dailyLimit;
    private final DoubleProperty weeklyLimit;
    private final DoubleProperty dailyUsed;
    private final DoubleProperty weeklyUsed;

    public TransactionLimit(String userName, double dailyLimit, double weeklyLimit) {
        this.userName = new SimpleStringProperty(this, "userName", userName);
        this.dailyLimit = new SimpleDoubleProperty(this, "dailyLimit", dailyLimit);
        this.weeklyLimit = new SimpleDoubleProperty(this, "weeklyLimit", weeklyLimit);
        this.dailyUsed = new SimpleDoubleProperty(this, "dailyUsed", 0.0);
        this.weeklyUsed = new SimpleDoubleProperty(this, "weeklyUsed", 0.0);
    }

    public StringProperty userNameProperty() {
        return userName;
    }

    public DoubleProperty dailyLimitProperty() {
        return dailyLimit;
    }

    public DoubleProperty weeklyLimitProperty() {
        return weeklyLimit;
    }

    public DoubleProperty dailyUsedProperty() {
        return dailyUsed;
    }

    public DoubleProperty weeklyUsedProperty() {
        return weeklyUsed;
    }

    public double getDailyLimit() {
        return dailyLimit.get();
    }

    public double getWeeklyLimit() {
        return weeklyLimit.get();
    }

    public double getDailyUsed() {
        return dailyUsed.get();
    }

    public double getWeeklyUsed() {
        return weeklyUsed.get();
    }

    public void addToDailyUsed(double amount) {
        dailyUsed.set(dailyUsed.get() + amount);
    }

    public void addToWeeklyUsed(double amount) {
        weeklyUsed.set(weeklyUsed.get() + amount);
    }

    public boolean isDailyLimitExceeded(double amount) {
        return (dailyUsed.get() + amount) > dailyLimit.get();
    }

    public boolean isWeeklyLimitExceeded(double amount) {
        return (weeklyUsed.get() + amount) > weeklyLimit.get();
    }
    
    public boolean isDailyLimitApproaching(double amount) {
        double threshold = dailyLimit.get() * 0.8; // 80% threshold
        return (dailyUsed.get() + amount) >= threshold && (dailyUsed.get() + amount) < dailyLimit.get();
    }
    
    public boolean isWeeklyLimitApproaching(double amount) {
        double threshold = weeklyLimit.get() * 0.8; // 80% threshold
        return (weeklyUsed.get() + amount) >= threshold && (weeklyUsed.get() + amount) < weeklyLimit.get();
    }
    
    public double getDailyLimitRemaining() {
        return dailyLimit.get() - dailyUsed.get();
    }
    
    public double getWeeklyLimitRemaining() {
        return weeklyLimit.get() - weeklyUsed.get();
    }
}

