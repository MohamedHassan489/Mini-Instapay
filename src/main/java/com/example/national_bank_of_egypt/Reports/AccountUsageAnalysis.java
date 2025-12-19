package com.example.national_bank_of_egypt.Reports;

public class AccountUsageAnalysis {
    private final int totalUsers;
    private final int activeUsers;
    private final int recentTransactions;
    private final int totalTransactions;
    private final double avgTransactionAmount;

    public AccountUsageAnalysis(int totalUsers, int activeUsers) {
        this(totalUsers, activeUsers, 0, 0, 0.0);
    }

    public AccountUsageAnalysis(int totalUsers, int activeUsers, int recentTransactions, 
                               int totalTransactions, double avgTransactionAmount) {
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.recentTransactions = recentTransactions;
        this.totalTransactions = totalTransactions;
        this.avgTransactionAmount = avgTransactionAmount;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public int getActiveUsers() {
        return activeUsers;
    }

    public int getRecentTransactions() {
        return recentTransactions;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public double getAvgTransactionAmount() {
        return avgTransactionAmount;
    }

    public double getEngagementRate() {
        if (totalUsers == 0) return 0.0;
        return (double) activeUsers / totalUsers * 100.0;
    }

    public double getTransactionsPerUser() {
        if (totalUsers == 0) return 0.0;
        return (double) totalTransactions / totalUsers;
    }
}

