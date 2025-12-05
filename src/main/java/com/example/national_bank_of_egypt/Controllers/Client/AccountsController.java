package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Models.BankAccount;
import com.example.national_bank_of_egypt.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountsController implements Initializable {
    public ListView<BankAccount> accounts_list;
    public Label account_num_lbl;
    public Label bank_name_lbl;
    public Label balance_lbl;
    public Label account_type_lbl;
    public Button add_account_btn;
    public Button remove_account_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (Model.getInstance().getCurrentUser() != null) {
            accounts_list.setItems(Model.getInstance().getCurrentUser().getBankAccounts());
            accounts_list.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    account_num_lbl.setText(newVal.getAccountNumber());
                    bank_name_lbl.setText(newVal.getBankName());
                    balance_lbl.setText("$" + newVal.getBalance());
                    account_type_lbl.setText(newVal.getAccountType());
                }
            });
        }
    }
}
