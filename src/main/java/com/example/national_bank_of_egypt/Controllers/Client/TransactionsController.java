package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Models.BankAccount;
import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Models.Transaction;
import com.example.national_bank_of_egypt.Views.TransactionCellFactory;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class TransactionsController implements Initializable {
    public ListView<Transaction> Transaction_ListView;
    public ComboBox<String> account_filter_combo;

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
                Transaction_ListView.setItems(Model.getInstance().getTransactions());
                Transaction_ListView.setCellFactory(e -> new TransactionCellFactory());
                
                System.out.println("TransactionsController initialized successfully. Loaded " + 
                    Model.getInstance().getTransactions().size() + " transactions.");
            } else {
                System.err.println("Warning: No current user found. Cannot load transactions.");
                if (Transaction_ListView != null) {
                    Transaction_ListView.setItems(FXCollections.observableArrayList());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in TransactionsController.initialize: " + e.getMessage());
            if (Transaction_ListView != null) {
                Transaction_ListView.setItems(FXCollections.observableArrayList());
            }
        }
    }

    private void initializeAccountFilter() {
        if (Model.getInstance().getCurrentUser() == null) return;
        
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
                // Extract account number from display text (format: "ACCOUNT_NUMBER - BANK_NAME (TYPE)")
                String accountNumber = selected.split(" - ")[0];
                // Load transactions for selected account
                Model.getInstance().loadTransactionsByAccount(accountNumber, -1);
            }
            // Refresh the list view
            Transaction_ListView.setItems(Model.getInstance().getTransactions());
        });
    }

    private void initAllTransactionList(){
        try {
            // Load transactions for the current user
            // -1 means load all transactions for the current user
            Model.getInstance().loadTransactions(-1);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading transactions: " + e.getMessage());
        }
    }
}
