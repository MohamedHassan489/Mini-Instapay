package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Models.TransactionLimit;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {
    public TextField firstName_fld;
    public TextField lastName_fld;
    public TextField email_fld;
    public TextField phoneNumber_fld;
    public TextField address_fld;
    public Button update_btn;
    public Label error_lbl;
    public TextField dailyLimit_fld;
    public TextField weeklyLimit_fld;
    public Button updateLimits_btn;
    public Label limits_error_lbl;
    public Label dailyUsed_lbl;
    public Label weeklyUsed_lbl;
    public CheckBox twoFactor_checkbox;
    public Button update2FA_btn;
    public Label twoFactor_status_lbl;
    public Label twoFactor_error_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            if (Model.getInstance().getCurrentUser() != null) {
                // Load profile data
                if (firstName_fld != null) {
                    firstName_fld.setText(Model.getInstance().getCurrentUser().getFirstName());
                }
                if (lastName_fld != null) {
                    lastName_fld.setText(Model.getInstance().getCurrentUser().getLastName());
                }
                if (email_fld != null) {
                    email_fld.setText(Model.getInstance().getCurrentUser().getEmail());
                }
                if (phoneNumber_fld != null) {
                    phoneNumber_fld.setText(Model.getInstance().getCurrentUser().getPhoneNumber());
                }
                if (address_fld != null) {
                    address_fld.setText(Model.getInstance().getCurrentUser().addressProperty().get());
                }
                
                // Load 2FA status
                load2FAStatus();
                
                // Load transaction limits
                loadTransactionLimits();
            }
            
            if (update_btn != null) {
                update_btn.setOnAction(event -> onUpdateProfile());
            }
            
            if (updateLimits_btn != null) {
                updateLimits_btn.setOnAction(event -> onUpdateLimits());
            }
            
            if (update2FA_btn != null) {
                update2FA_btn.setOnAction(event -> onUpdate2FA());
            }
            
            System.out.println("ProfileController initialized successfully");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in ProfileController.initialize: " + e.getMessage());
        }
    }
    
    private void load2FAStatus() {
        if (Model.getInstance().getCurrentUser() != null) {
            String twoFactorEnabled = Model.getInstance().getCurrentUser().twoFactorEnabledProperty().get();
            boolean isEnabled = "true".equalsIgnoreCase(twoFactorEnabled);
            
            if (twoFactor_checkbox != null) {
                twoFactor_checkbox.setSelected(isEnabled);
            }
            
            if (twoFactor_status_lbl != null) {
                if (isEnabled) {
                    twoFactor_status_lbl.setText("(Currently Enabled)");
                    twoFactor_status_lbl.setStyle("-fx-text-fill: green;");
                } else {
                    twoFactor_status_lbl.setText("(Currently Disabled)");
                    twoFactor_status_lbl.setStyle("-fx-text-fill: #666;");
                }
            }
        }
    }
    
    private void onUpdate2FA() {
        if (twoFactor_error_lbl != null) {
            twoFactor_error_lbl.setText("");
        }
        
        if (Model.getInstance().getCurrentUser() == null) {
            if (twoFactor_error_lbl != null) {
                twoFactor_error_lbl.setText("User not logged in");
            }
            return;
        }
        
        boolean enable2FA = twoFactor_checkbox != null && twoFactor_checkbox.isSelected();
        
        // Update 2FA setting in database
        if (Model.getInstance().getDataBaseDriver().updateTwoFactorEnabled(
                Model.getInstance().getCurrentUser().getUserName(), enable2FA)) {
            
            // Update current user object
            Model.getInstance().getCurrentUser().twoFactorEnabledProperty().set(enable2FA ? "true" : "false");
            
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Success");
            successAlert.setHeaderText("2FA Setting Updated");
            successAlert.setContentText("Two-Factor Authentication has been " + (enable2FA ? "enabled" : "disabled") + " successfully!");
            successAlert.showAndWait();
            
            if (twoFactor_error_lbl != null) {
                twoFactor_error_lbl.setText("2FA setting updated successfully!");
                twoFactor_error_lbl.setStyle("-fx-text-fill: green;");
            }
            
            // Reload 2FA status
            load2FAStatus();
        } else {
            if (twoFactor_error_lbl != null) {
                twoFactor_error_lbl.setText("Failed to update 2FA setting. Please try again.");
                twoFactor_error_lbl.setStyle("-fx-text-fill: red;");
            }
        }
    }
    
    private void loadTransactionLimits() {
        if (Model.getInstance().getCurrentUser() != null) {
            TransactionLimit limit = Model.getInstance().getTransactionLimit(Model.getInstance().getCurrentUser().getUserName());
            if (limit != null) {
                if (dailyLimit_fld != null) {
                    dailyLimit_fld.setText(String.format("%.2f", limit.getDailyLimit()));
                }
                if (weeklyLimit_fld != null) {
                    weeklyLimit_fld.setText(String.format("%.2f", limit.getWeeklyLimit()));
                }
                if (dailyUsed_lbl != null) {
                    dailyUsed_lbl.setText(String.format("Daily Used: $%.2f / $%.2f", limit.getDailyUsed(), limit.getDailyLimit()));
                }
                if (weeklyUsed_lbl != null) {
                    weeklyUsed_lbl.setText(String.format("Weekly Used: $%.2f / $%.2f", limit.getWeeklyUsed(), limit.getWeeklyLimit()));
                }
            }
        }
    }
    
    private void onUpdateLimits() {
        // Clear previous error
        if (limits_error_lbl != null) {
            limits_error_lbl.setText("");
        }
        
        if (Model.getInstance().getCurrentUser() == null) {
            if (limits_error_lbl != null) {
                limits_error_lbl.setText("User not logged in");
            }
            return;
        }
        
        String dailyLimitStr = dailyLimit_fld != null ? dailyLimit_fld.getText().trim() : "";
        String weeklyLimitStr = weeklyLimit_fld != null ? weeklyLimit_fld.getText().trim() : "";
        
        // Validate input
        if (dailyLimitStr.isEmpty() || weeklyLimitStr.isEmpty()) {
            if (limits_error_lbl != null) {
                limits_error_lbl.setText("Please fill all fields");
            }
            return;
        }
        
        double dailyLimit, weeklyLimit;
        try {
            dailyLimit = Double.parseDouble(dailyLimitStr);
            weeklyLimit = Double.parseDouble(weeklyLimitStr);
            
            if (dailyLimit <= 0 || weeklyLimit <= 0) {
                if (limits_error_lbl != null) {
                    limits_error_lbl.setText("Limits must be greater than 0");
                }
                return;
            }
            
            if (dailyLimit > 100000 || weeklyLimit > 500000) {
                if (limits_error_lbl != null) {
                    limits_error_lbl.setText("Limits exceed maximum allowed (Daily: $100,000, Weekly: $500,000)");
                }
                return;
            }
            
            if (weeklyLimit < dailyLimit) {
                if (limits_error_lbl != null) {
                    limits_error_lbl.setText("Weekly limit must be greater than or equal to daily limit");
                }
                return;
            }
        } catch (NumberFormatException e) {
            if (limits_error_lbl != null) {
                limits_error_lbl.setText("Please enter valid numbers");
            }
            return;
        }
        
        // Update limits
        if (Model.getInstance().getDataBaseDriver().updateTransactionLimit(
                Model.getInstance().getCurrentUser().getUserName(), dailyLimit, weeklyLimit)) {
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Success");
            successAlert.setHeaderText("Limits Updated");
            successAlert.setContentText("Transaction limits have been updated successfully!");
            successAlert.showAndWait();
            
            if (limits_error_lbl != null) {
                limits_error_lbl.setText("Limits updated successfully!");
                limits_error_lbl.setStyle("-fx-text-fill: green;");
            }
            
            // Reload limits to show updated values
            loadTransactionLimits();
        } else {
            if (limits_error_lbl != null) {
                limits_error_lbl.setText("Failed to update limits. Please try again.");
                limits_error_lbl.setStyle("-fx-text-fill: red;");
            }
        }
    }

    private void onUpdateProfile() {
        // Clear previous error
        if (error_lbl != null) {
            error_lbl.setText("");
        }
        
        String firstName = firstName_fld != null ? firstName_fld.getText().trim() : "";
        String lastName = lastName_fld != null ? lastName_fld.getText().trim() : "";
        String email = email_fld != null ? email_fld.getText().trim() : "";
        String phoneNumber = phoneNumber_fld != null ? phoneNumber_fld.getText().trim() : "";
        String address = address_fld != null ? address_fld.getText().trim() : "";
        
        // Validate required fields
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
            if (error_lbl != null) {
                error_lbl.setText("Please fill all required fields (First Name, Last Name, Email, Phone Number)");
            }
            return;
        }
        
        // Validate email format
        if (!isValidEmail(email)) {
            if (error_lbl != null) {
                error_lbl.setText("Please enter a valid email address");
            }
            return;
        }
        
        // Validate phone number format
        if (!phoneNumber.matches("\\d{10,15}")) {
            if (error_lbl != null) {
                error_lbl.setText("Phone number must be 10-15 digits");
            }
            return;
        }
        
        // Check if email is being changed and if new email already exists
        if (Model.getInstance().getCurrentUser() != null && 
            !email.equals(Model.getInstance().getCurrentUser().getEmail())) {
            if (Model.getInstance().getDataBaseDriver().emailExists(email)) {
                if (error_lbl != null) {
                    error_lbl.setText("Email already exists. Please use a different email.");
                }
                return;
            }
        }
        
        // Update profile
        if (Model.getInstance().updateUserProfile(firstName, lastName, email, phoneNumber, address)) {
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Success");
            successAlert.setHeaderText("Profile Updated");
            successAlert.setContentText("Your profile has been updated successfully!");
            successAlert.showAndWait();
            
            if (error_lbl != null) {
                error_lbl.setText("Profile updated successfully!");
                error_lbl.setStyle("-fx-text-fill: green;");
            }
        } else {
            if (error_lbl != null) {
                error_lbl.setText("Failed to update profile. Please try again.");
                error_lbl.setStyle("-fx-text-fill: red;");
            }
        }
    }
    
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}

