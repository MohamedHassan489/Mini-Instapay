package com.example.national_bank_of_egypt;

import com.example.national_bank_of_egypt.Models.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Feature 6: Update Personal Information
 * Tests: Users can update profile details
 */
public class PersonalInformationTest {
    private Model model;

    @BeforeEach
    void setUp() {
        model = Model.getInstance();
        model.registerUser("John", "Doe", "john@test.com", "1111111111",
            "123 Main St", "johndoe", "password123");
        model.evaluateUserCred("johndoe", "password123");
    }

    @Test
    void testUpdatePersonalInformation() {
        // Test: Updating profile should succeed
        boolean result = model.updateUserProfile(
            "John", "Smith", "john.smith@test.com", "2222222222", "456 New St"
        );
        
        assertTrue(result, "Profile update should succeed");
        assertEquals("John", model.getCurrentUser().getFirstName(), "First name should be updated");
        assertEquals("Smith", model.getCurrentUser().getLastName(), "Last name should be updated");
        assertEquals("john.smith@test.com", model.getCurrentUser().getEmail(), "Email should be updated");
        assertEquals("2222222222", model.getCurrentUser().getPhoneNumber(), "Phone should be updated");
    }

    @Test
    void testUpdateWithDuplicateEmail() {
        // Create another user
        model.registerUser("Jane", "Doe", "jane@test.com", "3333333333",
            "789 Other St", "janedoe", "password456");
        
        // Try to update with duplicate email
        boolean result = model.updateUserProfile(
            "John", "Doe", "jane@test.com", "1111111111", "123 Main St"
        );
        assertFalse(result, "Profile update should fail with duplicate email");
    }

    @Test
    void testUpdateWhenNotLoggedIn() {
        // Test: Update should fail when no user is logged in
        model.setCurrentUser(null);
        boolean result = model.updateUserProfile(
            "John", "Doe", "john@test.com", "1111111111", "123 Main St"
        );
        assertFalse(result, "Profile update should fail when not logged in");
    }
}

