package com.example.national_bank_of_egypt.Utils;

import javafx.scene.control.Label;
import javafx.scene.Node;

/**
 * Utility class for displaying error and success messages with animations.
 */
public class MessageUtils {
    
    /**
     * Show an error message with shake animation
     * @param errorLabel The label to display the error
     * @param message The error message
     */
    public static void showError(Label errorLabel, String message) {
        if (errorLabel == null) return;
        
        errorLabel.setText(message);
        errorLabel.getStyleClass().removeAll("label-success", "label-warning");
        if (!errorLabel.getStyleClass().contains("label-error")) {
            errorLabel.getStyleClass().add("label-error");
        }
        errorLabel.setVisible(true);
        
        // Add shake animation
        AnimationUtils.shake(errorLabel, 10, AnimationUtils.STANDARD_DURATION).play();
    }
    
    /**
     * Show a success message
     * @param label The label to display the success message
     * @param message The success message
     */
    public static void showSuccess(Label label, String message) {
        if (label == null) return;
        
        label.setText(message);
        label.getStyleClass().removeAll("label-error", "label-warning");
        if (!label.getStyleClass().contains("label-success")) {
            label.getStyleClass().add("label-success");
        }
        label.setVisible(true);
        
        // Add fade-in animation
        label.setOpacity(0);
        AnimationUtils.fadeIn(label, AnimationUtils.STANDARD_DURATION).play();
    }
    
    /**
     * Clear the message label
     * @param label The label to clear
     */
    public static void clearMessage(Label label) {
        if (label == null) return;
        label.setText("");
        label.setVisible(false);
    }
    
    /**
     * Show validation error on an input field
     * @param field The input field to mark as error
     */
    public static void markFieldError(Node field) {
        if (field == null) return;
        field.getStyleClass().removeAll("input-standard");
        if (!field.getStyleClass().contains("input-error")) {
            field.getStyleClass().add("input-error");
        }
        AnimationUtils.shake(field, 5, AnimationUtils.QUICK_DURATION).play();
    }
    
    /**
     * Mark input field as valid
     * @param field The input field to mark as valid
     */
    public static void markFieldValid(Node field) {
        if (field == null) return;
        field.getStyleClass().removeAll("input-error");
        if (!field.getStyleClass().contains("input-standard")) {
            field.getStyleClass().add("input-standard");
        }
    }
}

