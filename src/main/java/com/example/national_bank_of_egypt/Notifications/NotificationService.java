package com.example.national_bank_of_egypt.Notifications;

import com.example.national_bank_of_egypt.Models.DataBaseDriver;
import com.example.national_bank_of_egypt.Models.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class NotificationService extends NotificationSubject {
    private static NotificationService instance;
    private final ObservableList<Notification> notifications;
    private final DataBaseDriver dataBaseDriver;

    private NotificationService() {
        this.notifications = FXCollections.observableArrayList();
        this.dataBaseDriver = Model.getInstance().getDataBaseDriver();
    }

    public static synchronized NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    public void sendNotification(String userId, String title, String message, String type) {
        String notificationId = UUID.randomUUID().toString();
        LocalDateTime timestamp = LocalDateTime.now();
        Notification notification = new Notification(
            notificationId, userId, title, message, type, timestamp
        );
        notifications.add(notification);
        
        // Persist to database
        String timestampStr = timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        dataBaseDriver.createNotification(notificationId, userId, title, message, type, timestampStr);
        
        notifyObservers(notification);
    }

    public void loadUserNotifications(String userId) {
        // Clear existing notifications for this user from memory
        notifications.removeIf(n -> n.getUserId().equals(userId));
        
        // Load from database
        ResultSet rs = dataBaseDriver.getNotifications(userId);
        if (rs != null) {
            try {
                while (rs.next()) {
                    String notificationId = rs.getString("NotificationID");
                    String title = rs.getString("Title");
                    String message = rs.getString("Message");
                    String type = rs.getString("Type");
                    String timestampStr = rs.getString("Timestamp");
                    String isReadStr = rs.getString("IsRead");
                    
                    // Parse timestamp
                    LocalDateTime timestamp;
                    try {
                        timestamp = LocalDateTime.parse(timestampStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    } catch (Exception e) {
                        // Try alternative format
                        try {
                            timestamp = LocalDateTime.parse(timestampStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                        } catch (Exception e2) {
                            timestamp = LocalDateTime.now();
                        }
                    }
                    
                    Notification notification = new Notification(
                        notificationId, userId, title, message, type, timestamp
                    );
                    
                    // Set read status
                    if ("true".equalsIgnoreCase(isReadStr)) {
                        notification.markAsRead();
                    }
                    
                    notifications.add(notification);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ObservableList<Notification> getUserNotifications(String userId) {
        // Ensure notifications are loaded for this user
        if (notifications.stream().noneMatch(n -> n.getUserId().equals(userId))) {
            loadUserNotifications(userId);
        }
        return notifications.filtered(n -> n.getUserId().equals(userId));
    }

    public void markAsRead(String notificationId) {
        notifications.stream()
            .filter(n -> n.getNotificationId().equals(notificationId))
            .findFirst()
            .ifPresent(notification -> {
                notification.markAsRead();
                // Persist to database
                dataBaseDriver.markNotificationAsRead(notificationId);
            });
    }
}

