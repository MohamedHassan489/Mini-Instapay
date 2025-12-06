package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Models.BankAccount;
import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Models.TransactionLimit;
import com.example.national_bank_of_egypt.Transactions.TransactionType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class SendMoneyController implements Initializable {
    public TextField receiver_fld;
    public ComboBox<BankAccount> sourceAccount_combo;
    public TextField amount_fld;
    public ComboBox<TransactionType> transactionType_combo;
    public DatePicker scheduledDate_picker;
    public VBox scheduledDate_container;
    public TextArea message_fld;
    public Button send_btn;
    public Label error_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Ensure user is logged in
            if (Model.getInstance().getCurrentUser() == null) {
                if (error_lbl != null) {
                    error_lbl.setText("Please log in to send money");
                    error_lbl.setStyle("-fx-text-fill: red; -fx-font-size: 9;");
                }
                return;
            }
            
            // Initialize source account combo
            if (sourceAccount_combo != null) {
                var accounts = Model.getInstance().getCurrentUser().getBankAccounts();
                if (accounts == null || accounts.isEmpty()) {
                    // Reload accounts if empty
                    Model.getInstance().reloadUserBankAccounts();
                    accounts = Model.getInstance().getCurrentUser().getBankAccounts();
                }
                
                if (accounts != null && !accounts.isEmpty()) {
                    sourceAccount_combo.setItems(FXCollections.observableArrayList(accounts));
                    sourceAccount_combo.setValue(accounts.get(0));
                } else {
                    if (error_lbl != null) {
                        error_lbl.setText("No bank accounts found. Please add an account first.");
                        error_lbl.setStyle("-fx-text-fill: orange; -fx-font-size: 9;");
                    }
                }
            }
            
            // Initialize transaction type combo
            if (transactionType_combo != null) {
                transactionType_combo.setItems(FXCollections.observableArrayList(TransactionType.INSTANT, TransactionType.SCHEDULED));
                transactionType_combo.setValue(TransactionType.INSTANT);
                
                // Set cell factory to display readable names
                transactionType_combo.setButtonCell(new ListCell<TransactionType>() {
                    @Override
                    protected void updateItem(TransactionType item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText("");
                        } else {
                            setText(item == TransactionType.INSTANT ? "Instant Transfer" : "Scheduled Transfer");
                        }
                    }
                });
                
                transactionType_combo.setCellFactory(listView -> new ListCell<TransactionType>() {
                    @Override
                    protected void updateItem(TransactionType item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText("");
                        } else {
                            setText(item == TransactionType.INSTANT ? "Instant Transfer" : "Scheduled Transfer");
                        }
                    }
                });
                
                // Show/hide scheduled date picker based on transaction type
                transactionType_combo.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (scheduledDate_container != null) {
                        scheduledDate_container.setVisible(newValue == TransactionType.SCHEDULED);
                        if (newValue == TransactionType.SCHEDULED && scheduledDate_picker != null) {
                            scheduledDate_picker.setValue(LocalDate.now().plusDays(1));
                        }
                    }
                });
            }
            
            // Initialize scheduled date picker
            if (scheduledDate_picker != null) {
                try {
                    scheduledDate_picker.setValue(LocalDate.now().plusDays(1));
                    scheduledDate_picker.setDayCellFactory(picker -> new DateCell() {
                        @Override
                        public void updateItem(LocalDate date, boolean empty) {
                            super.updateItem(date, empty);
                            if (date != null) {
                                setDisable(empty || date.isBefore(LocalDate.now().plusDays(1)));
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Error initializing DatePicker: " + e.getMessage());
                }
            }
            
            // Set up account combo display
            if (sourceAccount_combo != null) {
                sourceAccount_combo.setButtonCell(new ListCell<BankAccount>() {
                    @Override
                    protected void updateItem(BankAccount item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText("Select account");
                        } else {
                            setText(item.getBankName() + " - " + item.getAccountNumber() + " ($" + String.format("%.2f", item.getBalance()) + ")");
                        }
                    }
                });
                
                sourceAccount_combo.setCellFactory(listView -> new ListCell<BankAccount>() {
                    @Override
                    protected void updateItem(BankAccount item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText("");
                        } else {
                            setText(item.getBankName() + " - " + item.getAccountNumber() + " ($" + String.format("%.2f", item.getBalance()) + ")");
                        }
                    }
                });
            }
            
            // Setup send button
            if (send_btn != null) {
                send_btn.setOnAction(event -> onSendMoney());
            }
            
            // Initialize error label
            if (error_lbl != null) {
                error_lbl.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (error_lbl != null) {
                String errorMsg = "Error initializing form";
                if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                    errorMsg += ": " + e.getMessage();
                }
                error_lbl.setText(errorMsg + "\nPlease try refreshing or restart the app.");
                error_lbl.setStyle("-fx-text-fill: red; -fx-font-size: 9;");
            }
        }
    }

    private void onSendMoney() {
        // Clear previous error
        if (error_lbl != null) {
            error_lbl.setText("");
        }
        
        String receiver = receiver_fld != null ? receiver_fld.getText().trim() : "";
        BankAccount sourceAccount = sourceAccount_combo != null ? sourceAccount_combo.getValue() : null;
        TransactionType transactionType = transactionType_combo != null ? transactionType_combo.getValue() : null;
        String amountStr = amount_fld != null ? amount_fld.getText().trim() : "";
        String message = message_fld != null ? message_fld.getText() : "";
        
        // Validate required fields
        if (receiver.isEmpty()) {
            if (error_lbl != null) {
                error_lbl.setText("Please enter receiver (phone number, account number, or username)");
            }
            return;
        }
        
        if (sourceAccount == null) {
            if (error_lbl != null) {
                error_lbl.setText("Please select a source account");
            }
            return;
        }
        
        if (amountStr.isEmpty()) {
            if (error_lbl != null) {
                error_lbl.setText("Please enter an amount");
            }
            return;
        }
        
        if (transactionType == null) {
            if (error_lbl != null) {
                error_lbl.setText("Please select a transaction type");
            }
            return;
        }
        
        // Validate scheduled date if scheduled transaction
        LocalDate scheduledDate = null;
        if (transactionType == TransactionType.SCHEDULED) {
            if (scheduledDate_picker == null || scheduledDate_picker.getValue() == null) {
                if (error_lbl != null) {
                    error_lbl.setText("Please select a scheduled date");
                }
                return;
            }
            scheduledDate = scheduledDate_picker.getValue();
            if (scheduledDate.isBefore(LocalDate.now().plusDays(1))) {
                if (error_lbl != null) {
                    error_lbl.setText("Scheduled date must be at least tomorrow");
                }
                return;
            }
        }
        
        // Validate amount
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                if (error_lbl != null) {
                    error_lbl.setText("Amount must be greater than 0");
                }
                return;
            }
            if (amount > 100000) {
                if (error_lbl != null) {
                    error_lbl.setText("Amount exceeds maximum limit of $100,000");
                }
                return;
            }
        } catch (NumberFormatException e) {
            if (error_lbl != null) {
                error_lbl.setText("Please enter a valid amount");
            }
            return;
        }
        
        // Check if sender has sufficient balance
        if (sourceAccount.getBalance() < amount) {
            if (error_lbl != null) {
                error_lbl.setText("Insufficient balance. Available: $" + String.format("%.2f", sourceAccount.getBalance()));
            }
            return;
        }
        
        // Disable button during processing
        if (send_btn != null) {
            send_btn.setDisable(true);
            send_btn.setText("Processing...");
        }
        
        // Send money
        String transactionTypeStr = transactionType.toString();
        boolean success = false;
        String errorMessage = "";
        
        try {
            success = Model.getInstance().sendMoney(receiver, sourceAccount.getAccountNumber(), amount, message, transactionTypeStr, scheduledDate);
            
            if (!success) {
                // Try to get more specific error information
                TransactionLimit limit = Model.getInstance().getTransactionLimit(Model.getInstance().getCurrentUser().getUserName());
                if (limit != null && (limit.isDailyLimitExceeded(amount) || limit.isWeeklyLimitExceeded(amount))) {
                    errorMessage = "Transaction limit exceeded. Daily: $" + String.format("%.2f", limit.getDailyLimitRemaining()) + " remaining, Weekly: $" + String.format("%.2f", limit.getWeeklyLimitRemaining()) + " remaining";
                } else {
                    errorMessage = "Transaction failed. Please check:\n- Receiver exists (phone/account/username/email)\n- Sufficient balance\n- Transaction limits";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "An error occurred: " + e.getMessage();
        } finally {
            // Re-enable button
            if (send_btn != null) {
                send_btn.setDisable(false);
                send_btn.setText("Send Money");
            }
        }
        
        if (success) {
            // Show success message
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Success");
            successAlert.setHeaderText("Transaction " + (transactionType == TransactionType.SCHEDULED ? "Scheduled" : "Completed"));
            if (transactionType == TransactionType.SCHEDULED) {
                successAlert.setContentText("Your transaction of $" + String.format("%.2f", amount) + " to " + receiver + " is scheduled for " + scheduledDate + ".");
            } else {
                successAlert.setContentText("Successfully sent $" + String.format("%.2f", amount) + " to " + receiver + "!");
            }
            successAlert.showAndWait();
            
            if (error_lbl != null) {
                error_lbl.setText("✓ " + (transactionType == TransactionType.SCHEDULED ? "Transaction scheduled" : "Transaction successful") + "!");
                error_lbl.setStyle("-fx-text-fill: green; -fx-font-size: 9;");
            }
            
            // Clear form
            if (receiver_fld != null) receiver_fld.setText("");
            if (amount_fld != null) amount_fld.setText("");
            if (message_fld != null) message_fld.setText("");
            if (transactionType_combo != null) transactionType_combo.setValue(TransactionType.INSTANT);
            
            // Refresh account list to show updated balance
            if (sourceAccount_combo != null && Model.getInstance().getCurrentUser() != null) {
                Model.getInstance().reloadUserBankAccounts();
                var accounts = Model.getInstance().getCurrentUser().getBankAccounts();
                if (accounts != null && !accounts.isEmpty()) {
                    sourceAccount_combo.setItems(FXCollections.observableArrayList(accounts));
                    // Find and select the same account if it still exists
                    BankAccount updatedAccount = accounts.stream()
                        .filter(acc -> acc.getAccountNumber().equals(sourceAccount.getAccountNumber()))
                        .findFirst()
                        .orElse(accounts.get(0));
                    sourceAccount_combo.setValue(updatedAccount);
                }
            }
        } else {
            if (error_lbl != null) {
                error_lbl.setText("✗ " + errorMessage);
                error_lbl.setStyle("-fx-text-fill: red; -fx-font-size: 9;");
            }
        }
    }
}

