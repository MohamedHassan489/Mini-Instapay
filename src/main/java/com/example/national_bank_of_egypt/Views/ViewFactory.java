package com.example.national_bank_of_egypt.Views;

import com.example.national_bank_of_egypt.Controllers.Admin.AdminController;
import com.example.national_bank_of_egypt.Controllers.Client.ClientController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ViewFactory {

    private AccountType loginAccountType;

    private final ObjectProperty<clientmenuoption> clientSelectedMenuItem;
    private final ObjectProperty<AdminMenuOption> adminselectedmenuitem;
    private AnchorPane dashboardview;
    private AnchorPane accountsView;
    private AnchorPane transactionsView;
    private AnchorPane sendMoneyView;
    private AnchorPane profileView;
    private AnchorPane disputesView;
    private AnchorPane notificationsView;
    private AnchorPane usersView;
    private AnchorPane adminTransactionsView;
    private AnchorPane adminDisputesView;
    private AnchorPane reportsView;
    private AnchorPane systemHealthView;


    public ViewFactory(){
        this.loginAccountType =  AccountType.CLIENT;
        this.clientSelectedMenuItem =  new SimpleObjectProperty<>();
        this.adminselectedmenuitem =  new SimpleObjectProperty<>();
    }

    public void setLoginAccountType(AccountType loginAccountType) {
        this.loginAccountType = loginAccountType;
    }

    public AccountType getLoginAccountType() {
        return loginAccountType;
    }

    public ObjectProperty<clientmenuoption> getClientSelectedMenuItem() {

        return clientSelectedMenuItem;
    }


    public AnchorPane getDashboardview(){
        if (dashboardview == null){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Client/Dashboard.fxml"));
                dashboardview = loader.load();
            }catch (Exception e){
                e.printStackTrace();
                // Return empty AnchorPane if loading fails
                dashboardview = new AnchorPane();
            }
        }
        return dashboardview;
    }

    public AnchorPane getTransactionsView() {
        // Always reload to ensure fresh state and proper initialization
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Client/Transactions.fxml"));
            AnchorPane view = loader.load();
            transactionsView = view;
            System.out.println("Transactions view loaded successfully");
            return view;
        }catch (Exception e){
            e.printStackTrace();
            System.err.println("Error loading Transactions view: " + e.getMessage());
            System.err.println("Exception type: " + e.getClass().getName());
            if (e.getCause() != null) {
                System.err.println("Caused by: " + e.getCause().getMessage());
            }
            // Return empty AnchorPane with error message if loading fails
            AnchorPane errorPane = new AnchorPane();
            javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
                "Error loading Transactions view.\n" + 
                "Error: " + e.getMessage() + "\n" +
                "Please check the console for details and restart the application."
            );
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11; -fx-padding: 20; -fx-wrap-text: true;");
            errorLabel.setWrapText(true);
            errorPane.getChildren().add(errorLabel);
            transactionsView = errorPane;
            return errorPane;
        }
    }

    public AnchorPane getAccountsView() {
        // Always reload to ensure fresh state and proper initialization
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Client/Accounts.fxml"));
            AnchorPane view = loader.load();
            accountsView = view;
            System.out.println("Accounts view loaded successfully");
            return view;
        }catch (Exception e){
            e.printStackTrace();
            System.err.println("Error loading Accounts view: " + e.getMessage());
            System.err.println("Exception type: " + e.getClass().getName());
            if (e.getCause() != null) {
                System.err.println("Caused by: " + e.getCause().getMessage());
            }
            // Return empty AnchorPane with error message if loading fails
            AnchorPane errorPane = new AnchorPane();
            javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
                "Error loading Accounts view.\n" + 
                "Error: " + e.getMessage() + "\n" +
                "Please check the console for details and restart the application."
            );
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11; -fx-padding: 20; -fx-wrap-text: true;");
            errorLabel.setWrapText(true);
            errorPane.getChildren().add(errorLabel);
            accountsView = errorPane;
            return errorPane;
        }
    }
    public AnchorPane getSendMoneyView() {
        // Always reload to ensure fresh state and proper initialization
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Client/SendMoney.fxml"));
            AnchorPane view = loader.load();
            sendMoneyView = view;
            System.out.println("SendMoney view loaded successfully");
            return view;
        }catch (Exception e){
            e.printStackTrace();
            System.err.println("Error loading SendMoney view: " + e.getMessage());
            System.err.println("Exception type: " + e.getClass().getName());
            if (e.getCause() != null) {
                System.err.println("Caused by: " + e.getCause().getMessage());
            }
            // Return empty AnchorPane with error message if loading fails
            AnchorPane errorPane = new AnchorPane();
            javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
                "Error loading Send Money view.\n" + 
                "Error: " + e.getMessage() + "\n" +
                "Please check the console for details and restart the application."
            );
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11; -fx-padding: 20; -fx-wrap-text: true;");
            errorLabel.setWrapText(true);
            errorPane.getChildren().add(errorLabel);
            sendMoneyView = errorPane;
            return errorPane;
        }
    }

    public AnchorPane getProfileView() {
        // Always reload to ensure fresh state and proper initialization
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Client/Profile.fxml"));
            AnchorPane view = loader.load();
            profileView = view;
            System.out.println("Profile view loaded successfully");
            return view;
        }catch (Exception e){
            e.printStackTrace();
            System.err.println("Error loading Profile view: " + e.getMessage());
            System.err.println("Exception type: " + e.getClass().getName());
            if (e.getCause() != null) {
                System.err.println("Caused by: " + e.getCause().getMessage());
            }
            // Return empty AnchorPane with error message if loading fails
            AnchorPane errorPane = new AnchorPane();
            javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
                "Error loading Profile view.\n" + 
                "Error: " + e.getMessage() + "\n" +
                "Please check the console for details and restart the application."
            );
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11; -fx-padding: 20; -fx-wrap-text: true;");
            errorLabel.setWrapText(true);
            errorPane.getChildren().add(errorLabel);
            profileView = errorPane;
            return errorPane;
        }
    }

    public AnchorPane getDisputesView() {
        if (disputesView == null){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Client/Disputes.fxml"));
                disputesView = loader.load();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return disputesView;
    }

    public AnchorPane getNotificationsView() {
        if (notificationsView == null){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Client/Notifications.fxml"));
                notificationsView = loader.load();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return notificationsView;
    }

    public AnchorPane getReportsView() {
        if (reportsView == null){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/Reports.fxml"));
                reportsView = loader.load();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return reportsView;
    }

    public AnchorPane getSystemHealthView() {
        if (systemHealthView == null){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/SystemHealth.fxml"));
                systemHealthView = loader.load();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return systemHealthView;
    }

    public void showLoginWindow(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        CreateStage(loader);
    }

    public void showRegistrationWindow(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Register.fxml"));
        CreateStage(loader);
    }
    public void showClinetWindow(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Client/Client.fxml"));
        ClientController clientController = new ClientController();
        loader.setController(clientController);
        CreateStage(loader);
    }
//    Admin
    public  ObjectProperty<AdminMenuOption>  getAdminselectedmenuitem(){
        return adminselectedmenuitem;
    }
    public void showAdminWindow(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource( "/fxml/Admin/Admin.fxml"));
        AdminController controller = new AdminController();
        loader.setController(controller);
        CreateStage(loader);
    }
    public AnchorPane getUsersView(){
        if (usersView == null){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/Users.fxml"));
                usersView = loader.load();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return usersView;
    }

    public AnchorPane getAdminTransactionsView(){
        if (adminTransactionsView == null){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/AdminTransactions.fxml"));
                adminTransactionsView = loader.load();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return adminTransactionsView;
    }

    public AnchorPane getAdminDisputesView(){
        if (adminDisputesView == null){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/AdminDisputes.fxml"));
                adminDisputesView = loader.load();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return adminDisputesView;
    }
    private void CreateStage(FXMLLoader loader){
        try {
            javafx.scene.Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            try {
                stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/images/icon.png"))));
            } catch (Exception e) {
                // Icon not critical, continue without it
            }
            stage.setResizable(true);
            // Increased width by 50% more: 810x960 (was 540x960, originally 360x640)
            stage.setWidth(810);
            stage.setHeight(960);
            stage.setMinWidth(600);
            stage.setMinHeight(700);
            stage.setMaxWidth(1200);
            stage.setMaxHeight(1200);
            stage.setScene(scene);
            stage.setTitle("Mini-InstaPay");
            stage.show();
        }catch (Exception e){
            e.printStackTrace();
            // Show error dialog instead of blank page
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load window");
            alert.setContentText("An error occurred while loading the window: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void closeStage(Stage stage){
        stage.close();
    }
}
