package com.example.national_bank_of_egypt.Controllers.Admin;

import com.example.national_bank_of_egypt.Models.Client;
import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Views.ClientCellFactory;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class DepositController implements Initializable {
    public TextField UserName_fld;
    public Button search_btn;
    public ListView<Client> result_listView;
    public TextField amount_fld;
    public Button deposit_btn;
    private Client client;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        search_btn.setOnAction(actionEvent -> onClientSearch());
        deposit_btn.setOnAction(actionEvent -> onDeposit());
    }

    private void onClientSearch(){
        ObservableList<Client> searchResult = Model.getInstance().searchClient(UserName_fld.getText());
        result_listView.setItems(searchResult);
        result_listView.setCellFactory(clientListView ->  new ClientCellFactory());
        client = searchResult.getFirst();
    }
    public void onDeposit(){
        double amount = Double.parseDouble(amount_fld.getText());
        double newBalance = amount + client.savingAccountProperty().get().balanceProperty().get();
        if (amount_fld.getText() != null){
            Model.getInstance().getDataBaseDriver().DepositSaving(client.userNameProperty().get(),newBalance);
        }
        empty_Fld();
    }
    public void empty_Fld(){
        UserName_fld.setText("");
        amount_fld.setText("");
    }
}

