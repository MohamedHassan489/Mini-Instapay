package com.example.national_bank_of_egypt.Views;

import com.example.national_bank_of_egypt.Controllers.Client.NotificationCellController;
import com.example.national_bank_of_egypt.Notifications.Notification;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;

import java.io.IOException;

public class NotificationCellFactory extends ListCell<Notification> {
    @Override
    protected void updateItem(Notification notification, boolean empty) {
        super.updateItem(notification, empty);
        if (empty || notification == null) {
            setText(null);
            setGraphic(null);
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Client/NotificationCell.fxml"));
            try {
                NotificationCellController controller = new NotificationCellController(notification);
                loader.setController(controller);
                setText(null);
                setGraphic(loader.load());
            } catch (IOException e) {
                e.printStackTrace();
                // Fallback to simple text display
                setGraphic(null);
                setText(notification.getTitle() + ": " + notification.getMessage());
            }
        }
    }
}

