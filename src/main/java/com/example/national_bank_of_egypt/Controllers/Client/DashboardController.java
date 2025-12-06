package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Models.Transaction;
import com.example.national_bank_of_egypt.Views.TransactionCellFactory;
import javafx.beans.binding.Bindings;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    public Text userName;
    public Label login_date;
    public Label income_bal;
    public Label expenses_bal;
    public ListView<Transaction> Transaction_list;
    public TextField username_fld;
    public TextField amount_fld;
    public TextArea message_fld;
    public Button send_btn;
    public Label checking_bal;
    public Label checking_acc_num;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Add null checks to prevent crashes
            if (userName == null || login_date == null) {
                System.err.println("Warning: Some FXML fields are null in DashboardController");
                return;
            }
            
            ShowData();
            initLatestTransactions();
            if (Transaction_list != null) {
                Transaction_list.setItems(Model.getInstance().getTransactions());
                Transaction_list.setCellFactory(e -> new TransactionCellFactory());
            }
            accountSummary();
            if (send_btn != null) {
                send_btn.setOnAction(event -> onSendMoney());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error initializing DashboardController: " + e.getMessage());
        }
    }

    private void ShowData(){
        try {
            if (Model.getInstance().getCurrentUser() != null && userName != null && login_date != null) {
                userName.textProperty().bind(Bindings.concat("Hi, ").concat(Model.getInstance().getCurrentUser().firstNameProperty()));
                login_date.setText("Today, " + LocalDate.now());
                
                // Display first account if available
                if (!Model.getInstance().getCurrentUser().getBankAccounts().isEmpty()) {
                    var firstAccount = Model.getInstance().getCurrentUser().getBankAccounts().get(0);
                    if (checking_bal != null) {
                        checking_bal.setText("$" + String.format("%.2f", firstAccount.getBalance()));
                    }
                    if (checking_acc_num != null) {
                        checking_acc_num.setText(firstAccount.getAccountNumber());
                    }
                } else {
                    // No accounts - set default values
                    if (checking_bal != null) {
                        checking_bal.setText("$0.00");
                    }
                    if (checking_acc_num != null) {
                        checking_acc_num.setText("No Account");
                    }
                }
            } else {
                // User not logged in - set default values
                if (userName != null) {
                    userName.setText("Hi, Guest");
                }
                if (login_date != null) {
                    login_date.setText("Today, " + LocalDate.now());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in ShowData: " + e.getMessage());
        }
    }

    private void initLatestTransactions(){
        Model.getInstance().loadTransactions(4);
    }

    private void accountSummary(){
        try {
            if (Model.getInstance().getCurrentUser() == null) return;
            
            double income = 0;
            double expenses = 0;
            Model.getInstance().loadTransactions(-1);
            for (Transaction transaction : Model.getInstance().getTransactions()){
                if (transaction.getSender() != null && transaction.getSender().equals(Model.getInstance().getCurrentUser().getUserName())){
                    expenses += transaction.getAmount();
                } else if (transaction.getReceiver() != null && transaction.getReceiver().equals(Model.getInstance().getCurrentUser().getUserName())){
                    income += transaction.getAmount();
                }
            }
            if (income_bal != null) {
                income_bal.setText("+$" + String.format("%.2f", income));
            }
            if (expenses_bal != null) {
                expenses_bal.setText("-$" + String.format("%.2f", expenses));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSendMoney(){
        if (Model.getInstance().getCurrentUser() == null) return;
        
        String receiverIdentifier = username_fld.getText();
        if (receiverIdentifier.isEmpty()) {
            return;
        }
        
        try {
            double amount = Double.parseDouble(amount_fld.getText());
            String message = message_fld.getText();
            
            if (Model.getInstance().getCurrentUser().getBankAccounts().isEmpty()) {
                return;
            }
            
            String senderAccount = Model.getInstance().getCurrentUser().getBankAccounts().get(0).getAccountNumber();
            
            if (Model.getInstance().sendMoney(receiverIdentifier, senderAccount, amount, message, "INSTANT")) {
                username_fld.setText("");
                amount_fld.setText("");
                message_fld.setText("");
                ShowData();
                initLatestTransactions();
                accountSummary();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
