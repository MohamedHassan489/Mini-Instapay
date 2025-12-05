package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Views.clientmenuoption;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientMenuController implements Initializable {
    public Button dashboard_btn;
    public Button transaction_btn;
    public Button accounts_btn;
    public Button send_money_btn;
    public Button profile_btn;
    public Button disputes_btn;
    public Button notifications_btn;
    public Button logout_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addlisteners();
    }

    private void addlisteners(){
        if (dashboard_btn != null) dashboard_btn.setOnAction(actionEvent -> onDashboard());
        if (transaction_btn != null) transaction_btn.setOnAction(actionEvent -> onTransactions());
        if (accounts_btn != null) accounts_btn.setOnAction(actionEvent -> onAccounts());
        if (send_money_btn != null) send_money_btn.setOnAction(actionEvent -> onSendMoney());
        if (profile_btn != null) profile_btn.setOnAction(actionEvent -> onProfile());
        if (disputes_btn != null) disputes_btn.setOnAction(actionEvent -> onDisputes());
        if (notifications_btn != null) notifications_btn.setOnAction(actionEvent -> onNotifications());
        if (logout_btn != null) logout_btn.setOnAction(actionEvent -> onLogout());
    }



    private void onLogout() {
        Stage stage = (Stage) logout_btn.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().getViewFactory().showLoginWindow();
        Model.getInstance().setUserLoginSuccessFlag(false);
        Model.getInstance().setCurrentUser(null);
    }

    private void onAccounts() {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set(clientmenuoption.ACCOUNTS);
    }

    private void onTransactions() {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set(clientmenuoption.TRANSACTIONS);
    }

    private void onDashboard() {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set(clientmenuoption.DASHBOARD);
    }

    private void onSendMoney() {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set(clientmenuoption.SEND_MONEY);
    }

    private void onProfile() {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set(clientmenuoption.PROFILE);
    }

    private void onDisputes() {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set(clientmenuoption.DISPUTES);
    }

    private void onNotifications() {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set(clientmenuoption.NOTIFICATIONS);
    }
}
