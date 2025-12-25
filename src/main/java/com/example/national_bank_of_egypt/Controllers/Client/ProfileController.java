package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Models.TransactionLimit;
import com.example.national_bank_of_egypt.Utils.AnimationUtils;
import com.example.national_bank_of_egypt.Utils.ErrorHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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

    @FXML
    private VBox profile_form_container;

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
                update_btn.setOnAction(event -> {
                    logDebug("ProfileController.java:59", "update_btn clicked", "A", "buttonClick",
                            new java.util.HashMap<String, Object>() {
                                {
                                    put("button", "update_btn");
                                    put("disabled", update_btn.isDisabled());
                                }
                            });
                    onUpdateProfile();
                });
            }

            if (updateLimits_btn != null) {
                updateLimits_btn.setOnAction(event -> {
                    logDebug("ProfileController.java:64", "updateLimits_btn clicked", "B", "buttonClick",
                            new java.util.HashMap<String, Object>() {
                                {
                                    put("button", "updateLimits_btn");
                                    put("disabled", updateLimits_btn.isDisabled());
                                }
                            });
                    onUpdateLimits();
                });
            }

            if (update2FA_btn != null) {
                update2FA_btn.setOnAction(event -> onUpdate2FA());
            }

            // Add page load animations
            Platform.runLater(() -> {
                animatePageLoad();
                hideMessage(error_lbl);
                hideMessage(twoFactor_error_lbl);
                hideMessage(limits_error_lbl);
            });

            System.out.println("ProfileController initialized successfully");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in ProfileController.initialize: " + e.getMessage());
        }
    }

    /**
     * Animate page load with fade-in and slide-up
     */
    private void animatePageLoad() {
        if (firstName_fld != null && firstName_fld.getScene() != null) {
            javafx.scene.Parent root = firstName_fld.getScene().getRoot();
            if (root != null) {
                root.setOpacity(0);
                root.setTranslateY(20);
                AnimationUtils.fadeInSlideUp(root, 20, AnimationUtils.ENTRANCE_DURATION).play();
            }
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
        hideMessage(twoFactor_error_lbl);

        if (Model.getInstance().getCurrentUser() == null) {
            showError(twoFactor_error_lbl, "User not logged in");
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
            successAlert.setContentText(
                    "Two-Factor Authentication has been " + (enable2FA ? "enabled" : "disabled") + " successfully!");
            successAlert.showAndWait();

            showSuccess(twoFactor_error_lbl, "2FA setting updated successfully!");

            // Reload 2FA status
            load2FAStatus();
        } else {
            showError(twoFactor_error_lbl, "Failed to update 2FA setting. Please try again.");
        }
    }

    private void loadTransactionLimits() {
        if (Model.getInstance().getCurrentUser() != null) {
            TransactionLimit limit = Model.getInstance()
                    .getTransactionLimit(Model.getInstance().getCurrentUser().getUserName());
            if (limit != null) {
                if (dailyLimit_fld != null) {
                    dailyLimit_fld.setText(String.format("%.2f", limit.getDailyLimit()));
                }
                if (weeklyLimit_fld != null) {
                    weeklyLimit_fld.setText(String.format("%.2f", limit.getWeeklyLimit()));
                }
                if (dailyUsed_lbl != null) {
                    dailyUsed_lbl.setText(
                            String.format("Daily Used: $%.2f / $%.2f", limit.getDailyUsed(), limit.getDailyLimit()));
                }
                if (weeklyUsed_lbl != null) {
                    weeklyUsed_lbl.setText(
                            String.format("Weekly Used: $%.2f / $%.2f", limit.getWeeklyUsed(), limit.getWeeklyLimit()));
                }
            }
        }
    }

    private void onUpdateLimits() {
        logDebug("ProfileController.java:160", "onUpdateLimits entry", "B", "methodEntry",
                new java.util.HashMap<String, Object>() {
                    {
                        put("currentUser",
                                Model.getInstance().getCurrentUser() != null
                                        ? Model.getInstance().getCurrentUser().getUserName()
                                        : "null");
                    }
                });
        // Clear previous error
        hideMessage(limits_error_lbl);

        if (Model.getInstance().getCurrentUser() == null) {
            logDebug("ProfileController.java:166", "currentUser is null", "B", "validation",
                    new java.util.HashMap<String, Object>() {
                        {
                            put("result", "earlyReturn");
                        }
                    });
            showError(limits_error_lbl, "User not logged in");
            return;
        }

        String dailyLimitStr = dailyLimit_fld != null ? dailyLimit_fld.getText().trim() : "";
        String weeklyLimitStr = weeklyLimit_fld != null ? weeklyLimit_fld.getText().trim() : "";
        logDebug("ProfileController.java:174", "field values extracted", "B", "values",
                new java.util.HashMap<String, Object>() {
                    {
                        put("dailyLimitStr", dailyLimitStr);
                        put("weeklyLimitStr", weeklyLimitStr);
                        put("dailyLimit_fld_null", dailyLimit_fld == null);
                        put("weeklyLimit_fld_null", weeklyLimit_fld == null);
                    }
                });

        // Validate input
        if (dailyLimitStr.isEmpty() || weeklyLimitStr.isEmpty()) {
            showError(limits_error_lbl, "Please fill all fields");
            return;
        }

        double dailyLimit, weeklyLimit;
        try {
            dailyLimit = Double.parseDouble(dailyLimitStr);
            weeklyLimit = Double.parseDouble(weeklyLimitStr);

            if (dailyLimit <= 0 || weeklyLimit <= 0) {
                showError(limits_error_lbl, "Limits must be greater than 0");
                return;
            }

            if (dailyLimit > 100000 || weeklyLimit > 500000) {
                showError(limits_error_lbl, "Limits exceed maximum allowed (Daily: $100,000, Weekly: $500,000)");
                return;
            }

            if (weeklyLimit < dailyLimit) {
                showError(limits_error_lbl, "Weekly limit must be greater than or equal to daily limit");
                return;
            }
        } catch (NumberFormatException e) {
            showError(limits_error_lbl, "Please enter valid numbers");
            return;
        }

        // Update limits
        logDebug("ProfileController.java:216", "calling updateTransactionLimit", "B", "databaseCall",
                new java.util.HashMap<String, Object>() {
                    {
                        put("userName", Model.getInstance().getCurrentUser().getUserName());
                        put("dailyLimit", dailyLimit);
                        put("weeklyLimit", weeklyLimit);
                    }
                });
        boolean updateResult = Model.getInstance().getDataBaseDriver().updateTransactionLimit(
                Model.getInstance().getCurrentUser().getUserName(), dailyLimit, weeklyLimit);
        logDebug("ProfileController.java:218", "updateTransactionLimit result", "B", "databaseResult",
                new java.util.HashMap<String, Object>() {
                    {
                        put("success", updateResult);
                    }
                });
        if (updateResult) {
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Success");
            successAlert.setHeaderText("Limits Updated");
            successAlert.setContentText("Transaction limits have been updated successfully!");
            successAlert.showAndWait();

            showSuccess(limits_error_lbl, "Limits updated successfully!");

            // Reload limits to show updated values
            loadTransactionLimits();
        } else {
            // Display specific error from Model
            String errorMessage = Model.getInstance().getLastErrorMessage();
            if (errorMessage != null && !errorMessage.isEmpty()) {
                showError(limits_error_lbl, errorMessage);
            } else {
                showError(limits_error_lbl, "Failed to update limits. Please try again.");
            }
        }
    }

    private void onUpdateProfile() {
        logDebug("ProfileController.java:240", "onUpdateProfile entry", "A", "methodEntry",
                new java.util.HashMap<String, Object>() {
                    {
                        put("currentUser",
                                Model.getInstance().getCurrentUser() != null
                                        ? Model.getInstance().getCurrentUser().getUserName()
                                        : "null");
                    }
                });
        // Clear previous error
        hideMessage(error_lbl);

        String firstName = firstName_fld != null ? firstName_fld.getText().trim() : "";
        String lastName = lastName_fld != null ? lastName_fld.getText().trim() : "";
        String email = email_fld != null ? email_fld.getText().trim() : "";
        String phoneNumber = phoneNumber_fld != null ? phoneNumber_fld.getText().trim() : "";
        String address = address_fld != null ? address_fld.getText().trim() : "";
        logDebug("ProfileController.java:250", "field values extracted", "A", "values",
                new java.util.HashMap<String, Object>() {
                    {
                        put("firstName", firstName);
                        put("lastName", lastName);
                        put("email", email);
                        put("phoneNumber", phoneNumber);
                        put("address", address);
                    }
                });

        // Validate required fields
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
            showError(error_lbl, "Please fill all required fields (First Name, Last Name, Email, Phone Number)");
            return;
        }

        // Validate email format
        if (!isValidEmail(email)) {
            showError(error_lbl, "Please enter a valid email address");
            return;
        }

        // Validate phone number format
        if (!phoneNumber.matches("\\d{10,15}")) {
            showError(error_lbl, "Phone number must be 10-15 digits");
            return;
        }

        // Check if email is being changed and if new email already exists
        if (Model.getInstance().getCurrentUser() != null &&
                !email.equals(Model.getInstance().getCurrentUser().getEmail())) {
            // #region agent log
            logDebug("ProfileController.java:295", "before emailExists check", "H2", "timing",
                    new java.util.HashMap<String, Object>() {
                        {
                            put("email", email);
                            put("timestamp", System.currentTimeMillis());
                        }
                    });
            // #endregion
            if (Model.getInstance().getDataBaseDriver().emailExists(email)) {
                // #region agent log
                logDebug("ProfileController.java:298", "emailExists returned true", "H2", "validation",
                        new java.util.HashMap<String, Object>() {
                            {
                                put("email", email);
                            }
                        });
                // #endregion
                showError(error_lbl, "Email already exists. Please use a different email.");
                return;
            }
            // #region agent log
            logDebug("ProfileController.java:304", "after emailExists check", "H2", "timing",
                    new java.util.HashMap<String, Object>() {
                        {
                            put("email", email);
                            put("timestamp", System.currentTimeMillis());
                        }
                    });
            // #endregion
        }

        // Store old values before update for notification
        String oldFirstName = null;
        String oldLastName = null;
        String oldEmail = null;
        String oldPhoneNumber = null;
        String oldAddress = null;
        if (Model.getInstance().getCurrentUser() != null) {
            oldFirstName = Model.getInstance().getCurrentUser().getFirstName();
            oldLastName = Model.getInstance().getCurrentUser().getLastName();
            oldEmail = Model.getInstance().getCurrentUser().getEmail();
            oldPhoneNumber = Model.getInstance().getCurrentUser().getPhoneNumber();
            oldAddress = Model.getInstance().getCurrentUser().getAddress();
        }

        // Update profile
        // #region agent log
        logDebug("ProfileController.java:308", "calling updateUserProfile", "H1,H2,H3", "databaseCall",
                new java.util.HashMap<String, Object>() {
                    {
                        put("firstName", firstName);
                        put("lastName", lastName);
                        put("email", email);
                        put("phoneNumber", phoneNumber);
                        put("timestamp", System.currentTimeMillis());
                    }
                });
        // #endregion
        boolean updateResult = Model.getInstance().updateUserProfile(firstName, lastName, email, phoneNumber, address);
        // #region agent log
        logDebug("ProfileController.java:311", "updateUserProfile result", "H1,H2,H3", "databaseResult",
                new java.util.HashMap<String, Object>() {
                    {
                        put("success", updateResult);
                        put("timestamp", System.currentTimeMillis());
                    }
                });
        // #endregion
        if (updateResult) {
            // Send notification about profile update
            if (Model.getInstance().getCurrentUser() != null) {
                String userName = Model.getInstance().getCurrentUser().getUserName();
                java.util.List<String> changedFields = new java.util.ArrayList<>();

                if (oldFirstName != null && !firstName.equals(oldFirstName)) {
                    changedFields.add("First Name");
                }
                if (oldLastName != null && !lastName.equals(oldLastName)) {
                    changedFields.add("Last Name");
                }
                if (oldEmail != null && !email.equals(oldEmail)) {
                    changedFields.add("Email");
                }
                if (oldPhoneNumber != null && !phoneNumber.equals(oldPhoneNumber)) {
                    changedFields.add("Phone Number");
                }
                if (oldAddress != null && !address.equals(oldAddress)) {
                    changedFields.add("Address");
                }

                String changesMessage;
                if (changedFields.isEmpty()) {
                    changesMessage = "Your profile information has been updated successfully.";
                } else if (changedFields.size() == 1) {
                    changesMessage = "Your " + changedFields.get(0) + " has been updated successfully.";
                } else {
                    changesMessage = "Your " + String.join(", ", changedFields.subList(0, changedFields.size() - 1)) +
                            " and " + changedFields.get(changedFields.size() - 1) + " have been updated successfully.";
                }

                com.example.national_bank_of_egypt.Notifications.NotificationService.getInstance()
                        .sendNotification(userName, "Profile Updated", changesMessage, "PROFILE");
            }

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Success");
            successAlert.setHeaderText("Profile Updated");
            successAlert.setContentText("Your profile has been updated successfully!");
            successAlert.showAndWait();

            showSuccess(error_lbl, "Profile updated successfully!");
        } else {
            // Send notification about profile update failure
            if (Model.getInstance().getCurrentUser() != null) {
                String userName = Model.getInstance().getCurrentUser().getUserName();
                com.example.national_bank_of_egypt.Notifications.NotificationService.getInstance()
                        .sendNotification(userName, "Profile Update Failed",
                                "Failed to update your profile. The database may be temporarily locked. Please try again in a moment.",
                                "PROFILE");
            }

            // Display specific error from Model
            String errorMessage = Model.getInstance().getLastErrorMessage();
            if (errorMessage != null && !errorMessage.isEmpty()) {
                showError(error_lbl, errorMessage);
            } else {
                showError(error_lbl, "Failed to update profile. Please try again.");
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

    // #region agent log
    private void logDebug(String location, String message, String hypothesisId, String type,
            java.util.Map<String, Object> data) {
        try {
            StringBuilder json = new StringBuilder();
            json.append("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"").append(hypothesisId)
                    .append("\",\"location\":\"").append(location.replace("\"", "\\\""))
                    .append("\",\"message\":\"").append(message.replace("\"", "\\\""))
                    .append("\",\"timestamp\":").append(System.currentTimeMillis())
                    .append(",\"data\":{");
            boolean first = true;
            for (java.util.Map.Entry<String, Object> entry : data.entrySet()) {
                if (!first)
                    json.append(",");
                json.append("\"").append(entry.getKey()).append("\":");
                Object val = entry.getValue();
                if (val instanceof String) {
                    json.append("\"").append(val.toString().replace("\"", "\\\"")).append("\"");
                } else if (val instanceof Boolean || val instanceof Number) {
                    json.append(val);
                } else {
                    json.append("\"").append(String.valueOf(val).replace("\"", "\\\"")).append("\"");
                }
                first = false;
            }
            json.append("}}\n");
            Files.write(Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"),
                    json.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) {
            // Silently fail to avoid disrupting the application
        }
    }

    // #endregion
    private void showMessage(Label targetLabel, String message, String style) {
        if (targetLabel != null) {
            targetLabel.setText(message);
            targetLabel.setStyle(style);
            targetLabel.setVisible(true);
            targetLabel.setManaged(true);
            // Slide down and fade in animation
            targetLabel.setTranslateY(-10);
            targetLabel.setOpacity(0);
            AnimationUtils.fadeInSlideUp(targetLabel, 10, AnimationUtils.STANDARD_DURATION).play();
        }
    }

    private void showError(Label targetLabel, String message) {
        showMessage(targetLabel, message, "-fx-text-fill: red;");
    }

    private void showSuccess(Label targetLabel, String message) {
        showMessage(targetLabel, message, "-fx-text-fill: green;");
    }

    private void hideMessage(Label targetLabel) {
        if (targetLabel != null) {
            targetLabel.setText("");
            targetLabel.setVisible(false);
            targetLabel.setManaged(false);
        }
    }
}
