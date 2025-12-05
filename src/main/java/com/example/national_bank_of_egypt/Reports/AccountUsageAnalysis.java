package com.example.national_bank_of_egypt.Reports;

public class AccountUsageAnalysis {
    private final int totalUsers;
    private final int activeUsers;

    public AccountUsageAnalysis(int totalUsers, int activeUsers) {
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public int getActiveUsers() {
        return activeUsers;
    }

    public double getEngagementRate() {
        if (totalUsers == 0) return 0.0;
        return (double) activeUsers / totalUsers * 100.0;
    }
}

