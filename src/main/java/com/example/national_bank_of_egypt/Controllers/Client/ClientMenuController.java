package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Utils.AnimationUtils;
import com.example.national_bank_of_egypt.Views.clientmenuoption;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
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
    
    @FXML
    private VBox menu_container;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addlisteners();
        
        // Add animations on load
        Platform.runLater(() -> {
            animateMenuItems();
            setupMenuButtonAnimations();
        });
    }
    
    /**
     * Animate menu items with staggered fade-in
     */
    private void animateMenuItems() {
        if (menu_container != null) {
            Button[] menuButtons = {
                dashboard_btn, transaction_btn, accounts_btn, send_money_btn,
                disputes_btn, notifications_btn, profile_btn, logout_btn
            };
            
            // Staggered fade-in animation
            for (int i = 0; i < menuButtons.length; i++) {
                if (menuButtons[i] != null) {
                    menuButtons[i].setOpacity(0);
                    menuButtons[i].setTranslateX(-10);
                    
                    javafx.animation.FadeTransition fade = AnimationUtils.fadeIn(
                        menuButtons[i], 
                        AnimationUtils.STANDARD_DURATION
                    );
                    javafx.animation.TranslateTransition slide = AnimationUtils.slideInFromLeft(
                        menuButtons[i], 
                        10, 
                        AnimationUtils.STANDARD_DURATION
                    );
                    
                    fade.setDelay(javafx.util.Duration.millis(i * 50));
                    slide.setDelay(javafx.util.Duration.millis(i * 50));
                    
                    fade.play();
                    slide.play();
                }
            }
        }
    }
    
    /**
     * Setup hover animations for menu buttons
     */
    private void setupMenuButtonAnimations() {
        Button[] menuButtons = {
            dashboard_btn, transaction_btn, accounts_btn, send_money_btn,
            disputes_btn, notifications_btn, profile_btn, logout_btn
        };
        
        for (Button btn : menuButtons) {
            if (btn != null) {
                btn.hoverProperty().addListener((obs, wasHovered, isHovered) -> {
                    if (isHovered) {
                        AnimationUtils.scaleHover(btn, 1.05, AnimationUtils.QUICK_DURATION).play();
                    }
                });
            }
        }
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
