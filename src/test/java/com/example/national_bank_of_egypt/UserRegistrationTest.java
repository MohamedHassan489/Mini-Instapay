package com.example.national_bank_of_egypt;

import com.example.national_bank_of_egypt.Models.DataBaseDriver;
import com.example.national_bank_of_egypt.Models.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import java.io.File;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Feature 1: User Registration
 * Tests: Users can register with credentials
 */
public class UserRegistrationTest {
    private Model model;
    private DataBaseDriver dbDriver;
    private static final String TEST_DB = "TestMiniInstaPay.db";

    @BeforeEach
    void setUp() {
        // Use test database
        model = Model.getInstance();
        dbDriver = model.getDataBaseDriver();
    }

    @AfterEach
    void tearDown() {
        // Clean up test database
        File dbFile = new File(TEST_DB);
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Test
    void testSuccessfulRegistration() {
        // Test: Valid registration should succeed
        boolean result = model.registerUser(
            "John", "Doe", "john@test.com", "1234567890",
            "123 Test St", "johndoe", "password123"
        );
        assertTrue(result, "Registration should succeed with valid data");
    }

    @Test
    void testRegistrationWithDuplicateUsername() {
        // Test: Duplicate username should fail
        model.registerUser("John", "Doe", "john@test.com", "1234567890",
            "123 Test St", "johndoe", "password123");
        
        boolean result = model.registerUser("Jane", "Smith", "jane@test.com", "0987654321",
            "456 Test St", "johndoe", "password456");
        assertFalse(result, "Registration should fail with duplicate username");
    }

    @Test
    void testRegistrationWithDuplicateEmail() {
        // Test: Duplicate email should fail
        model.registerUser("John", "Doe", "john@test.com", "1234567890",
            "123 Test St", "johndoe", "password123");
        
        boolean result = model.registerUser("Jane", "Smith", "john@test.com", "0987654321",
            "456 Test St", "janesmith", "password456");
        assertFalse(result, "Registration should fail with duplicate email");
    }

    @Test
    void testRegistrationWithDuplicatePhone() {
        // Test: Duplicate phone number should fail
        model.registerUser("John", "Doe", "john@test.com", "1234567890",
            "123 Test St", "johndoe", "password123");
        
        boolean result = model.registerUser("Jane", "Smith", "jane@test.com", "1234567890",
            "456 Test St", "janesmith", "password456");
        assertFalse(result, "Registration should fail with duplicate phone number");
    }

    @Test
    void testRegistrationCreatesTransactionLimits() {
        // Test: Registration should automatically create transaction limits
        boolean registered = model.registerUser(
            "John", "Doe", "john@test.com", "1234567890",
            "123 Test St", "johndoe", "password123"
        );
        assertTrue(registered);
        
        // Check if transaction limits were created
        var limits = model.getTransactionLimit("johndoe");
        assertNotNull(limits, "Transaction limits should be created on registration");
        assertEquals(5000.0, limits.getDailyLimit(), "Default daily limit should be 5000");
        assertEquals(20000.0, limits.getWeeklyLimit(), "Default weekly limit should be 20000");
    }
}

