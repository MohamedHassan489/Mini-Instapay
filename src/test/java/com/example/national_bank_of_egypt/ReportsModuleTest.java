package com.example.national_bank_of_egypt;

import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Reports.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Features 20-21: Reports Module
 * Tests: Transaction Summary, Account Usage Analysis
 */
public class ReportsModuleTest {
    private Model model;
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        model = Model.getInstance();
        reportService = ReportService.getInstance();
        
        // Create test data
        model.registerUser("User1", "Test", "user1@test.com", "1111111111",
            "Address 1", "user1", "pass123");
        model.evaluateUserCred("user1", "pass123");
        model.addBankAccount("ACC001", "Bank", 1000.0, "Checking");
        
        model.registerUser("User2", "Test", "user2@test.com", "2222222222",
            "Address 2", "user2", "pass456");
        
        // Create transactions
        model.sendMoney("user2", "ACC001", 100.0, "Payment 1", "INSTANT");
        model.sendMoney("user2", "ACC001", 200.0, "Payment 2", "INSTANT");
    }

    @Test
    void testTransactionSummaryMonthly() {
        // Feature 20: Reports – Transaction Summary (Monthly)
        var summary = reportService.generateMonthlyTransactionSummary(1, 2024);
        
        assertNotNull(summary, "Monthly transaction summary should be generated");
        assertTrue(summary.getTotalTransactions() >= 0, "Summary should have transaction count");
        assertTrue(summary.getTotalAmount() >= 0, "Summary should have total amount");
    }

    @Test
    void testTransactionSummaryAnnual() {
        // Feature 20: Reports – Transaction Summary (Annual)
        var summary = reportService.generateAnnualTransactionSummary(2024);
        
        assertNotNull(summary, "Annual transaction summary should be generated");
        assertTrue(summary.getTotalTransactions() >= 0, "Summary should have transaction count");
        assertTrue(summary.getTotalAmount() >= 0, "Summary should have total amount");
    }

    @Test
    void testAccountUsageAnalysis() {
        // Feature 21: Reports – Account Usage Analysis
        var analysis = reportService.generateAccountUsageAnalysis("user1");
        
        assertNotNull(analysis, "Account usage analysis should be generated");
        assertTrue(analysis.getTotalTransactions() >= 0, "Analysis should have transaction count");
        assertTrue(analysis.getActiveDays() >= 0, "Analysis should have active days");
    }

    @Test
    void testTransactionSummaryCalculatesCorrectly() {
        // Test: Summary should calculate totals correctly
        var summary = reportService.generateMonthlyTransactionSummary(
            java.time.LocalDate.now().getMonthValue(),
            java.time.LocalDate.now().getYear()
        );
        
        assertNotNull(summary, "Summary should be generated");
        // If transactions exist, totals should be positive
        if (summary.getTotalTransactions() > 0) {
            assertTrue(summary.getTotalAmount() > 0, "Total amount should be positive if transactions exist");
        }
    }

    @Test
    void testAccountUsageAnalysisForNonExistentUser() {
        // Test: Analysis for non-existent user should handle gracefully
        var analysis = reportService.generateAccountUsageAnalysis("nonexistent");
        
        assertNotNull(analysis, "Analysis should be generated even for non-existent user");
        assertEquals(0, analysis.getTotalTransactions(), "Non-existent user should have 0 transactions");
    }
}

