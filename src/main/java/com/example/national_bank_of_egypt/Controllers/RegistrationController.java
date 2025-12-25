package com.example.national_bank_of_egypt.Controllers;

import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Utils.AnimationUtils;
import com.example.national_bank_of_egypt.Utils.ErrorHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
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

    @FXML
    private VBox register_form_container;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (register_btn != null) {
            register_btn.setOnAction(actionEvent -> onRegister());
        }
        if (cancel_btn != null) {
            cancel_btn.setOnAction(actionEvent -> onCancel());
        }

        // Add animations on load
        Platform.runLater(() -> {
            animateRegistrationForm();
            setupInputAnimations();
            hideError();
        });
    }

    /**
     * Animate the registration form entrance
     */
    private void animateRegistrationForm() {
        if (register_form_container != null) {
            // Start with form invisible and slightly to the right
            register_form_container.setOpacity(0);
            register_form_container.setTranslateX(30);

            // Fade in and slide in from right (parallel animation)
            javafx.animation.ParallelTransition parallel = new javafx.animation.ParallelTransition();
            parallel.getChildren().addAll(
                    AnimationUtils.slideInFromRight(register_form_container, 30, AnimationUtils.ENTRANCE_DURATION),
                    AnimationUtils.fadeIn(register_form_container, AnimationUtils.ENTRANCE_DURATION));
            parallel.play();
        }
    }

    /**
     * Setup input field focus animations
     */
    private void setupInputAnimations() {
        // Add focus listeners for all text fields
        TextField[] fields = { firstName_fld, lastName_fld, email_fld, phoneNumber_fld,
                address_fld, userName_fld };

        for (TextField field : fields) {
            if (field != null) {
                field.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal) {
                        AnimationUtils.scaleHover(field, 1.02, AnimationUtils.STANDARD_DURATION).play();
                    }
                });
            }
        }

        // Password fields
        if (password_fld != null) {
            password_fld.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    AnimationUtils.scaleHover(password_fld, 1.02, AnimationUtils.STANDARD_DURATION).play();
                }
            });
        }

        if (confirmPassword_fld != null) {
            confirmPassword_fld.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    AnimationUtils.scaleHover(confirmPassword_fld, 1.02, AnimationUtils.STANDARD_DURATION).play();
                }
            });
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

        // Clear previous error
        hideError();

        // Validate required fields
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                phoneNumber.isEmpty() || userName.isEmpty() || password.isEmpty()) {
            showError("Please fill all required fields");
            shakeEmptyFields(firstName, lastName, email, phoneNumber, userName, password);
            return;
        }

        // Validate email format
        if (!isValidEmail(email)) {
            showError("Please enter a valid email address");
            if (email_fld != null) {
                AnimationUtils.shake(email_fld, 5, AnimationUtils.STANDARD_DURATION).play();
            }
            return;
        }

        // Validate phone number (should be numeric and reasonable length)
        if (!phoneNumber.matches("\\d{10,15}")) {
            showError("Phone number must be 10-15 digits");
            if (phoneNumber_fld != null) {
                AnimationUtils.shake(phoneNumber_fld, 5, AnimationUtils.STANDARD_DURATION).play();
            }
            return;
        }

        // Validate username (alphanumeric and underscore, 3-20 chars)
        if (!userName.matches("^[a-zA-Z0-9_]{3,20}$")) {
            showError("Username must be 3-20 characters (letters, numbers, underscore only)");
            if (userName_fld != null) {
                AnimationUtils.shake(userName_fld, 5, AnimationUtils.STANDARD_DURATION).play();
            }
            return;
        }

        // Validate password match
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            if (password_fld != null) {
                AnimationUtils.shake(password_fld, 5, AnimationUtils.STANDARD_DURATION).play();
            }
            if (confirmPassword_fld != null) {
                AnimationUtils.shake(confirmPassword_fld, 5, AnimationUtils.STANDARD_DURATION).play();
            }
            return;
        }

        // Validate password strength
        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            if (password_fld != null) {
                AnimationUtils.shake(password_fld, 5, AnimationUtils.STANDARD_DURATION).play();
            }
            return;
        }

        // Attempt registration
        try {
            Model.getInstance().clearLastError();
            
            if (Model.getInstance().registerUser(firstName, lastName, email, phoneNumber, address, userName, password)) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Registration Successful");
                successAlert.setHeaderText("Account Created");
                successAlert.setContentText("Registration successful! You can now login with your credentials.");
                successAlert.showAndWait();

                clearFields();
                Stage stage = (Stage) error_lbl.getScene().getWindow();
                Model.getInstance().getViewFactory().closeStage(stage);
                Model.getInstance().getViewFactory().showLoginWindow();
            } else {
                // Display specific error from Model
                String errorMessage = Model.getInstance().getLastErrorMessage();
                if (errorMessage != null && !errorMessage.isEmpty()) {
                    showError(errorMessage);
                } else {
                    showError("Registration failed: Username, email, or phone number already exists");
                }
            }
        } catch (Exception e) {
            // Handle any unexpected errors
            showError("An unexpected error occurred: " + ErrorHandler.getUserFriendlyMessage(e));
            e.printStackTrace();
        }
    }

    /**
     * Shake empty required fields
     */
    private void shakeEmptyFields(String firstName, String lastName, String email,
            String phoneNumber, String userName, String password) {
        if (firstName.isEmpty() && firstName_fld != null) {
            AnimationUtils.shake(firstName_fld, 5, AnimationUtils.STANDARD_DURATION).play();
        }
        if (lastName.isEmpty() && lastName_fld != null) {
            AnimationUtils.shake(lastName_fld, 5, AnimationUtils.STANDARD_DURATION).play();
        }
        if (email.isEmpty() && email_fld != null) {
            AnimationUtils.shake(email_fld, 5, AnimationUtils.STANDARD_DURATION).play();
        }
        if (phoneNumber.isEmpty() && phoneNumber_fld != null) {
            AnimationUtils.shake(phoneNumber_fld, 5, AnimationUtils.STANDARD_DURATION).play();
        }
        if (userName.isEmpty() && userName_fld != null) {
            AnimationUtils.shake(userName_fld, 5, AnimationUtils.STANDARD_DURATION).play();
        }
        if (password.isEmpty() && password_fld != null) {
            AnimationUtils.shake(password_fld, 5, AnimationUtils.STANDARD_DURATION).play();
        }
    }

    /**
     * Show error message with animation
     */
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

    private void hideError() {
        if (error_lbl != null) {
            error_lbl.setText("");
            error_lbl.setVisible(false);
            error_lbl.setManaged(false);
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        // Basic email validation: contains @ and has domain
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
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
