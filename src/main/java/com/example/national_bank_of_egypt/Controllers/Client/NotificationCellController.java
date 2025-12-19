package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Notifications.Notification;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class NotificationCellController implements Initializable {
    public Label title_lbl;
    public Label message_lbl;
    public Label timestamp_lbl;
    public Label type_lbl;
    
    private Notification notification;

    public NotificationCellController(Notification notification) {
        this.notification = notification;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (notification != null) {
            if (title_lbl != null) {
                title_lbl.setText(notification.getTitle());
            }
            if (message_lbl != null) {
                message_lbl.setText(notification.getMessage());
            }
            if (timestamp_lbl != null && notification.getTimestamp() != null) {
                timestamp_lbl.setText(notification.getTimestamp()
                    .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
            }
            if (type_lbl != null) {
                type_lbl.setText(notification.getType());
                
                // Style based on notification type
                String type = notification.getType();
                if ("TRANSACTION".equals(type)) {
                    type_lbl.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
                } else if ("LIMIT".equals(type)) {
                    type_lbl.setStyle("-fx-text-fill: #f57c00; -fx-font-weight: bold;");
                } else if ("FRAUD".equals(type)) {
                    type_lbl.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
                } else if ("ACCOUNT".equals(type)) {
                    type_lbl.setStyle("-fx-text-fill: #1976d2; -fx-font-weight: bold;");
                } else if ("DISPUTE".equals(type)) {
                    type_lbl.setStyle("-fx-text-fill: #7b1fa2; -fx-font-weight: bold;");
                }
            }
            
            // Style unread notifications
            if (!notification.isRead()) {
                if (title_lbl != null) {
                    title_lbl.setStyle("-fx-font-weight: bold;");
                }
            }
        }
    }
}

