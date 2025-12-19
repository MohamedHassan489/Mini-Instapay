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
                    // #region agent log
                    try {
                        java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"), 
                            ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"D2\",\"location\":\"ClientController.java:22\",\"message\":\"DASHBOARD case entered\",\"timestamp\":" + System.currentTimeMillis() + "}\n").getBytes(), 
                            java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
                    } catch (Exception logEx) {}
                    // #endregion
                    var dashboard = Model.getInstance().getViewFactory().getDashboardview();
                    // #region agent log
                    try {
                        java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"), 
                            ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"D2\",\"location\":\"ClientController.java:25\",\"message\":\"got dashboard view\",\"timestamp\":" + System.currentTimeMillis() + ",\"data\":{\"dashboardNull\":" + (dashboard == null) + "}}\n").getBytes(), 
                            java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
                    } catch (Exception logEx) {}
                    // #endregion
                    if (dashboard != null) {
                        client_parent.setCenter(dashboard);
                        // #region agent log
                        try {
                            java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"), 
                                ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"D2\",\"location\":\"ClientController.java:29\",\"message\":\"setCenter called with dashboard\",\"timestamp\":" + System.currentTimeMillis() + "}\n").getBytes(), 
                                java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
                        } catch (Exception logEx) {}
                        // #endregion
                    } else {
                        // #region agent log
                        try {
                            java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"), 
                                ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"D2\",\"location\":\"ClientController.java:32\",\"message\":\"Dashboard view is null\",\"timestamp\":" + System.currentTimeMillis() + "}\n").getBytes(), 
                                java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
                        } catch (Exception logEx) {}
                        // #endregion
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
                    if (view != null) {
                        client_parent.setCenter(view);
                    } else {
                        System.err.println("Error: Notifications view is null");
                        // Show error message
                        javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
                            "Error loading Notifications view. Please check the console for details."
                        );
                        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14; -fx-padding: 20;");
                        javafx.scene.layout.AnchorPane errorPane = new javafx.scene.layout.AnchorPane();
                        errorPane.getChildren().add(errorLabel);
                        client_parent.setCenter(errorPane);
                    }
                }
                case BANK_STATEMENT -> {
                    var view = Model.getInstance().getViewFactory().getBankStatementView();
                    if (view != null) {
                        client_parent.setCenter(view);
                    } else {
                        System.err.println("Error: Bank Statement view is null");
                        // Show error message
                        javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
                            "Error loading Bank Statement view. Please check the console for details."
                        );
                        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14; -fx-padding: 20;");
                        javafx.scene.layout.AnchorPane errorPane = new javafx.scene.layout.AnchorPane();
                        errorPane.getChildren().add(errorLabel);
                        client_parent.setCenter(errorPane);
                    }
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
