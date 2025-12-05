package com.example.national_bank_of_egypt.Reports;

import com.example.national_bank_of_egypt.Models.DataBaseDriver;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReportService {
    private static ReportService instance;
    private final DataBaseDriver dataBaseDriver;

    private ReportService() {
        this.dataBaseDriver = new DataBaseDriver();
    }

    public static synchronized ReportService getInstance() {
        if (instance == null) {
            instance = new ReportService();
        }
        return instance;
    }

    public TransactionSummary generateMonthlyReport(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        
        try {
            ResultSet rs = dataBaseDriver.getTransactionStats(
                startDate.format(DateTimeFormatter.ISO_DATE),
                endDate.format(DateTimeFormatter.ISO_DATE)
            );
            
            if (rs != null && rs.next()) {
                int totalTransactions = rs.getInt("total");
                double totalAmount = rs.getDouble("totalAmount");
                double avgAmount = rs.getDouble("avgAmount");
                return new TransactionSummary(totalTransactions, totalAmount, avgAmount, startDate, endDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return new TransactionSummary(0, 0.0, 0.0, startDate, endDate);
    }

    public TransactionSummary generateAnnualReport(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        
        try {
            ResultSet rs = dataBaseDriver.getTransactionStats(
                startDate.format(DateTimeFormatter.ISO_DATE),
                endDate.format(DateTimeFormatter.ISO_DATE)
            );
            
            if (rs != null && rs.next()) {
                int totalTransactions = rs.getInt("total");
                double totalAmount = rs.getDouble("totalAmount");
                double avgAmount = rs.getDouble("avgAmount");
                return new TransactionSummary(totalTransactions, totalAmount, avgAmount, startDate, endDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return new TransactionSummary(0, 0.0, 0.0, startDate, endDate);
    }

    public AccountUsageAnalysis generateAccountUsageAnalysis() {
        try {
            ResultSet rs = dataBaseDriver.getUserActivityStats();
            if (rs != null && rs.next()) {
                int totalUsers = rs.getInt("totalUsers");
                int activeUsers = rs.getInt("activeUsers");
                return new AccountUsageAnalysis(totalUsers, activeUsers);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return new AccountUsageAnalysis(0, 0);
    }
}

