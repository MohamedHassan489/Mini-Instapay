package com.example.national_bank_of_egypt.Utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.application.Platform;

/**
 * Centralized error handling utility
 */
public class ErrorHandler {
    
    /**
     * Show error in a Label (for inline form errors)
     */
    public static void showError(Label errorLabel, String message) {
        if (errorLabel != null && message != null) {
            Platform.runLater(() -> {
                errorLabel.setText(message);
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");
                
                // Add shake animation if AnimationUtils exists
                try {
                    AnimationUtils.shake(errorLabel, 5, AnimationUtils.STANDARD_DURATION).play();
                } catch (Exception e) {
                    // AnimationUtils not available, just show error without animation
                }
            });
        }
    }
    
    /**
     * Hide error label
     */
    public static void hideError(Label errorLabel) {
        if (errorLabel != null) {
            Platform.runLater(() -> {
                errorLabel.setVisible(false);
                errorLabel.setManaged(false);
                errorLabel.setText("");
            });
        }
    }
    
    /**
     * Show error in an Alert dialog
     */
    public static void showErrorAlert(String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title != null ? title : "Error");
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
    
    /**
     * Show error alert with exception details
     */
    public static void showErrorAlert(String title, Exception exception) {
        String message = exception.getMessage();
        if (message == null || message.isEmpty()) {
            message = "An unexpected error occurred: " + exception.getClass().getSimpleName();
        }
        showErrorAlert(title, "An error occurred", message);
    }
    
    /**
     * Show success message in a Label
     */
    public static void showSuccess(Label messageLabel, String message) {
        if (messageLabel != null && message != null) {
            Platform.runLater(() -> {
                messageLabel.setText(message);
                messageLabel.setVisible(true);
                messageLabel.setManaged(true);
                messageLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 12px;");
                
                // Fade in animation
                try {
                    AnimationUtils.fadeIn(messageLabel, AnimationUtils.STANDARD_DURATION).play();
                } catch (Exception e) {
                    // AnimationUtils not available
                }
            });
        }
    }
    
    /**
     * Show success Alert dialog
     */
    public static void showSuccessAlert(String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title != null ? title : "Success");
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
    
    /**
     * Log error to console with consistent formatting
     */
    public static void logError(String context, Exception exception) {
        System.err.println("ERROR in " + context + ": " + exception.getMessage());
        exception.printStackTrace();
    }
    
    /**
     * Get user-friendly error message from exception
     */
    public static String getUserFriendlyMessage(Exception exception) {
        String message = exception.getMessage();
        
        if (message == null || message.isEmpty()) {
            return "An unexpected error occurred. Please try again.";
        }
        
        // Check for common error patterns and make them user-friendly
        if (message.contains("UNIQUE constraint failed")) {
            if (message.contains("UserName")) {
                return "This username is already taken. Please choose another.";
            } else if (message.contains("Email")) {
                return "This email is already registered.";
            } else if (message.contains("PhoneNumber")) {
                return "This phone number is already registered.";
            } else if (message.contains("AccountNumber")) {
                return "This account number already exists.";
            }
            return "This value is already in use. Please use a different one.";
        }
        
        if (message.contains("NOT NULL constraint failed")) {
            return "Please fill in all required fields.";
        }
        
        if (message.contains("connection") || message.contains("database")) {
            return "Database connection error. Please try again later.";
        }
        
        if (message.contains("insufficient") || message.contains("balance")) {
            return "Insufficient balance for this transaction.";
        }
        
        // Return original message if no pattern matches
        return message;
    }
}
