package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Models.Transaction;
import com.example.national_bank_of_egypt.Views.TransactionCellFactory;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class TransactionsController implements Initializable {
    public ListView<Transaction> Transaction_ListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            if (Transaction_ListView == null) {
                System.err.println("Error: Transaction_ListView is null in TransactionsController");
                return;
            }
            
            // Load transactions for current user
            if (Model.getInstance().getCurrentUser() != null) {
                initAllTransactionList();
                Transaction_ListView.setItems(Model.getInstance().getTransactions());
                Transaction_ListView.setCellFactory(e -> new TransactionCellFactory());
                
                System.out.println("TransactionsController initialized successfully. Loaded " + 
                    Model.getInstance().getTransactions().size() + " transactions.");
            } else {
                System.err.println("Warning: No current user found. Cannot load transactions.");
                if (Transaction_ListView != null) {
                    Transaction_ListView.setItems(javafx.collections.FXCollections.observableArrayList());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in TransactionsController.initialize: " + e.getMessage());
            if (Transaction_ListView != null) {
                Transaction_ListView.setItems(javafx.collections.FXCollections.observableArrayList());
            }
        }
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
