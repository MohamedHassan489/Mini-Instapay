package com.example.national_bank_of_egypt.Controllers.Admin;

import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Models.Transaction;
import com.example.national_bank_of_egypt.Views.TransactionCellFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminTransactionsController implements Initializable {
    public ListView<Transaction> transactions_list;
    public ComboBox<String> filter_combo;
    public TextField search_fld;
    public Button flagSuspicious_btn;
    public Label status_lbl;
    
    private ObservableList<Transaction> allTransactions;
    private FilteredList<Transaction> filteredTransactions;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        filter_combo.setItems(FXCollections.observableArrayList("All", "Success", "Pending", "Failed", "Suspicious"));
        filter_combo.setValue("All");
        
        // Load all transactions
        Model.getInstance().loadAllTransactions();
        allTransactions = Model.getInstance().getTransactions();
        
        // Create filtered list
        filteredTransactions = new FilteredList<>(allTransactions, p -> true);
        transactions_list.setItems(filteredTransactions);
        transactions_list.setCellFactory(e -> new TransactionCellFactory());
        
        if (flagSuspicious_btn != null) {
            flagSuspicious_btn.setOnAction(event -> onFlagSuspicious());
        }
        
        if (search_fld != null) {
            search_fld.textProperty().addListener((observable, oldValue, newValue) -> filterTransactions());
        }
        
        if (filter_combo != null) {
            filter_combo.valueProperty().addListener((observable, oldValue, newValue) -> filterTransactions());
        }
    }
    
    private void filterTransactions() {
        String searchText = search_fld != null ? search_fld.getText().toLowerCase() : "";
        String statusFilter = filter_combo != null ? filter_combo.getValue() : "All";
        
        filteredTransactions.setPredicate(transaction -> {
            // Status filter
            boolean statusMatch = statusFilter.equals("All") || 
                                 transaction.getStatus().equalsIgnoreCase(statusFilter);
            
            if (!statusMatch) {
                return false;
            }
            
            // Search filter
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            
            return transaction.getTransactionId().toLowerCase().contains(searchText) ||
                   transaction.getSender().toLowerCase().contains(searchText) ||
                   transaction.getReceiver().toLowerCase().contains(searchText) ||
                   String.valueOf(transaction.getAmount()).contains(searchText) ||
                   transaction.getSenderAccount().toLowerCase().contains(searchText) ||
                   transaction.getReceiverAccount().toLowerCase().contains(searchText);
        });
    }
    
    private void onFlagSuspicious() {
        Transaction selected = transactions_list.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Flag Transaction");
            confirmAlert.setHeaderText("Confirm Flagging");
            confirmAlert.setContentText("Are you sure you want to flag this transaction as suspicious?\nTransaction ID: " + selected.getTransactionId());
            
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (Model.getInstance().flagTransactionAsSuspicious(selected.getTransactionId())) {
                        status_lbl.setText("Transaction flagged as suspicious");
                        status_lbl.setStyle("-fx-text-fill: green;");
                        // Refresh the list
                        Model.getInstance().loadAllTransactions();
                        filterTransactions();
                    } else {
                        status_lbl.setText("Failed to flag transaction");
                        status_lbl.setStyle("-fx-text-fill: red;");
                    }
                }
            });
        } else {
            status_lbl.setText("Please select a transaction");
            status_lbl.setStyle("-fx-text-fill: red;");
        }
    }
}

