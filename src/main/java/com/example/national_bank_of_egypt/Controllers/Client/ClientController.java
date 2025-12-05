package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    public BorderPane client_parent;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().addListener((observableValue, oldVal, newVal) -> {

            switch (newVal){
                case TRANSACTIONS -> client_parent.setCenter(Model.getInstance().getViewFactory().getTransactionsView());
                case ACCOUNTS -> client_parent.setCenter(Model.getInstance().getViewFactory().getAccountsView());
                case SEND_MONEY -> client_parent.setCenter(Model.getInstance().getViewFactory().getSendMoneyView());
                case PROFILE -> client_parent.setCenter(Model.getInstance().getViewFactory().getProfileView());
                case DISPUTES -> client_parent.setCenter(Model.getInstance().getViewFactory().getDisputesView());
                case NOTIFICATIONS -> client_parent.setCenter(Model.getInstance().getViewFactory().getNotificationsView());
                default -> client_parent.setCenter(Model.getInstance().getViewFactory().getDashboardview());
            }
        });

    }
}
