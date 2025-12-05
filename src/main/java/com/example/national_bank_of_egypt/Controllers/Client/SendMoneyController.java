package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Models.BankAccount;
import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Transactions.TransactionType;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class SendMoneyController implements Initializable {
    public TextField receiver_fld;
    public ComboBox<BankAccount> sourceAccount_combo;
    public TextField amount_fld;
    public ComboBox<TransactionType> transactionType_combo;
    public TextArea message_fld;
    public Button send_btn;
    public Label error_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (Model.getInstance().getCurrentUser() != null) {
            sourceAccount_combo.setItems(FXCollections.observableArrayList(Model.getInstance().getCurrentUser().getBankAccounts()));
            if (!Model.getInstance().getCurrentUser().getBankAccounts().isEmpty()) {
                sourceAccount_combo.setValue(Model.getInstance().getCurrentUser().getBankAccounts().get(0));
            }
        }
        transactionType_combo.setItems(FXCollections.observableArrayList(TransactionType.INSTANT, TransactionType.SCHEDULED));
        transactionType_combo.setValue(TransactionType.INSTANT);
        
        if (send_btn != null) {
            send_btn.setOnAction(event -> onSendMoney());
        }
    }

    private void onSendMoney() {
        String receiver = receiver_fld.getText();
        BankAccount sourceAccount = sourceAccount_combo.getValue();
        String transactionType = transactionType_combo.getValue().toString();
        
        if (receiver.isEmpty() || sourceAccount == null) {
            error_lbl.setText("Please fill all required fields");
            return;
        }
        
        try {
            double amount = Double.parseDouble(amount_fld.getText());
            String message = message_fld.getText();
            
            if (Model.getInstance().sendMoney(receiver, sourceAccount.getAccountNumber(), amount, message, transactionType)) {
                error_lbl.setText("Transaction successful!");
                receiver_fld.setText("");
                amount_fld.setText("");
                message_fld.setText("");
            } else {
                error_lbl.setText("Transaction failed. Please check receiver details and account balance.");
            }
        } catch (NumberFormatException e) {
            error_lbl.setText("Invalid amount");
        }
    }
}

