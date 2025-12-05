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
    public Label total_balance;
    public Label account_count;
    public Label income_bal;
    public Label expenses_bal;
    public ListView<Transaction> Transaction_list;
    public TextField username_fld;
    public TextField amount_fld;
    public TextArea message_fld;
    public Button send_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ShowData();
        initLatestTransactions();
        Transaction_list.setItems(Model.getInstance().getTransactions());
        Transaction_list.setCellFactory(e -> new TransactionCellFactory());
        accountSummary();
        if (send_btn != null) {
            send_btn.setOnAction(event -> onSendMoney());
        }
    }

    private void ShowData(){
        if (Model.getInstance().getCurrentUser() != null) {
            userName.textProperty().bind(Bindings.concat("Hi, ").concat(Model.getInstance().getCurrentUser().firstNameProperty()));
            login_date.setText("Today, " + LocalDate.now());
            
            double totalBal = Model.getInstance().getCurrentUser().getBankAccounts().stream()
                .mapToDouble(acc -> acc.getBalance())
                .sum();
            total_balance.setText("$" + totalBal);
            account_count.setText(String.valueOf(Model.getInstance().getCurrentUser().getBankAccounts().size()));
        }
    }

    private void initLatestTransactions(){
        Model.getInstance().loadTransactions(4);
    }

    private void accountSummary(){
        double income = 0;
        double expenses = 0;
        Model.getInstance().loadTransactions(-1);
        for (Transaction transaction : Model.getInstance().getTransactions()){
            if (transaction.getSender().equals(Model.getInstance().getCurrentUser().getUserName())){
                expenses += transaction.getAmount();
            }else {
                income += transaction.getAmount();
            }
        }
        income_bal.setText("+$" + income);
        expenses_bal.setText("-$" + expenses);
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
