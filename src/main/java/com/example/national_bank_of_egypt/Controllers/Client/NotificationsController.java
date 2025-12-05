package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Notifications.Notification;
import com.example.national_bank_of_egypt.Notifications.NotificationService;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class NotificationsController implements Initializable {
    public ListView<Notification> notifications_list;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (Model.getInstance().getCurrentUser() != null) {
            notifications_list.setItems(NotificationService.getInstance().getUserNotifications(Model.getInstance().getCurrentUser().getUserName()));
        }
    }
}

