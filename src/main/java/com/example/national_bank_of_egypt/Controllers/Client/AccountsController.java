package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Models.BankAccount;
import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Utils.AnimationUtils;
import com.example.national_bank_of_egypt.Utils.DialogUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountsController implements Initializable {
    public ListView<BankAccount> accounts_list;
    public Label account_num_lbl;
    public Label bank_name_lbl;
    public Label balance_lbl;
    public Label account_type_lbl;
    public TextField new_account_num_fld;
    public TextField new_bank_name_fld;
    public ComboBox<String> account_type_combo;
    public TextField new_balance_fld;
    public Button add_account_btn;
    public Label add_account_error_lbl;
    public Button remove_account_btn;
    public Label remove_account_error_lbl;
    public TextField update_bank_name_fld;
    public ComboBox<String> update_account_type_combo;
    public Button update_account_btn;
    public Label update_account_error_lbl;

    @FXML
    private VBox accounts_form_container;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Initialize account type combo
            if (account_type_combo != null) {
                account_type_combo.setItems(FXCollections.observableArrayList("Checking", "Saving", "Business"));
                account_type_combo.setValue("Checking");
            }

            // Initialize update account type combo
            if (update_account_type_combo != null) {
                update_account_type_combo.setItems(FXCollections.observableArrayList("Checking", "Saving", "Business"));
            }

            // Setup add account button
            if (add_account_btn != null) {
                add_account_btn.setOnAction(event -> onAddAccount());
            }

            // Setup update account button
            if (update_account_btn != null) {
                update_account_btn.setOnAction(event -> onUpdateAccount());
            }

            // Hide error labels by default
            clearError(add_account_error_lbl);
            clearError(remove_account_error_lbl);
            clearError(update_account_error_lbl);

            // Setup remove account button
            if (remove_account_btn != null) {
                remove_account_btn.setOnAction(event -> onRemoveAccount());
            }

            if (Model.getInstance().getCurrentUser() != null) {
                if (accounts_list != null) {
                    // Reload accounts if empty
                    if (Model.getInstance().getCurrentUser().getBankAccounts() == null ||
                            Model.getInstance().getCurrentUser().getBankAccounts().isEmpty()) {
                        Model.getInstance().reloadUserBankAccounts();
                    }

                    accounts_list.setItems(Model.getInstance().getCurrentUser().getBankAccounts());

                    // Set custom cell factory to display account information with better styling
                    // for scrolling
                    accounts_list.setCellFactory(listView -> {
                        ListCell<BankAccount> cell = new ListCell<BankAccount>() {
                            @Override
                            protected void updateItem(BankAccount account, boolean empty) {
                                super.updateItem(account, empty);
                                if (empty || account == null) {
                                    setText("");
                                    setStyle("");
                                    setPrefHeight(0);
                                } else {
                                    // Enhanced display with better formatting
                                    setText(account.getBankName() + " - " + account.getAccountNumber() +
                                            " (" + account.getAccountType() + ") - $"
                                            + String.format("%.2f", account.getBalance()));

                                    setPrefHeight(45); // Fixed height for each item for consistent scrolling
                                }
                            }
                        };

                        // Add listener to update style when selection changes
                        cell.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                            if (cell.getItem() != null) {
                                if (isNowSelected) {
                                    cell.setStyle(
                                            "-fx-font-size: 12px; -fx-padding: 10px; -fx-background-color: #e3f2fd; -fx-border-color: #2196F3; -fx-border-width: 0 0 2 0; -fx-font-weight: bold;");
                                } else {
                                    cell.setStyle(
                                            "-fx-font-size: 12px; -fx-padding: 10px; -fx-background-color: #f9f9f9; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
                                }
                            }
                        });

                        // Set initial style
                        cell.setStyle(
                                "-fx-font-size: 12px; -fx-padding: 10px; -fx-background-color: #f9f9f9; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");

                        return cell;
                    });

                    // Enable smooth scrolling
                    accounts_list.setFocusTraversable(true);

                    // Add keyboard navigation for better scrolling
                    accounts_list.setOnKeyPressed(event -> {
                        switch (event.getCode()) {
                            case UP, DOWN:
                                // Ensure selected item is visible when navigating with keyboard
                                int selectedIndex = accounts_list.getSelectionModel().getSelectedIndex();
                                if (selectedIndex >= 0) {
                                    accounts_list.scrollTo(selectedIndex);
                                }
                                break;
                            default:
                                // Ignore other keys
                                break;
                        }
                    });

                    accounts_list.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                        if (newVal != null) {
                            // Scroll to selected item to ensure it's visible
                            int selectedIndex = accounts_list.getSelectionModel().getSelectedIndex();
                            if (selectedIndex >= 0) {
                                accounts_list.scrollTo(selectedIndex);
                            }

                            if (account_num_lbl != null) {
                                account_num_lbl.setText(newVal.getAccountNumber());
                            }
                            if (bank_name_lbl != null) {
                                bank_name_lbl.setText(newVal.getBankName());
                            }
                            if (balance_lbl != null) {
                                balance_lbl.setText("$" + String.format("%.2f", newVal.getBalance()));
                            }
                            if (account_type_lbl != null) {
                                account_type_lbl.setText(newVal.getAccountType());
                            }
                            // Populate update fields
                            if (update_bank_name_fld != null) {
                                update_bank_name_fld.setText(newVal.getBankName());
                            }
                            if (update_account_type_combo != null) {
                                update_account_type_combo.setValue(newVal.getAccountType());
                            }
                        } else {
                            // Clear labels when no selection
                            if (account_num_lbl != null)
                                account_num_lbl.setText("-");
                            if (bank_name_lbl != null)
                                bank_name_lbl.setText("-");
                            if (balance_lbl != null)
                                balance_lbl.setText("$0.00");
                            if (account_type_lbl != null)
                                account_type_lbl.setText("-");
                            if (update_bank_name_fld != null)
                                update_bank_name_fld.setText("");
                            if (update_account_type_combo != null)
                                update_account_type_combo.setValue(null);
                        }
                    });

                    // Select first account if available
                    if (!Model.getInstance().getCurrentUser().getBankAccounts().isEmpty()) {
                        accounts_list.getSelectionModel().select(0);
                    }
                }
            }

            // Add page load animations
            Platform.runLater(() -> {
                animatePageLoad();
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in AccountsController.initialize: " + e.getMessage());
            System.err.println("Exception type: " + e.getClass().getName());
            if (e.getCause() != null) {
                System.err.println("Caused by: " + e.getCause().getMessage());
                e.getCause().printStackTrace();
            }
        }
    }

    /**
     * Animate page load with fade-in and slide-up
     */
    private void animatePageLoad() {
        if (accounts_list != null && accounts_list.getScene() != null) {
            javafx.scene.Parent root = accounts_list.getScene().getRoot();
            if (root != null) {
                root.setOpacity(0);
                root.setTranslateY(20);
                AnimationUtils.fadeInSlideUp(root, 20, AnimationUtils.ENTRANCE_DURATION).play();
            }
        }
    }

    private void showError(Label label, String message) {
        if (label != null) {
            label.setText(message);
            label.setVisible(true);
            label.setManaged(true);
        }
    }

    private void clearError(Label label) {
        if (label != null) {
            label.setText("");
            label.setVisible(false);
            label.setManaged(false);
        }
    }

    private void onAddAccount() {
        // Clear previous error
        clearError(add_account_error_lbl);

        // Validate input
        String accountNumber = new_account_num_fld != null ? new_account_num_fld.getText().trim() : "";
        String bankName = new_bank_name_fld != null ? new_bank_name_fld.getText().trim() : "";
        String accountType = account_type_combo != null ? account_type_combo.getValue() : "";
        String balanceStr = new_balance_fld != null ? new_balance_fld.getText().trim() : "";

        if (accountNumber.isEmpty() || bankName.isEmpty() || accountType == null || balanceStr.isEmpty()) {
            showError(add_account_error_lbl, "Please fill all required fields");
            return;
        }

        // Validate account number format (alphanumeric, reasonable length)
        if (!accountNumber.matches("^[A-Za-z0-9]{6,20}$")) {
            showError(add_account_error_lbl, "Account number must be 6-20 alphanumeric characters");
            return;
        }

        // Validate balance
        double balance;
        try {
            balance = Double.parseDouble(balanceStr);
            if (balance < 0) {
                showError(add_account_error_lbl, "Balance cannot be negative");
                return;
            }
        } catch (NumberFormatException e) {
            showError(add_account_error_lbl, "Please enter a valid balance amount");
            return;
        }

        // Check if account number already exists
        if (Model.getInstance().getCurrentUser() != null) {
            for (BankAccount acc : Model.getInstance().getCurrentUser().getBankAccounts()) {
                if (acc.getAccountNumber().equals(accountNumber)) {
                    showError(add_account_error_lbl, "Account number already exists");
                    return;
                }
            }
        }

        // Add account
        if (Model.getInstance().addBankAccount(accountNumber, bankName, balance, accountType)) {
            // Reload bank accounts from database to ensure consistency
            if (Model.getInstance().getCurrentUser() != null) {
                Model.getInstance().reloadUserBankAccounts();
            }

            // Show success message
            DialogUtils.showSuccess("Success", "Bank account has been added successfully!");

            // Clear form
            if (new_account_num_fld != null)
                new_account_num_fld.setText("");
            if (new_bank_name_fld != null)
                new_bank_name_fld.setText("");
            if (new_balance_fld != null)
                new_balance_fld.setText("");
            if (account_type_combo != null)
                account_type_combo.setValue("Checking");

            // Refresh list and select new account
            if (accounts_list != null && Model.getInstance().getCurrentUser() != null) {
                accounts_list.setItems(Model.getInstance().getCurrentUser().getBankAccounts());
                // Select the newly added account
                for (BankAccount acc : Model.getInstance().getCurrentUser().getBankAccounts()) {
                    if (acc.getAccountNumber().equals(accountNumber)) {
                        accounts_list.getSelectionModel().select(acc);
                        break;
                    }
                }
            }
        } else {
            showError(add_account_error_lbl, "Failed to add account. Account number may already exist.");
        }
    }

    private void onRemoveAccount() {
        // Clear previous error
        clearError(remove_account_error_lbl);

        // Get selected account
        BankAccount selectedAccount = accounts_list != null ? accounts_list.getSelectionModel().getSelectedItem()
                : null;

        if (selectedAccount == null) {
            showError(remove_account_error_lbl, "Please select an account to remove");
            return;
        }

        // Confirm deletion
        boolean confirmed = DialogUtils.showDestructiveConfirmation(
                "Confirm Account Removal",
                "Are you sure you want to remove account " + selectedAccount.getAccountNumber() +
                        " (" + selectedAccount.getBankName() + ")?");

        if (confirmed) {
            // Check if this is the last account
            if (Model.getInstance().getCurrentUser() != null &&
                    Model.getInstance().getCurrentUser().getBankAccounts().size() <= 1) {
                showError(remove_account_error_lbl,
                        "Cannot remove the last account. You must have at least one account.");
                return;
            }

            // Remove account
            if (Model.getInstance().removeBankAccount(selectedAccount.getAccountNumber())) {
                // Reload bank accounts from database to ensure consistency
                if (Model.getInstance().getCurrentUser() != null) {
                    Model.getInstance().reloadUserBankAccounts();
                }

                // Show success message
                DialogUtils.showSuccess("Success", "Bank account has been removed successfully!");

                // Clear details and refresh list
                if (account_num_lbl != null)
                    account_num_lbl.setText("-");
                if (bank_name_lbl != null)
                    bank_name_lbl.setText("-");
                if (balance_lbl != null)
                    balance_lbl.setText("$0.00");
                if (account_type_lbl != null)
                    account_type_lbl.setText("-");
                if (update_bank_name_fld != null)
                    update_bank_name_fld.setText("");
                if (update_account_type_combo != null)
                    update_account_type_combo.setValue(null);

                // Refresh list
                if (accounts_list != null && Model.getInstance().getCurrentUser() != null) {
                    accounts_list.setItems(Model.getInstance().getCurrentUser().getBankAccounts());
                    // Select first account if available
                    if (!Model.getInstance().getCurrentUser().getBankAccounts().isEmpty()) {
                        accounts_list.getSelectionModel().select(0);
                    }
                }
            } else {
                showError(remove_account_error_lbl, "Failed to remove account. Please try again.");
            }
        }
    }

    private void onUpdateAccount() {
        // Clear previous error
        clearError(update_account_error_lbl);

        // Get selected account
        BankAccount selectedAccount = accounts_list != null ? accounts_list.getSelectionModel().getSelectedItem()
                : null;

        if (selectedAccount == null) {
            showError(update_account_error_lbl, "Please select an account to update");
            return;
        }

        // Get updated values
        String bankName = update_bank_name_fld != null ? update_bank_name_fld.getText().trim() : "";
        String accountType = update_account_type_combo != null ? update_account_type_combo.getValue() : "";

        // Validate input
        if (bankName.isEmpty() || accountType == null || accountType.isEmpty()) {
            showError(update_account_error_lbl, "Please fill all fields");
            return;
        }

        // Update account
        if (Model.getInstance().updateBankAccount(selectedAccount.getAccountNumber(), bankName, accountType)) {
            // Reload bank accounts from database to ensure consistency
            if (Model.getInstance().getCurrentUser() != null) {
                Model.getInstance().reloadUserBankAccounts();
            }

            // Show success message
            DialogUtils.showSuccess("Success", "Bank account information has been updated successfully!");

            // Refresh list to show updated values
            if (accounts_list != null && Model.getInstance().getCurrentUser() != null) {
                accounts_list.setItems(Model.getInstance().getCurrentUser().getBankAccounts());
                // Reselect the updated account
                for (BankAccount acc : Model.getInstance().getCurrentUser().getBankAccounts()) {
                    if (acc.getAccountNumber().equals(selectedAccount.getAccountNumber())) {
                        accounts_list.getSelectionModel().select(acc);
                        break;
                    }
                }
            }
        } else {
            showError(update_account_error_lbl, "Failed to update account. Please try again.");
        }
    }
}
