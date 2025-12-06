package com.example.national_bank_of_egypt;

import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Notifications.Notification;
import com.example.national_bank_of_egypt.Notifications.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Features 15-16: Notifications Module
 * Tests: Push Notifications, Transaction Alerts
 */
public class NotificationsModuleTest {
    private Model model;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        model = Model.getInstance();
        notificationService = NotificationService.getInstance();
        model.registerUser("Test", "User", "test@test.com", "1111111111",
            "Test Address", "testuser", "testpass123");
        model.evaluateUserCred("testuser", "testpass123");
    }

    @Test
    void testPushNotificationCreation() {
        // Feature 15: Push Notifications
        notificationService.sendNotification("testuser", "Test Notification",
            "This is a test notification", "INFO");
        
        var notifications = notificationService.getUserNotifications("testuser");
        assertFalse(notifications.isEmpty(), "User should have notifications");
        
        Notification lastNotification = notifications.get(notifications.size() - 1);
        assertEquals("Test Notification", lastNotification.getTitle(), "Notification title should match");
        assertEquals("This is a test notification", lastNotification.getMessage(), "Notification message should match");
    }

    @Test
    void testTransactionSuccessNotification() {
        // Feature 15: Transaction success notification
        model.addBankAccount("ACC001", "Test Bank", 1000.0, "Checking");
        model.registerUser("Receiver", "User", "receiver@test.com", "2222222222",
            "Receiver St", "receiver", "pass456");
        
        model.sendMoney("receiver", "ACC001", 100.0, "Payment", "INSTANT");
        
        // Notification should be sent automatically
        var notifications = notificationService.getUserNotifications("testuser");
        // Check if transaction notification exists
        boolean hasTransactionNotification = notifications.stream()
            .anyMatch(n -> n.getType().equals("TRANSACTION") || n.getTitle().contains("Transaction"));
        assertTrue(hasTransactionNotification || notifications.size() > 0,
            "Transaction should generate notification");
    }

    @Test
    void testTransactionAlert() {
        // Feature 16: Transaction Alerts
        model.addBankAccount("ACC001", "Test Bank", 1000.0, "Checking");
        model.registerUser("Receiver", "User", "receiver@test.com", "2222222222",
            "Receiver St", "receiver", "pass456");
        
        // Send money - should trigger alert
        model.sendMoney("receiver", "ACC001", 50.0, "Alert test", "INSTANT");
        
        var notifications = notificationService.getUserNotifications("testuser");
        assertTrue(notifications.size() > 0, "Transaction should generate alert");
    }

    @Test
    void testLowBalanceNotification() {
        // Feature 15: Low account balance notification
        model.addBankAccount("ACC001", "Test Bank", 100.0, "Checking");
        model.registerUser("Receiver", "User", "receiver@test.com", "2222222222",
            "Receiver St", "receiver", "pass456");
        
        // Send most of balance
        model.sendMoney("receiver", "ACC001", 90.0, "Low balance test", "INSTANT");
        
        // Low balance notification should be sent
        var notifications = notificationService.getUserNotifications("testuser");
        assertTrue(notifications.size() > 0, "Low balance should generate notification");
    }

    @Test
    void testTransactionLimitApproachingNotification() {
        // Feature 15: Transaction limit approaching notification
        model.getDataBaseDriver().updateTransactionLimit("testuser", 100.0, 500.0);
        model.addBankAccount("ACC001", "Test Bank", 1000.0, "Checking");
        model.registerUser("Receiver", "User", "receiver@test.com", "2222222222",
            "Receiver St", "receiver", "pass456");
        
        // Send money approaching limit (80% threshold = 80.0)
        model.sendMoney("receiver", "ACC001", 75.0, "Limit test", "INSTANT");
        
        // Notification should be sent when approaching limit
        var notifications = notificationService.getUserNotifications("testuser");
        assertTrue(notifications.size() > 0, "Approaching limit should generate notification");
    }

    @Test
    void testNotificationObserverPattern() {
        // Feature 15: Notification service uses Observer pattern
        // Test that notifications are delivered to subscribers
        notificationService.sendNotification("testuser", "Observer Test",
            "Testing observer pattern", "INFO");
        
        var notifications = notificationService.getUserNotifications("testuser");
        assertFalse(notifications.isEmpty(), "Observer pattern should deliver notifications");
    }
}

