package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Models.BankAccount;
import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Reports.ReportService;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class BankStatementController implements Initializable {
    public ComboBox<BankAccount> account_combo;
    public DatePicker startDate_picker;
    public DatePicker endDate_picker;
    public Button generate_btn;
    public TextArea statement_area;
    public Label error_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Initialize account combo
            if (account_combo != null) {
                var accounts = Model.getInstance().getCurrentUser().getBankAccounts();
                if (accounts == null || accounts.isEmpty()) {
                    Model.getInstance().reloadUserBankAccounts();
                    accounts = Model.getInstance().getCurrentUser().getBankAccounts();
                }
                
                if (accounts != null && !accounts.isEmpty()) {
                    account_combo.setItems(FXCollections.observableArrayList(accounts));
                    account_combo.setValue(accounts.get(0));
                }
                
                // Set display format
                account_combo.setButtonCell(new javafx.scene.control.ListCell<BankAccount>() {
                    @Override
                    protected void updateItem(BankAccount item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText("All Accounts");
                        } else {
                            setText(item.getBankName() + " - " + item.getAccountNumber());
                        }
                    }
                });
                
                account_combo.setCellFactory(listView -> new javafx.scene.control.ListCell<BankAccount>() {
                    @Override
                    protected void updateItem(BankAccount item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText("");
                        } else {
                            setText(item.getBankName() + " - " + item.getAccountNumber());
                        }
                    }
                });
            }
            
            // Initialize date pickers
            if (startDate_picker != null) {
                startDate_picker.setValue(LocalDate.now().minusMonths(1));
            }
            if (endDate_picker != null) {
                endDate_picker.setValue(LocalDate.now());
            }
            
            // Setup generate button
            if (generate_btn != null) {
                generate_btn.setOnAction(event -> onGenerateStatement());
            }
            
            // Initialize error label
            if (error_lbl != null) {
                error_lbl.setText("");
            }
            
            // Initialize statement area
            if (statement_area != null) {
                statement_area.setEditable(false);
                statement_area.setWrapText(true);
                statement_area.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 11;");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (error_lbl != null) {
                error_lbl.setText("Error initializing: " + e.getMessage());
                error_lbl.setStyle("-fx-text-fill: red;");
            }
        }
    }

    private void onGenerateStatement() {
        // Clear previous error
        if (error_lbl != null) {
            error_lbl.setText("");
        }
        
        if (Model.getInstance().getCurrentUser() == null) {
            if (error_lbl != null) {
                error_lbl.setText("Please log in to generate bank statement");
                error_lbl.setStyle("-fx-text-fill: red;");
            }
            return;
        }
        
        String userId = Model.getInstance().getCurrentUser().getUserName();
        String accountNumber = null;
        
        // Get selected account
        if (account_combo != null && account_combo.getValue() != null) {
            accountNumber = account_combo.getValue().getAccountNumber();
        }
        
        // Get date range
        String startDate = null;
        String endDate = null;
        
        if (startDate_picker != null && startDate_picker.getValue() != null) {
            startDate = startDate_picker.getValue().format(DateTimeFormatter.ISO_DATE);
        }
        
        if (endDate_picker != null && endDate_picker.getValue() != null) {
            endDate = endDate_picker.getValue().format(DateTimeFormatter.ISO_DATE);
        }
        
        // Validate date range
        if (startDate != null && endDate != null) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            if (start.isAfter(end)) {
                if (error_lbl != null) {
                    error_lbl.setText("Start date must be before or equal to end date");
                    error_lbl.setStyle("-fx-text-fill: red;");
                }
                return;
            }
        }
        
        // Disable button during generation
        if (generate_btn != null) {
            generate_btn.setDisable(true);
            generate_btn.setText("Generating...");
        }
        
        try {
            // Generate statement
            ReportService reportService = ReportService.getInstance();
            String statement = reportService.generateBankStatement(userId, startDate, endDate, accountNumber);
            
            if (statement_area != null) {
                statement_area.setText(statement);
            }
            
            if (error_lbl != null) {
                error_lbl.setText("Statement generated successfully!");
                error_lbl.setStyle("-fx-text-fill: green;");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (error_lbl != null) {
                error_lbl.setText("Error generating statement: " + e.getMessage());
                error_lbl.setStyle("-fx-text-fill: red;");
            }
            if (statement_area != null) {
                statement_area.setText("Error generating bank statement. Please try again.");
            }
        } finally {
            // Re-enable button
            if (generate_btn != null) {
                generate_btn.setDisable(false);
                generate_btn.setText("Generate Statement");
            }
        }
    }
}
