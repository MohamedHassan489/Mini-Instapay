package com.example.national_bank_of_egypt.Controllers;

import com.example.national_bank_of_egypt.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class RegistrationController implements Initializable {
    public TextField firstName_fld;
    public TextField lastName_fld;
    public TextField email_fld;
    public TextField phoneNumber_fld;
    public TextField address_fld;
    public TextField userName_fld;
    public PasswordField password_fld;
    public PasswordField confirmPassword_fld;
    public Button register_btn;
    public Button cancel_btn;
    public Label error_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (register_btn != null) {
            register_btn.setOnAction(actionEvent -> onRegister());
        }
        if (cancel_btn != null) {
            cancel_btn.setOnAction(actionEvent -> onCancel());
        }
    }

    private void onRegister() {
        String firstName = firstName_fld.getText().trim();
        String lastName = lastName_fld.getText().trim();
        String email = email_fld.getText().trim();
        String phoneNumber = phoneNumber_fld.getText().trim();
        String address = address_fld.getText().trim();
        String userName = userName_fld.getText().trim();
        String password = password_fld.getText();
        String confirmPassword = confirmPassword_fld.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || 
            phoneNumber.isEmpty() || userName.isEmpty() || password.isEmpty()) {
            error_lbl.setText("Please fill all required fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            error_lbl.setText("Passwords do not match");
            return;
        }

        if (password.length() < 6) {
            error_lbl.setText("Password must be at least 6 characters");
            return;
        }

        if (Model.getInstance().registerUser(firstName, lastName, email, phoneNumber, address, userName, password)) {
            error_lbl.setText("Registration successful! You can now login.");
            clearFields();
            Stage stage = (Stage) error_lbl.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showLoginWindow();
        } else {
            error_lbl.setText("Registration failed: Username or phone number already exists");
        }
    }

    private void onCancel() {
        Stage stage = (Stage) error_lbl.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().getViewFactory().showLoginWindow();
    }

    private void clearFields() {
        firstName_fld.setText("");
        lastName_fld.setText("");
        email_fld.setText("");
        phoneNumber_fld.setText("");
        address_fld.setText("");
        userName_fld.setText("");
        password_fld.setText("");
        confirmPassword_fld.setText("");
    }
}

