package com.example.national_bank_of_egypt.Controllers;

import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Views.AccountType;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        acc_selector.setItems(FXCollections.observableArrayList(AccountType.CLIENT, AccountType.ADMIN));
        acc_selector.setValue(Model.getInstance().getViewFactory().getLoginAccountType());
        acc_selector.valueProperty().addListener(observable -> setAcc_selector());
        login_btn.setOnAction(actionEvent -> onLogin());
        if (register_btn != null) {
            register_btn.setOnAction(actionEvent -> onRegister());
        }
    }

    private void onLogin(){
        try {
            // Validate input
            String username = Username_fld.getText();
            String password = password_fld.getText();
            
            if (username == null || username.trim().isEmpty()) {
                error_lbl.setText("Please enter username.");
                return;
            }
            
            if (password == null || password.trim().isEmpty()) {
                error_lbl.setText("Please enter password.");
                return;
            }
            
            Stage stage = (Stage) error_lbl.getScene().getWindow();
            
            if(Model.getInstance().getViewFactory().getLoginAccountType() == AccountType.CLIENT){
                Model.getInstance().evaluateUserCred(username.trim(), password);
                
                // Check if account is suspended
                if (Model.getInstance().getCurrentUser() != null && 
                    "suspended".equalsIgnoreCase(Model.getInstance().getCurrentUser().twoFactorEnabledProperty().get())) {
                    error_lbl.setText("Your account has been suspended. Please contact support.");
                    Username_fld.setText("");
                    password_fld.setText("");
                    return;
                }
                
                // Check if 2FA is enabled and OTP verification is needed
                if (Model.getInstance().getCurrentUser() != null && 
                    "true".equalsIgnoreCase(Model.getInstance().getCurrentUser().twoFactorEnabledProperty().get())) {
                    // Show OTP verification dialog
                    if (verifyOTP(username.trim())) {
                        Model.getInstance().setUserLoginSuccessFlag(true);
                        Model.getInstance().getViewFactory().showClinetWindow();
                        Model.getInstance().getViewFactory().closeStage(stage);
                    } else {
                        error_lbl.setText("Invalid OTP. Please try again.");
                        Model.getInstance().setCurrentUser(null);
                        Model.getInstance().setUserLoginSuccessFlag(false);
                    }
                } else if (Model.getInstance().getUserLoginSuccessFlag() != null && Model.getInstance().getUserLoginSuccessFlag()) {
                    Model.getInstance().getViewFactory().showClinetWindow();
                    Model.getInstance().getViewFactory().closeStage(stage);
                } else {
                    Username_fld.setText("");
                    password_fld.setText("");
                    error_lbl.setText("Invalid credentials. Please check your username and password.");
                }
            } else {
                Model.getInstance().evaluateAdminCred(username.trim(), password);
                if (Model.getInstance().getAdminLoginSuccessFlag() != null && Model.getInstance().getAdminLoginSuccessFlag()) {
                    Model.getInstance().getViewFactory().showAdminWindow();
                    Model.getInstance().getViewFactory().closeStage(stage);
                } else {
                    Username_fld.setText("");
                    password_fld.setText("");
                    error_lbl.setText("Invalid admin credentials. Please check your username and password.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            error_lbl.setText("An error occurred during login. Please try again.");
        }
    }
    
    private boolean verifyOTP(String userName) {
        com.example.national_bank_of_egypt.Security.OTPService otpService = 
            com.example.national_bank_of_egypt.Security.OTPService.getInstance();
        
        // Get user's email from current user
        com.example.national_bank_of_egypt.Models.User currentUser = Model.getInstance().getCurrentUser();
        if (currentUser == null) {
            error_lbl.setText("Error: User information not available.");
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
        if (acc_selector.getValue() == AccountType.ADMIN){
            username_lbl.setText("AdminName");
            if (register_btn != null) {
                register_btn.setVisible(false);
            }
        }else {
            username_lbl.setText("UserName");
            if (register_btn != null) {
                register_btn.setVisible(true);
            }
        }
    }
}

