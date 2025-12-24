package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Models.BankAccount;
import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Models.Transaction;
import com.example.national_bank_of_egypt.Utils.AnimationUtils;
import com.example.national_bank_of_egypt.Views.TransactionCellFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class TransactionsController implements Initializable {
    public ListView<Transaction> Transaction_ListView;
    public ComboBox<String> account_filter_combo;
    @FXML
    public TextField search_field;
    @FXML
    public VBox empty_state_container;

    private FilteredList<Transaction> filteredTransactions;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            if (Transaction_ListView == null) {
                System.err.println("Error: Transaction_ListView is null in TransactionsController");
                return;
            }

            // Load transactions for current user
            if (Model.getInstance().getCurrentUser() != null) {
                // Initialize account filter ComboBox
                if (account_filter_combo != null) {
                    initializeAccountFilter();
                }

                // Load all transactions initially
                initAllTransactionList();
                ObservableList<Transaction> allTransactions = Model.getInstance().getTransactions();
                filteredTransactions = new FilteredList<>(allTransactions, p -> true);

                Transaction_ListView.setItems(filteredTransactions);
                Transaction_ListView.setCellFactory(e -> new TransactionCellFactory());

                // Setup search functionality
                if (search_field != null) {
                    search_field.textProperty().addListener((observable, oldValue, newValue) -> {
                        filterTransactions(newValue);
                    });
                }

                // Setup empty state
                updateEmptyState();
                filteredTransactions
                        .addListener((javafx.collections.ListChangeListener.Change<? extends Transaction> c) -> {
                            updateEmptyState();
                        });
            } else {
                System.err.println("Warning: No current user found. Cannot load transactions.");
                if (Transaction_ListView != null) {
                    Transaction_ListView.setItems(FXCollections.observableArrayList());
                }
                updateEmptyState();
            }

            // Add page load animations
            Platform.runLater(() -> {
                animatePageLoad();
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in TransactionsController.initialize: " + e.getMessage());
            if (Transaction_ListView != null) {
                Transaction_ListView.setItems(FXCollections.observableArrayList());
            }
        }
    }

    /**
     * Animate page load with fade-in and slide-up
     */
    private void animatePageLoad() {
        if (Transaction_ListView != null && Transaction_ListView.getScene() != null) {
            javafx.scene.Parent root = Transaction_ListView.getScene().getRoot();
            if (root != null) {
                root.setOpacity(0);
                root.setTranslateY(20);
                AnimationUtils.fadeInSlideUp(root, 20, AnimationUtils.ENTRANCE_DURATION).play();
            }
        }
    }

    private void initializeAccountFilter() {
        if (Model.getInstance().getCurrentUser() == null)
            return;

        // Add "All Accounts" option
        account_filter_combo.getItems().add("All Accounts");

        // Add all user accounts
        for (BankAccount account : Model.getInstance().getCurrentUser().getBankAccounts()) {
            String displayText = account.getAccountNumber() + " - " + account.getBankName() +
                    " (" + account.getAccountType() + ")";
            account_filter_combo.getItems().add(displayText);
        }

        // Set default to "All Accounts"
        account_filter_combo.setValue("All Accounts");

        // Add listener to filter transactions when account selection changes
        account_filter_combo.setOnAction(event -> {
            String selected = account_filter_combo.getSelectionModel().getSelectedItem();
            if (selected == null || selected.equals("All Accounts")) {
                // Load all transactions
                initAllTransactionList();
            } else {
                // Extract account number from display text (format: "ACCOUNT_NUMBER - BANK_NAME
                // (TYPE)")
                String accountNumber = selected.split(" - ")[0];
                // Load transactions for selected account
                Model.getInstance().loadTransactionsByAccount(accountNumber, -1);
            }
            // Update filtered list with new transactions
            ObservableList<Transaction> allTransactions = Model.getInstance().getTransactions();
            filteredTransactions = new FilteredList<>(allTransactions, p -> true);
            Transaction_ListView.setItems(filteredTransactions);

            // Reapply search filter if there's text in search field
            if (search_field != null && search_field.getText() != null && !search_field.getText().isEmpty()) {
                filterTransactions(search_field.getText());
            }

            // Update empty state
            updateEmptyState();
        });
    }

    private void initAllTransactionList() {
        try {
            // Load transactions for the current user
            // -1 means load all transactions for the current user
            if (Model.getInstance().getCurrentUser() != null) {
                Model.getInstance().loadTransactions(-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading transactions: " + e.getMessage());
        }
    }

    private void filterTransactions(String searchText) {
        if (filteredTransactions == null)
            return;

        if (searchText == null || searchText.isEmpty()) {
            filteredTransactions.setPredicate(transaction -> true);
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            filteredTransactions.setPredicate(transaction -> {
                return String.valueOf(transaction.getAmount()).contains(lowerCaseFilter) ||
                        (transaction.getSender() != null
                                && transaction.getSender().toLowerCase().contains(lowerCaseFilter))
                        ||
                        (transaction.getReceiver() != null
                                && transaction.getReceiver().toLowerCase().contains(lowerCaseFilter))
                        ||
                        (transaction.getTransactionId() != null
                                && transaction.getTransactionId().toLowerCase().contains(lowerCaseFilter));
            });
        }
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (empty_state_container == null || Transaction_ListView == null)
            return;

        boolean isEmpty = filteredTransactions == null || filteredTransactions.isEmpty();
        empty_state_container.setVisible(isEmpty);
        Transaction_ListView.setVisible(!isEmpty);
    }
}
