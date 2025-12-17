package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Notifications.Notification;
import com.example.national_bank_of_egypt.Notifications.NotificationService;
import com.example.national_bank_of_egypt.Views.NotificationCellFactory;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class NotificationsController implements Initializable {
    public ListView<Notification> notifications_list;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set custom cell factory to display notifications properly
        notifications_list.setCellFactory(e -> new NotificationCellFactory());
        
        // Add click handler to mark notifications as read
        notifications_list.setOnMouseClicked(event -> {
            Notification selected = notifications_list.getSelectionModel().getSelectedItem();
            if (selected != null && !selected.isRead()) {
                NotificationService.getInstance().markAsRead(selected.getNotificationId());
                // Refresh to update display
                refreshNotifications();
            }
        });
        
        refreshNotifications();
    }

    public void refreshNotifications() {
        if (Model.getInstance().getCurrentUser() != null) {
            String userName = Model.getInstance().getCurrentUser().getUserName();
            // Reload notifications from database to get latest
            NotificationService.getInstance().loadUserNotifications(userName);
            notifications_list.setItems(NotificationService.getInstance().getUserNotifications(userName));
        }
    }
}

