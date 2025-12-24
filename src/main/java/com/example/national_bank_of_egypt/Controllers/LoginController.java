package com.example.national_bank_of_egypt.Controllers;

import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Utils.AnimationUtils;
import com.example.national_bank_of_egypt.Views.AccountType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public ChoiceBox<AccountType> acc_selector;
    public Label username_lbl;
    public TextField Username_fld;
    public PasswordField password_fld;
    public Button login_btn;
    public Button register_btn;
    public Label error_lbl;
    
    @FXML
    private VBox login_form_container;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        acc_selector.setItems(FXCollections.observableArrayList(AccountType.CLIENT, AccountType.ADMIN));
        acc_selector.setValue(Model.getInstance().getViewFactory().getLoginAccountType());
        acc_selector.valueProperty().addListener(observable -> setAcc_selector());
        login_btn.setOnAction(actionEvent -> onLogin());
        if (register_btn != null) {
            register_btn.setOnAction(actionEvent -> onRegister());
        }
        
        // Clear error when user starts typing
        if (Username_fld != null) {
            Username_fld.textProperty().addListener((obs, oldVal, newVal) -> {
                if (error_lbl != null && error_lbl.isVisible() && !newVal.isEmpty()) {
                    hideError();
                }
            });
        }
        
        if (password_fld != null) {
            password_fld.textProperty().addListener((obs, oldVal, newVal) -> {
                if (error_lbl != null && error_lbl.isVisible() && !newVal.isEmpty()) {
                    hideError();
                }
            });
        }
        
        // Add animations on load
        Platform.runLater(() -> {
            animateLoginForm();
            setupInputAnimations();
        });
    }
    
    /**
     * Animate the login form entrance
     */
    private void animateLoginForm() {
        if (login_form_container != null) {
            // Start with form invisible and slightly to the right
            login_form_container.setOpacity(0);
            login_form_container.setTranslateX(30);
            
            // Fade in and slide in from right (parallel animation)
            javafx.animation.ParallelTransition parallel = new javafx.animation.ParallelTransition();
            parallel.getChildren().addAll(
                AnimationUtils.slideInFromRight(login_form_container, 30, AnimationUtils.ENTRANCE_DURATION),
                AnimationUtils.fadeIn(login_form_container, AnimationUtils.ENTRANCE_DURATION)
            );
            parallel.play();
        }
    }
    
    /**
     * Setup input field focus animations
     */
    private void setupInputAnimations() {
        // Add focus listeners for smooth transitions
        if (Username_fld != null) {
            Username_fld.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    // Focus gained - scale up slightly
                    AnimationUtils.scaleHover(Username_fld, 1.02, AnimationUtils.STANDARD_DURATION).play();
                }
            });
        }
        
        if (password_fld != null) {
            password_fld.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    // Focus gained - scale up slightly
                    AnimationUtils.scaleHover(password_fld, 1.02, AnimationUtils.STANDARD_DURATION).play();
                }
            });
        }
    }

    private void onLogin(){
        try {
            // Hide any previous errors
            hideError();
            
            // Validate input
            String username = Username_fld.getText();
            String password = password_fld.getText();
            
            if (username == null || username.trim().isEmpty()) {
                showError("Please enter username.");
                // Shake animation for empty field
                if (Username_fld != null) {
                    AnimationUtils.shake(Username_fld, 5, AnimationUtils.STANDARD_DURATION).play();
                }
                return;
            }
            
            if (password == null || password.trim().isEmpty()) {
                showError("Please enter password.");
                // Shake animation for empty field
                if (password_fld != null) {
                    AnimationUtils.shake(password_fld, 5, AnimationUtils.STANDARD_DURATION).play();
                }
                return;
            }
            
            Stage stage = (Stage) error_lbl.getScene().getWindow();
            
            if(Model.getInstance().getViewFactory().getLoginAccountType() == AccountType.CLIENT){
                Model.getInstance().evaluateUserCred(username.trim(), password);
                
                // Check if login was successful by checking if currentUser is set
                if (Model.getInstance().getCurrentUser() == null) {
                    // Login failed - no user found
                    Username_fld.setText("");
                    password_fld.setText("");
                    showError("Invalid credentials. Please check your username and password.");
                    return;
                }
                
                // Check if account is suspended
                if ("suspended".equalsIgnoreCase(Model.getInstance().getCurrentUser().twoFactorEnabledProperty().get())) {
                    showError("Your account has been suspended. Please contact support.");
                    Username_fld.setText("");
                    password_fld.setText("");
                    Model.getInstance().setCurrentUser(null);
                    return;
                }
                
                // Check if 2FA is enabled and OTP verification is needed
                String twoFactorStatus = Model.getInstance().getCurrentUser().twoFactorEnabledProperty().get();
                if (twoFactorStatus != null && "true".equalsIgnoreCase(twoFactorStatus)) {
                    // Show OTP verification dialog
                    if (verifyOTP(username.trim())) {
                        Model.getInstance().setUserLoginSuccessFlag(true);
                        Model.getInstance().getViewFactory().showClinetWindow();
                        Model.getInstance().getViewFactory().closeStage(stage);
                    } else {
                        showError("Invalid OTP. Please try again.");
                        Model.getInstance().setCurrentUser(null);
                        Model.getInstance().setUserLoginSuccessFlag(false);
                    }
                } else {
                    // 2FA is not enabled - proceed with login
                    // Double-check that login flag is set (should be set by evaluateUserCred)
                    if (Model.getInstance().getUserLoginSuccessFlag() == null || !Model.getInstance().getUserLoginSuccessFlag()) {
                        Model.getInstance().setUserLoginSuccessFlag(true);
                    }
                    Model.getInstance().getViewFactory().showClinetWindow();
                    Model.getInstance().getViewFactory().closeStage(stage);
                }
            } else {
                Model.getInstance().evaluateAdminCred(username.trim(), password);
                if (Model.getInstance().getAdminLoginSuccessFlag() != null && Model.getInstance().getAdminLoginSuccessFlag()) {
                    Model.getInstance().getViewFactory().showAdminWindow();
                    Model.getInstance().getViewFactory().closeStage(stage);
                } else {
                    Username_fld.setText("");
                    password_fld.setText("");
                    showError("Invalid admin credentials. Please check your username and password.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred during login. Please try again.");
        }
    }
    
    private boolean verifyOTP(String userName) {
        com.example.national_bank_of_egypt.Security.OTPService otpService = 
            com.example.national_bank_of_egypt.Security.OTPService.getInstance();
        
        // Get user's email from current user
        com.example.national_bank_of_egypt.Models.User currentUser = Model.getInstance().getCurrentUser();
        if (currentUser == null) {
            showError("Error: User information not available.");
            return false;
        }
        
        String userEmail = currentUser.getEmail();
        String userDisplayName = currentUser.getFirstName() + " " + currentUser.getLastName();
        
        // Generate and send OTP via email
        boolean emailSent = otpService.generateAndSendOTP(userName, userEmail, userDisplayName);
        
        // Show OTP dialog
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Two-Factor Authentication");
        dialog.setHeaderText("Enter OTP");
        
        if (emailSent) {
            dialog.setContentText("An OTP has been sent to your email address: " + userEmail + 
                "\n\nPlease check your email and enter the OTP code below.\n" +
                "The OTP is valid for 5 minutes.");
        } else {
            dialog.setContentText("Failed to send OTP email. Please contact support.\n" +
                "For testing purposes, check the console for the OTP code.");
        }
        
        java.util.Optional<String> result = dialog.showAndWait();
        
        if (result.isPresent()) {
            return otpService.verifyOTP(userName, result.get());
        }
        
        return false;
    }

    private void onRegister() {
        Model.getInstance().getViewFactory().showRegistrationWindow();
    }

    private void setAcc_selector(){
        Model.getInstance().getViewFactory().setLoginAccountType(acc_selector.getValue());
        // Hide error when switching account types
        hideError();
        // Clear input fields when switching
        if (Username_fld != null) {
            Username_fld.clear();
        }
        if (password_fld != null) {
            password_fld.clear();
        }
        
        if (acc_selector.getValue() == AccountType.ADMIN){
            username_lbl.setText("Admin Name");
            if (Username_fld != null) {
                Username_fld.setPromptText("Enter your admin name");
            }
            if (register_btn != null) {
                // Fade out register button
                javafx.animation.FadeTransition fadeOut = AnimationUtils.fadeOut(register_btn, AnimationUtils.STANDARD_DURATION);
                fadeOut.setOnFinished(e -> register_btn.setVisible(false));
                fadeOut.play();
            }
        }else {
            username_lbl.setText("Username");
            if (Username_fld != null) {
                Username_fld.setPromptText("Enter your username");
            }
            if (register_btn != null) {
                // Fade in register button
                register_btn.setVisible(true);
                register_btn.setOpacity(0);
                AnimationUtils.fadeIn(register_btn, AnimationUtils.STANDARD_DURATION).play();
            }
        }
    }
    
    /**
     * Show error message with animation
     */
    private void showError(String message) {
        if (error_lbl != null) {
            error_lbl.setText(message);
            error_lbl.setVisible(true);
            error_lbl.setManaged(true);
            // Slide down and fade in animation
            error_lbl.setTranslateY(-10);
            error_lbl.setOpacity(0);
            AnimationUtils.fadeInSlideUp(error_lbl, 10, AnimationUtils.STANDARD_DURATION).play();
        }
    }
    
    /**
     * Hide error message
     */
    private void hideError() {
        if (error_lbl != null) {
            error_lbl.setText("");
            error_lbl.setVisible(false);
            error_lbl.setManaged(false);
        }
    }
}

