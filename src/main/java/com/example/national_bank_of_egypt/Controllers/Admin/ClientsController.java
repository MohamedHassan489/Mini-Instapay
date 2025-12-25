package com.example.national_bank_of_egypt.Controllers.Admin;

import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Models.User;
import com.example.national_bank_of_egypt.Views.ClientCellFactory;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import com.example.national_bank_of_egypt.Utils.DialogUtils;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ClientsController implements Initializable {
    public ListView<User> Clients_ListView;
    public TextField search_fld;
    public Label userDetails_lbl;
    public Button suspend_btn;
    public Button editProfile_btn;
    private User selectedUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SetData();
        Clients_ListView.setItems(Model.getInstance().getUsers());
        Clients_ListView.setCellFactory(e -> new ClientCellFactory());

        // Disable buttons initially
        if (suspend_btn != null)
            suspend_btn.setDisable(true);
        if (editProfile_btn != null)
            editProfile_btn.setDisable(true);

        // Add selection listener
        Clients_ListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedUser = newValue;
            if (newValue != null) {
                showUserProfile(newValue);
                if (suspend_btn != null)
                    suspend_btn.setDisable(false);
                if (editProfile_btn != null)
                    editProfile_btn.setDisable(false);
            } else {
                if (suspend_btn != null)
                    suspend_btn.setDisable(true);
                if (editProfile_btn != null)
                    editProfile_btn.setDisable(true);
            }
        });

        // Add button actions
        if (suspend_btn != null) {
            suspend_btn.setOnAction(event -> onSuspend());
        }
        if (editProfile_btn != null) {
            editProfile_btn.setOnAction(event -> onEditProfile());
        }

        // Add search functionality
        if (search_fld != null) {
            search_fld.textProperty().addListener((observable, oldValue, newValue) -> filterUsers(newValue));
        }
    }

    private void SetData() {
        if (Model.getInstance().getUsers().isEmpty()) {
            Model.getInstance().loadAllUsers();
        }
    }

    private void filterUsers(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            Clients_ListView.setItems(Model.getInstance().getUsers());
            return;
        }

        javafx.collections.transformation.FilteredList<User> filtered = new javafx.collections.transformation.FilteredList<>(
                Model.getInstance().getUsers(), user -> {
                    String lowerSearch = searchText.toLowerCase();
                    return user.getUserName().toLowerCase().contains(lowerSearch) ||
                            user.getFirstName().toLowerCase().contains(lowerSearch) ||
                            user.getLastName().toLowerCase().contains(lowerSearch) ||
                            user.getEmail().toLowerCase().contains(lowerSearch) ||
                            user.getPhoneNumber().toLowerCase().contains(lowerSearch);
                });

        Clients_ListView.setItems(filtered);
    }

    private void showUserProfile(User user) {
        if (userDetails_lbl != null) {
            StringBuilder details = new StringBuilder();
            details.append("User Profile Details:\n\n");
            details.append("Name: ").append(user.getFirstName()).append(" ").append(user.getLastName()).append("\n");
            details.append("Username: ").append(user.getUserName()).append("\n");
            details.append("Email: ").append(user.getEmail()).append("\n");
            details.append("Phone: ").append(user.getPhoneNumber()).append("\n");
            details.append("Address: ").append(user.getAddress()).append("\n");
            details.append("Date Created: ").append(user.dateCreatedProperty().get()).append("\n");
            details.append("2FA Enabled: ").append(user.twoFactorEnabledProperty().get()).append("\n");
            details.append("Bank Accounts: ").append(user.getBankAccounts().size()).append("\n");

            if (!user.getBankAccounts().isEmpty()) {
                details.append("\nBank Accounts:\n");
                user.getBankAccounts().forEach(account -> {
                    details.append("  - ").append(account.getAccountType())
                            .append(" (").append(account.getAccountNumber())
                            .append("): $").append(String.format("%.2f", account.getBalance())).append("\n");
                });
            }

            userDetails_lbl.setText(details.toString());
        }
    }

    private void onSuspend() {
        if (selectedUser != null) {
            boolean confirmed = DialogUtils.showDestructiveConfirmation(
                    "Suspend Account",
                    "Are you sure you want to suspend account: " + selectedUser.getUserName() + "?");

            if (confirmed) {
                try {
                    Model.getInstance().clearLastError();
                    if (Model.getInstance().suspendAccount(selectedUser.getUserName())) {
                        DialogUtils.showSuccess("Success",
                                "Account " + selectedUser.getUserName() + " has been suspended.");
                        // Refresh list or update UI if needed
                        Model.getInstance().loadAllUsers();
                    } else {
                        String errorMessage = Model.getInstance().getLastErrorMessage();
                        if (errorMessage != null && !errorMessage.isEmpty()) {
                            DialogUtils.showError("Error", errorMessage);
                        } else {
                            DialogUtils.showError("Error", "Could not suspend account. Please try again.");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogUtils.showError("Error", "An error occurred: " + e.getMessage());
                }
            }
        }
    }

    private void onEditProfile() {
        if (selectedUser == null) {
            DialogUtils.showError("Error", "Please select a user to edit.");
            return;
        }

        // Create a custom dialog for editing user profile
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit User Profile");
        dialog.setHeaderText("Edit profile for: " + selectedUser.getUserName());

        // Set up buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create form grid
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(25, 30, 20, 30));
        grid.setStyle("-fx-background-color: #F4F6F9;");

        // Create styled text fields with current values
        TextField firstNameField = createStyledTextField(selectedUser.getFirstName());
        TextField lastNameField = createStyledTextField(selectedUser.getLastName());
        TextField emailField = createStyledTextField(selectedUser.getEmail());
        TextField phoneField = createStyledTextField(selectedUser.getPhoneNumber());
        TextField addressField = createStyledTextField(selectedUser.getAddress());
        
        CheckBox twoFactorCheckBox = new CheckBox();
        twoFactorCheckBox.setSelected("true".equalsIgnoreCase(selectedUser.twoFactorEnabledProperty().get()));
        twoFactorCheckBox.setStyle("-fx-font-size: 14px;");

        // Add styled labels and fields to grid
        grid.add(createStyledLabel("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(createStyledLabel("Last Name:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(createStyledLabel("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(createStyledLabel("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(createStyledLabel("Address:"), 0, 4);
        grid.add(addressField, 1, 4);
        grid.add(createStyledLabel("2FA Enabled:"), 0, 5);
        grid.add(twoFactorCheckBox, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Style the dialog pane with system theme
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #F4F6F9; -fx-font-family: 'Inter', 'Segoe UI', sans-serif;");
        dialogPane.setHeaderText(null);
        
        // Style header
        Label headerLabel = new Label("Edit Profile: " + selectedUser.getUserName());
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #16423C; -fx-padding: 10 0 5 0;");
        dialogPane.setHeader(headerLabel);
        
        // Style buttons
        dialogPane.lookupButton(saveButtonType).setStyle(
            "-fx-background-color: #16423C; -fx-text-fill: white; -fx-font-weight: bold; " +
            "-fx-background-radius: 8; -fx-padding: 10 25; -fx-cursor: hand;"
        );
        dialogPane.lookupButton(ButtonType.CANCEL).setStyle(
            "-fx-background-color: transparent; -fx-border-color: #16423C; -fx-border-radius: 8; " +
            "-fx-text-fill: #16423C; -fx-background-radius: 8; -fx-padding: 9 24; -fx-font-weight: bold; -fx-cursor: hand;"
        );

        // Show dialog and process result
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == saveButtonType) {
            // Validate inputs
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            boolean twoFactorEnabled = twoFactorCheckBox.isSelected();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                DialogUtils.showError("Validation Error", "First name, last name, and email are required.");
                return;
            }

            // Email validation
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                DialogUtils.showError("Validation Error", "Please enter a valid email address.");
                return;
            }

            try {
                // Update user in database
                boolean success = Model.getInstance().getDataBaseDriver().updateUserProfile(
                        selectedUser.getUserName(),
                        firstName,
                        lastName,
                        email,
                        phone,
                        address,
                        twoFactorEnabled ? "true" : "false"
                );

                if (success) {
                    // Update the local user object
                    selectedUser.firstNameProperty().set(firstName);
                    selectedUser.lastNameProperty().set(lastName);
                    selectedUser.emailProperty().set(email);
                    selectedUser.phoneNumberProperty().set(phone);
                    selectedUser.addressProperty().set(address);
                    selectedUser.twoFactorEnabledProperty().set(twoFactorEnabled ? "true" : "false");

                    // Refresh the display
                    showUserProfile(selectedUser);
                    Clients_ListView.refresh();

                    DialogUtils.showSuccess("Success", "User profile updated successfully!");
                } else {
                    DialogUtils.showError("Error", "Failed to update user profile. Please try again.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                DialogUtils.showError("Error", "An error occurred while updating profile: " + e.getMessage());
            }
        }
    }
    
    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #16423C;");
        return label;
    }
    
    private TextField createStyledTextField(String value) {
        TextField field = new TextField(value);
        field.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; -fx-border-radius: 8; " +
            "-fx-border-color: #E0E0E0; -fx-padding: 10; -fx-font-size: 14px; -fx-pref-width: 250;"
        );
        field.setOnMouseEntered(e -> field.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; -fx-border-radius: 8; " +
            "-fx-border-color: #16423C; -fx-padding: 10; -fx-font-size: 14px; -fx-pref-width: 250;"
        ));
        field.setOnMouseExited(e -> field.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; -fx-border-radius: 8; " +
            "-fx-border-color: #E0E0E0; -fx-padding: 10; -fx-font-size: 14px; -fx-pref-width: 250;"
        ));
        return field;
    }
}
