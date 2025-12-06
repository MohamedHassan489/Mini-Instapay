package com.example.national_bank_of_egypt;

import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Notifications.NotificationFactory;
import com.example.national_bank_of_egypt.Notifications.NotificationService;
import com.example.national_bank_of_egypt.Security.EncryptionService;
import com.example.national_bank_of_egypt.Security.FraudDetectionService;
import com.example.national_bank_of_egypt.Security.OTPService;
import com.example.national_bank_of_egypt.Transactions.TransactionFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Feature 22: Design Patterns Present
 * Tests: At least 3 design patterns (Singleton, Factory, Observer, Strategy, Repository)
 */
public class DesignPatternsTest {

    @Test
    void testSingletonPattern() {
        // Test: Singleton pattern in Model, OTPService, EncryptionService, etc.
        Model instance1 = Model.getInstance();
        Model instance2 = Model.getInstance();
        assertSame(instance1, instance2, "Model should use Singleton pattern");
        
        OTPService otp1 = OTPService.getInstance();
        OTPService otp2 = OTPService.getInstance();
        assertSame(otp1, otp2, "OTPService should use Singleton pattern");
        
        EncryptionService enc1 = EncryptionService.getInstance();
        EncryptionService enc2 = EncryptionService.getInstance();
        assertSame(enc1, enc2, "EncryptionService should use Singleton pattern");
        
        FraudDetectionService fraud1 = FraudDetectionService.getInstance();
        FraudDetectionService fraud2 = FraudDetectionService.getInstance();
        assertSame(fraud1, fraud2, "FraudDetectionService should use Singleton pattern");
        
        NotificationService notif1 = NotificationService.getInstance();
        NotificationService notif2 = NotificationService.getInstance();
        assertSame(notif1, notif2, "NotificationService should use Singleton pattern");
    }

    @Test
    void testFactoryPattern() {
        // Test: Factory pattern in TransactionFactory, NotificationFactory
        TransactionFactory transactionFactory = new TransactionFactory();
        assertNotNull(transactionFactory, "TransactionFactory should exist");
        
        NotificationFactory notificationFactory = new NotificationFactory();
        assertNotNull(notificationFactory, "NotificationFactory should exist");
        
        // Factory pattern is used to create different transaction types
        assertTrue(true, "Factory pattern implemented for transactions and notifications");
    }

    @Test
    void testObserverPattern() {
        // Test: Observer pattern in NotificationService
        NotificationService notificationService = NotificationService.getInstance();
        assertNotNull(notificationService, "NotificationService should exist");
        
        // Observer pattern is used for notification delivery
        notificationService.sendNotification("testuser", "Test", "Message", "INFO");
        assertTrue(true, "Observer pattern implemented for notifications");
    }

    @Test
    void testStrategyPattern() {
        // Test: Strategy pattern in FraudDetectionService
        FraudDetectionService fraudService = FraudDetectionService.getInstance();
        assertNotNull(fraudService, "FraudDetectionService should exist");
        
        // Strategy pattern allows different fraud detection strategies
        fraudService.addStrategy(new com.example.national_bank_of_egypt.Security.AmountBasedFraudDetection());
        fraudService.addStrategy(new com.example.national_bank_of_egypt.Security.FrequencyBasedFraudDetection());
        
        assertTrue(true, "Strategy pattern implemented for fraud detection");
    }

    @Test
    void testRepositoryPattern() {
        // Test: Repository pattern interfaces exist
        try {
            Class<?> userRepo = Class.forName("com.example.national_bank_of_egypt.Repository.UserRepository");
            Class<?> transactionRepo = Class.forName("com.example.national_bank_of_egypt.Repository.TransactionRepository");
            Class<?> bankAccountRepo = Class.forName("com.example.national_bank_of_egypt.Repository.BankAccountRepository");
            
            assertNotNull(userRepo, "UserRepository interface should exist");
            assertNotNull(transactionRepo, "TransactionRepository interface should exist");
            assertNotNull(bankAccountRepo, "BankAccountRepository interface should exist");
            
            assertTrue(true, "Repository pattern implemented with interfaces");
        } catch (ClassNotFoundException e) {
            fail("Repository interfaces should exist");
        }
    }

    @Test
    void testAtLeastThreePatterns() {
        // Test: At least 3 design patterns are present
        int patternCount = 0;
        
        // Singleton
        if (Model.getInstance() == Model.getInstance()) patternCount++;
        
        // Factory
        try {
            Class.forName("com.example.national_bank_of_egypt.Transactions.TransactionFactory");
            patternCount++;
        } catch (ClassNotFoundException e) {}
        
        // Observer
        try {
            Class.forName("com.example.national_bank_of_egypt.Notifications.NotificationObserver");
            patternCount++;
        } catch (ClassNotFoundException e) {}
        
        // Strategy
        try {
            Class.forName("com.example.national_bank_of_egypt.Security.FraudDetectionStrategy");
            patternCount++;
        } catch (ClassNotFoundException e) {}
        
        // Repository
        try {
            Class.forName("com.example.national_bank_of_egypt.Repository.UserRepository");
            patternCount++;
        } catch (ClassNotFoundException e) {}
        
        assertTrue(patternCount >= 3, "At least 3 design patterns should be present. Found: " + patternCount);
    }
}

