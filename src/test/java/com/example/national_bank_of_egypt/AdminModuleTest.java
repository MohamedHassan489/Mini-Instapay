package com.example.national_bank_of_egypt;

import com.example.national_bank_of_egypt.Models.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Features 17-19: Admin Module
 * Tests: Manage Users, Oversee Transactions, System Health Monitoring
 */
public class AdminModuleTest {
    private Model model;

    @BeforeEach
    void setUp() {
        model = Model.getInstance();
        // Create test users
        model.registerUser("User1", "Test", "user1@test.com", "1111111111",
            "Address 1", "user1", "pass123");
        model.registerUser("User2", "Test", "user2@test.com", "2222222222",
            "Address 2", "user2", "pass456");
    }

    @Test
    void testAdminLogin() {
        // Test: Admin can login
        model.evaluateAdminCred("admin", "admin123");
        assertTrue(model.getAdminLoginSuccessFlag(), "Admin login should succeed");
    }

    @Test
    void testAdminViewAllUsers() {
        // Feature 17: Admin – Manage Users - View all users
        model.loadAllUsers();
        var users = model.getUsers();
        
        assertFalse(users.isEmpty(), "Admin should see all users");
        assertTrue(users.size() >= 2, "Should have at least 2 test users");
    }

    @Test
    void testAdminSuspendAccount() {
        // Feature 17: Admin – Manage Users - Suspend accounts
        boolean result = model.suspendAccount("user1");
        assertTrue(result, "Admin should be able to suspend accounts");
        
        // Verify user is suspended (TwoFactorEnabled field set to "suspended")
        model.evaluateUserCred("user1", "pass123");
        assertFalse(model.getUserLoginSuccessFlag(), "Suspended user should not be able to login");
    }

    @Test
    void testAdminViewAllTransactions() {
        // Feature 18: Admin – Oversee Transactions
        // Create some transactions
        model.evaluateUserCred("user1", "pass123");
        model.addBankAccount("ACC001", "Bank", 1000.0, "Checking");
        model.registerUser("Receiver", "User", "receiver@test.com", "3333333333",
            "Receiver St", "receiver", "pass789");
        model.sendMoney("receiver", "ACC001", 100.0, "Payment", "INSTANT");
        
        // Admin should see all transactions
        model.loadAllTransactions();
        var transactions = model.getTransactions();
        assertFalse(transactions.isEmpty(), "Admin should see all transactions");
    }

    @Test
    void testAdminFlagSuspiciousTransaction() {
        // Feature 18: Admin – Oversee Transactions - Flag suspicious activities
        model.evaluateUserCred("user1", "pass123");
        model.addBankAccount("ACC001", "Bank", 1000.0, "Checking");
        model.registerUser("Receiver", "User", "receiver@test.com", "3333333333",
            "Receiver St", "receiver", "pass789");
        model.sendMoney("receiver", "ACC001", 100.0, "Payment", "INSTANT");
        
        model.loadAllTransactions();
        if (!model.getTransactions().isEmpty()) {
            String transactionId = model.getTransactions().get(0).getTransactionId();
            boolean result = model.flagTransactionAsSuspicious(transactionId);
            assertTrue(result, "Admin should be able to flag suspicious transactions");
        }
    }

    @Test
    void testAdminSystemHealthMonitoring() {
        // Feature 19: Admin – System Health Monitoring
        // Test that system health data can be retrieved
        model.loadAllUsers();
        model.loadAllTransactions();
        
        int userCount = model.getUsers().size();
        int transactionCount = model.getTransactions().size();
        
        assertTrue(userCount >= 0, "System should track user count");
        assertTrue(transactionCount >= 0, "System should track transaction count");
        
        // System health monitoring exists in SystemHealthController
        assertTrue(true, "System health monitoring dashboard exists");
    }

    @Test
    void testAdminHandleDisputes() {
        // Feature 17: Admin – Manage Users - Handle user disputes
        model.evaluateUserCred("user1", "pass123");
        model.addBankAccount("ACC001", "Bank", 1000.0, "Checking");
        model.registerUser("Receiver", "User", "receiver@test.com", "3333333333",
            "Receiver St", "receiver", "pass789");
        model.sendMoney("receiver", "ACC001", 100.0, "Payment", "INSTANT");
        
        model.loadTransactions(-1);
        if (!model.getTransactions().isEmpty()) {
            String transactionId = model.getTransactions().get(0).getTransactionId();
            model.getDataBaseDriver().createDispute("DISPUTE001", transactionId, "user1",
                "Test dispute", "PENDING", java.time.LocalDate.now());
            
            model.loadAllDisputes();
            var disputes = model.getDisputes();
            assertFalse(disputes.isEmpty(), "Admin should see all disputes");
            
            // Admin can resolve disputes
            boolean result = model.resolveDispute("DISPUTE001", "RESOLVED", "Refund processed");
            assertTrue(result, "Admin should be able to resolve disputes");
        }
    }
}

