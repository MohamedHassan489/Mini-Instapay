package com.example.national_bank_of_egypt.Reports;

import java.time.LocalDate;

public class TransactionSummary {
    private final int totalTransactions;
    private final double totalAmount;
    private final double averageAmount;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public TransactionSummary(int totalTransactions, double totalAmount, double averageAmount, 
                              LocalDate startDate, LocalDate endDate) {
        this.totalTransactions = totalTransactions;
        this.totalAmount = totalAmount;
        this.averageAmount = averageAmount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getAverageAmount() {
        return averageAmount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return String.format("Transaction Summary (%s to %s):\n" +
                "Total Transactions: %d\n" +
                "Total Amount: $%.2f\n" +
                "Average Amount: $%.2f",
                startDate, endDate, totalTransactions, totalAmount, averageAmount);
    }
}

