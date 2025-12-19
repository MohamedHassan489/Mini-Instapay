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
        try {
            // Ensure notifications_list is not null
            if (notifications_list == null) {
                System.err.println("Error: notifications_list is null in NotificationsController");
                return;
            }
            
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
            
            // Delay refresh to ensure user is loaded
            javafx.application.Platform.runLater(() -> {
                refreshNotifications();
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error initializing NotificationsController: " + e.getMessage());
        }
    }

    public void refreshNotifications() {
        // #region agent log
        try {
            java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"), 
                ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"N1\",\"location\":\"NotificationsController.java:48\",\"message\":\"refreshNotifications entry\",\"timestamp\":" + System.currentTimeMillis() + ",\"data\":{\"notifications_list_null\":" + (notifications_list == null) + "}}\n").getBytes(), 
                java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception logEx) {}
        // #endregion
        try {
            if (notifications_list == null) {
                // #region agent log
                try {
                    java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"), 
                        ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"N1\",\"location\":\"NotificationsController.java:51\",\"message\":\"notifications_list is null\",\"timestamp\":" + System.currentTimeMillis() + "}\n").getBytes(), 
                        java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
                } catch (Exception logEx) {}
                // #endregion
                System.err.println("Error: notifications_list is null in refreshNotifications");
                return;
            }
            
            if (Model.getInstance().getCurrentUser() == null) {
                // #region agent log
                try {
                    java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"), 
                        ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"N1\",\"location\":\"NotificationsController.java:56\",\"message\":\"currentUser is null\",\"timestamp\":" + System.currentTimeMillis() + "}\n").getBytes(), 
                        java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
                } catch (Exception logEx) {}
                // #endregion
                System.err.println("Warning: Current user is null, cannot load notifications");
                notifications_list.setItems(javafx.collections.FXCollections.observableArrayList());
                return;
            }
            
            String userName = Model.getInstance().getCurrentUser().getUserName();
            // #region agent log
            try {
                java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"), 
                    ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"N1\",\"location\":\"NotificationsController.java:62\",\"message\":\"before loadUserNotifications\",\"timestamp\":" + System.currentTimeMillis() + ",\"data\":{\"userName\":\"" + userName + "\"}}\n").getBytes(), 
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            } catch (Exception logEx) {}
            // #endregion
            if (userName == null || userName.isEmpty()) {
                // #region agent log
                try {
                    java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"), 
                        ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"N1\",\"location\":\"NotificationsController.java:64\",\"message\":\"userName is null or empty\",\"timestamp\":" + System.currentTimeMillis() + "}\n").getBytes(), 
                        java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
                } catch (Exception logEx) {}
                // #endregion
                System.err.println("Warning: User name is null or empty");
                notifications_list.setItems(javafx.collections.FXCollections.observableArrayList());
                return;
            }
            
            // Reload notifications from database to get latest
            NotificationService.getInstance().loadUserNotifications(userName);
            // #region agent log
            try {
                java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"), 
                    ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"N1\",\"location\":\"NotificationsController.java:70\",\"message\":\"after loadUserNotifications\",\"timestamp\":" + System.currentTimeMillis() + "}\n").getBytes(), 
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            } catch (Exception logEx) {}
            // #endregion
            javafx.collections.ObservableList<Notification> userNotifications = 
                NotificationService.getInstance().getUserNotifications(userName);
            
            // #region agent log
            try {
                java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"), 
                    ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"N1\",\"location\":\"NotificationsController.java:73\",\"message\":\"got userNotifications\",\"timestamp\":" + System.currentTimeMillis() + ",\"data\":{\"userNotifications_null\":" + (userNotifications == null) + ",\"userNotifications_size\":" + (userNotifications != null ? userNotifications.size() : 0) + "}}\n").getBytes(), 
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            } catch (Exception logEx) {}
            // #endregion
            if (userNotifications != null) {
                notifications_list.setItems(userNotifications);
                // #region agent log
                try {
                    java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"), 
                        ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"N1\",\"location\":\"NotificationsController.java:76\",\"message\":\"setItems called\",\"timestamp\":" + System.currentTimeMillis() + ",\"data\":{\"itemsCount\":" + userNotifications.size() + "}}\n").getBytes(), 
                        java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
                } catch (Exception logEx) {}
                // #endregion
            } else {
                // #region agent log
                try {
                    java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"), 
                        ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"N1\",\"location\":\"NotificationsController.java:79\",\"message\":\"userNotifications is null, setting empty list\",\"timestamp\":" + System.currentTimeMillis() + "}\n").getBytes(), 
                        java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
                } catch (Exception logEx) {}
                // #endregion
                notifications_list.setItems(javafx.collections.FXCollections.observableArrayList());
            }
        } catch (Exception e) {
            // #region agent log
            try {
                java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"), 
                    ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"N1\",\"location\":\"NotificationsController.java:82\",\"message\":\"refreshNotifications exception\",\"timestamp\":" + System.currentTimeMillis() + ",\"data\":{\"error\":\"" + e.getMessage() + "\"}}\n").getBytes(), 
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            } catch (Exception logEx) {}
            // #endregion
            e.printStackTrace();
            System.err.println("Error refreshing notifications: " + e.getMessage());
            if (notifications_list != null) {
                notifications_list.setItems(javafx.collections.FXCollections.observableArrayList());
            }
        }
    }
}

