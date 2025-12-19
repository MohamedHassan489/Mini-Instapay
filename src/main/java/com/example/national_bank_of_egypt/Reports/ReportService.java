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
                int recentTransactions = rs.getInt("recentTransactions");
                int totalTransactions = rs.getInt("totalTransactions");
                double avgTransactionAmount = rs.getDouble("avgTransactionAmount");
                return new AccountUsageAnalysis(totalUsers, activeUsers, recentTransactions, 
                                               totalTransactions, avgTransactionAmount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return new AccountUsageAnalysis(0, 0, 0, 0, 0.0);
    }

    public String generateDetailedAccountUsageReport() {
        StringBuilder report = new StringBuilder();
        try {
            ResultSet rs = dataBaseDriver.getAccountUsageDetails();
            report.append("=== DETAILED ACCOUNT USAGE ANALYSIS ===\n\n");
            report.append(String.format("%-20s %-12s %-15s %-15s %-12s\n", 
                "Username", "Accounts", "Transactions", "Total Sent", "Total Received"));
            report.append("--------------------------------------------------------------------------------\n");
            
            int count = 0;
            while (rs != null && rs.next() && count < 20) {
                String username = rs.getString("UserName");
                int accountCount = rs.getInt("accountCount");
                int transactionCount = rs.getInt("transactionCount");
                double totalSent = rs.getDouble("totalSent");
                double totalReceived = rs.getDouble("totalReceived");
                
                report.append(String.format("%-20s %-12d %-15d $%-14.2f $%-14.2f\n",
                    username, accountCount, transactionCount, totalSent, totalReceived));
                count++;
            }
            
            if (count == 0) {
                report.append("No user activity data available.\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            report.append("Error generating detailed report: ").append(e.getMessage());
        }
        
        return report.toString();
    }

    public String generateBankStatement(String userId, String startDate, String endDate, String accountNumber) {
        StringBuilder statement = new StringBuilder();
        ResultSet rs = null;
        try {
            rs = dataBaseDriver.getTransactionsByDateRange(userId, startDate, endDate, accountNumber);
            
            // Header
            statement.append("═══════════════════════════════════════════════════════════════\n");
            statement.append("                    BANK STATEMENT\n");
            statement.append("═══════════════════════════════════════════════════════════════\n\n");
            
            if (accountNumber != null && !accountNumber.isEmpty()) {
                statement.append("Account Number: ").append(accountNumber).append("\n");
            } else {
                statement.append("All Accounts\n");
            }
            statement.append("User: ").append(userId).append("\n");
            statement.append("Period: ").append(startDate != null ? startDate : "All Time");
            if (startDate != null && endDate != null) {
                statement.append(" to ").append(endDate);
            }
            statement.append("\n");
            statement.append("Generated: ").append(java.time.LocalDate.now().toString()).append("\n");
            statement.append("═══════════════════════════════════════════════════════════════\n\n");
            
            // Column headers
            statement.append(String.format("%-12s %-12s %-20s %-20s %-12s %-15s %-10s\n",
                "Date", "Type", "From/To", "Account", "Amount", "Status", "Message"));
            statement.append("─────────────────────────────────────────────────────────────────────────────\n");
            
            double totalDebits = 0.0;
            double totalCredits = 0.0;
            int transactionCount = 0;
            
            if (rs != null) {
                while (rs.next()) {
                    transactionCount++;
                    String date = rs.getString("Date");
                    String sender = rs.getString("Sender");
                    String receiver = rs.getString("Receiver");
                    String senderAccount = rs.getString("SenderAccount");
                    String receiverAccount = rs.getString("ReceiverAccount");
                    double amount = rs.getDouble("Amount");
                    String status = rs.getString("Status");
                    String message = rs.getString("Message");
                    String transactionType = rs.getString("TransactionType");
                    
                    String type;
                    String fromTo;
                    String account;
                    double displayAmount;
                    
                    if (userId.equals(sender)) {
                        type = "DEBIT";
                        fromTo = "To: " + receiver;
                        account = receiverAccount != null ? receiverAccount : "N/A";
                        displayAmount = -amount;
                        totalDebits += amount;
                    } else {
                        type = "CREDIT";
                        fromTo = "From: " + sender;
                        account = senderAccount != null ? senderAccount : "N/A";
                        displayAmount = amount;
                        totalCredits += amount;
                    }
                    
                    String typeDisplay = transactionType != null ? transactionType : "INSTANT";
                    String messageDisplay = message != null && !message.isEmpty() ? 
                        (message.length() > 10 ? message.substring(0, 10) + "..." : message) : "-";
                    
                    statement.append(String.format("%-12s %-12s %-20s %-20s $%-11.2f %-15s %-10s\n",
                        date, typeDisplay, fromTo, account, displayAmount, status, messageDisplay));
                }
            }
            
            // Summary
            statement.append("─────────────────────────────────────────────────────────────────────────────\n");
            statement.append(String.format("Total Transactions: %d\n", transactionCount));
            statement.append(String.format("Total Debits:      $%.2f\n", totalDebits));
            statement.append(String.format("Total Credits:     $%.2f\n", totalCredits));
            statement.append(String.format("Net Amount:        $%.2f\n", totalCredits - totalDebits));
            statement.append("═══════════════════════════════════════════════════════════════\n");
            
            if (transactionCount == 0) {
                statement.append("\nNo transactions found for the selected period.\n");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            statement.append("\nError generating bank statement: ").append(e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception closeEx) {
                closeEx.printStackTrace();
            }
        }
        
        return statement.toString();
    }
}

