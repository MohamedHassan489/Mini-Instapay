package com.example.national_bank_of_egypt.Controllers.Admin;

import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Views.AdminMenuOption;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
public class AdminMenuController implements Initializable {
    public Button users_btn;
    public Button transactions_btn;
    public Button disputes_btn;
    public Button reports_btn;
    public Button system_health_btn;
    public Button logout_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();
    }

    private void addListeners(){
        if (logout_btn != null) logout_btn.setOnAction(actionEvent -> onLogout());
        if (users_btn != null) users_btn.setOnAction(actionEvent -> onUsers());
        if (transactions_btn != null) transactions_btn.setOnAction(actionEvent -> onTransactions());
        if (disputes_btn != null) disputes_btn.setOnAction(actionEvent -> onDisputes());
        if (reports_btn != null) reports_btn.setOnAction(actionEvent -> onReports());
        if (system_health_btn != null) system_health_btn.setOnAction(actionEvent -> onSystemHealth());
    }

    private void onUsers(){
        Model.getInstance().getViewFactory().getAdminselectedmenuitem().set(AdminMenuOption.USERS);
    }

    private void onTransactions(){
        Model.getInstance().getViewFactory().getAdminselectedmenuitem().set(AdminMenuOption.TRANSACTIONS);
    }

    private void onDisputes(){
        Model.getInstance().getViewFactory().getAdminselectedmenuitem().set(AdminMenuOption.DISPUTES);
    }

    private void onReports(){
        Model.getInstance().getViewFactory().getAdminselectedmenuitem().set(AdminMenuOption.REPORTS);
    }

    private void onSystemHealth(){
        Model.getInstance().getViewFactory().getAdminselectedmenuitem().set(AdminMenuOption.SYSTEM_HEALTH);
    }

    private void onLogout() {
        Stage stage = (Stage) logout_btn.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().getViewFactory().showLoginWindow();
        Model.getInstance().setAdminLoginSuccessFlag(false);
    }


}
