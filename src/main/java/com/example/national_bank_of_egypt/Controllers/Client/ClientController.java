package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Models.Model;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    public BorderPane client_parent;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up listener first
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().addListener((observableValue, oldVal, newVal) -> {
            if (client_parent == null) {
                System.err.println("Warning: client_parent is null in ClientController listener");
                return;
            }
            switch (newVal){
                case DASHBOARD -> {
                    var dashboard = Model.getInstance().getViewFactory().getDashboardview();
                    if (dashboard != null) {
                        client_parent.setCenter(dashboard);
                    } else {
                        System.err.println("Error: Dashboard view is null");
                    }
                }
                case TRANSACTIONS -> {
                    var view = Model.getInstance().getViewFactory().getTransactionsView();
                    if (view != null) client_parent.setCenter(view);
                }
                case ACCOUNTS -> {
                    var view = Model.getInstance().getViewFactory().getAccountsView();
                    if (view != null) client_parent.setCenter(view);
                }
                case SEND_MONEY -> {
                    var view = Model.getInstance().getViewFactory().getSendMoneyView();
                    if (view != null) client_parent.setCenter(view);
                }
                case PROFILE -> {
                    var view = Model.getInstance().getViewFactory().getProfileView();
                    if (view != null) client_parent.setCenter(view);
                }
                case DISPUTES -> {
                    var view = Model.getInstance().getViewFactory().getDisputesView();
                    if (view != null) client_parent.setCenter(view);
                }
                case NOTIFICATIONS -> {
                    var view = Model.getInstance().getViewFactory().getNotificationsView();
                    if (view != null) client_parent.setCenter(view);
                }
                default -> {
                    var dashboard = Model.getInstance().getViewFactory().getDashboardview();
                    if (dashboard != null) client_parent.setCenter(dashboard);
                }
            }
        });
        
        // Set default view to Dashboard after listener is set up
        // Use Platform.runLater to ensure UI is ready
        Platform.runLater(() -> {
            if (client_parent != null) {
                // Directly set the dashboard view first to ensure it displays
                var dashboard = Model.getInstance().getViewFactory().getDashboardview();
                if (dashboard != null) {
                    client_parent.setCenter(dashboard);
                }
                // Then set the menu item to keep state consistent
                Model.getInstance().getViewFactory().getClientSelectedMenuItem().set(com.example.national_bank_of_egypt.Views.clientmenuoption.DASHBOARD);
            }
        });
    }
}
