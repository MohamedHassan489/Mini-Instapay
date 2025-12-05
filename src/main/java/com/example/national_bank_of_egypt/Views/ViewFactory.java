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
                dashboardview = new FXMLLoader(getClass().getResource("/fxml/Client/Dashboard.fxml")).load();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return dashboardview;
    }

    public AnchorPane getTransactionsView() {
        if (transactionsView == null){
            try {
                transactionsView = new FXMLLoader(getClass().getResource("/fxml/Client/Transactions.fxml")).load();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return transactionsView;
    }

    public AnchorPane getAccountsView() {
        if (accountsView == null){
            try {
                accountsView = new FXMLLoader(getClass().getResource("/fxml/Client/Accounts.fxml")).load();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return accountsView;
    }
    public AnchorPane getSendMoneyView() {
        if (sendMoneyView == null){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Client/SendMoney.fxml"));
                sendMoneyView = loader.load();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return sendMoneyView;
    }

    public AnchorPane getProfileView() {
        if (profileView == null){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Client/Profile.fxml"));
                profileView = loader.load();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return profileView;
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
    private void CreateStage(FXMLLoader loader){
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        }catch (Exception e){
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/images/icon.png"))));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Mini-InstaPay");
        stage.show();
    }

    public void closeStage(Stage stage){
        stage.close();
    }
}
