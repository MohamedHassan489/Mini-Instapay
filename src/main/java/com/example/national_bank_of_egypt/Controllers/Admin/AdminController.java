package com.example.national_bank_of_egypt.Controllers.Admin;

import com.example.national_bank_of_egypt.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;


public class AdminController implements Initializable {
    public BorderPane admin_parent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Model.getInstance().getViewFactory().getAdminselectedmenuitem().addListener((observableValue, oldVal, newVal) -> {
            switch (newVal){
                case USERS -> admin_parent.setCenter(Model.getInstance().getViewFactory().getUsersView());
                case REPORTS -> admin_parent.setCenter(Model.getInstance().getViewFactory().getReportsView());
                case SYSTEM_HEALTH -> admin_parent.setCenter(Model.getInstance().getViewFactory().getSystemHealthView());
                default -> admin_parent.setCenter(Model.getInstance().getViewFactory().getUsersView());
            }
        });
    }
}
