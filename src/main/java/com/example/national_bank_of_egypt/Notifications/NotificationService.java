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
        
        // Insert notification at the beginning (newest first)
        notifications.add(0, notification);
        
        // Persist to database
        String timestampStr = timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        dataBaseDriver.createNotification(notificationId, userId, title, message, type, timestampStr);
        
        notifyObservers(notification);
    }

    public void loadUserNotifications(String userId) {
        // #region agent log
        try {
            java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"), 
                ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"N2\",\"location\":\"NotificationService.java:54\",\"message\":\"loadUserNotifications entry\",\"timestamp\":" + System.currentTimeMillis() + ",\"data\":{\"userId\":\"" + userId + "\"}}\n").getBytes(), 
                java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception logEx) {}
        // #endregion
        // Clear existing notifications for this user from memory
        notifications.removeIf(n -> n.getUserId().equals(userId));
        
        // Load from database (already ordered by Timestamp DESC - newest first)
        ResultSet rs = dataBaseDriver.getNotifications(userId);
        // #region agent log
        try {
            java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"), 
                ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"N2\",\"location\":\"NotificationService.java:60\",\"message\":\"got ResultSet\",\"timestamp\":" + System.currentTimeMillis() + ",\"data\":{\"resultSetNull\":" + (rs == null) + "}}\n").getBytes(), 
                java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception logEx) {}
        // #endregion
        int notificationCount = 0;
        if (rs != null) {
            try {
                while (rs.next()) {
                    notificationCount++;
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
                    
                    // Insert at the beginning (newest first) - database already returns newest first
                    notifications.add(0, notification);
                }
                // #region agent log
                try {
                    java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"), 
                        ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"N2\",\"location\":\"NotificationService.java:102\",\"message\":\"loaded notifications from DB\",\"timestamp\":" + System.currentTimeMillis() + ",\"data\":{\"notificationCount\":" + notificationCount + ",\"totalNotificationsSize\":" + notifications.size() + "}}\n").getBytes(), 
                        java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
                } catch (Exception logEx) {}
                // #endregion
            } catch (Exception e) {
                // #region agent log
                try {
                    java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"), 
                        ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"N2\",\"location\":\"NotificationService.java:104\",\"message\":\"loadUserNotifications exception\",\"timestamp\":" + System.currentTimeMillis() + ",\"data\":{\"error\":\"" + e.getMessage() + "\"}}\n").getBytes(), 
                        java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
                } catch (Exception logEx) {}
                // #endregion
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (Exception closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    public ObservableList<Notification> getUserNotifications(String userId) {
        // #region agent log
        try {
            java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"), 
                ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"N3\",\"location\":\"NotificationService.java:139\",\"message\":\"getUserNotifications entry\",\"timestamp\":" + System.currentTimeMillis() + ",\"data\":{\"userId\":\"" + userId + "\",\"notificationsSize\":" + notifications.size() + "}}\n").getBytes(), 
                java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception logEx) {}
        // #endregion
        try {
            if (userId == null || userId.isEmpty()) {
                // #region agent log
                try {
                    java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"), 
                        ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"N3\",\"location\":\"NotificationService.java:148\",\"message\":\"userId is null or empty\",\"timestamp\":" + System.currentTimeMillis() + "}\n").getBytes(), 
                        java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
                } catch (Exception logEx) {}
                // #endregion
                return FXCollections.observableArrayList();
            }
            
            // Always reload from database to ensure we have the latest notifications
            loadUserNotifications(userId);
            
            // Create a new ObservableList with filtered notifications
            ObservableList<Notification> userNotifications = FXCollections.observableArrayList();
            for (Notification notification : notifications) {
                if (notification != null && notification.getUserId() != null && notification.getUserId().equals(userId)) {
                    userNotifications.add(notification);
                }
            }
            
            // #region agent log
            try {
                java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"), 
                    ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"N3\",\"location\":\"NotificationService.java:165\",\"message\":\"filtered notifications\",\"timestamp\":" + System.currentTimeMillis() + ",\"data\":{\"filteredSize\":" + userNotifications.size() + "}}\n").getBytes(), 
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            } catch (Exception logEx) {}
            // #endregion
            
            // Sort by timestamp descending (newest first)
            userNotifications.sort((n1, n2) -> {
                if (n1 == null || n2 == null || n1.getTimestamp() == null || n2.getTimestamp() == null) {
                    return 0;
                }
                return n2.getTimestamp().compareTo(n1.getTimestamp());
            });
            
            // #region agent log
            try {
                java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"), 
                    ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"N3\",\"location\":\"NotificationService.java:175\",\"message\":\"returning userNotifications\",\"timestamp\":" + System.currentTimeMillis() + ",\"data\":{\"finalSize\":" + userNotifications.size() + "}}\n").getBytes(), 
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            } catch (Exception logEx) {}
            // #endregion
            return userNotifications;
        } catch (Exception e) {
            // #region agent log
            try {
                java.nio.file.Files.write(java.nio.file.Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"), 
                    ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"N3\",\"location\":\"NotificationService.java:181\",\"message\":\"getUserNotifications exception\",\"timestamp\":" + System.currentTimeMillis() + ",\"data\":{\"error\":\"" + e.getMessage() + "\",\"stackTrace\":\"" + java.util.Arrays.toString(e.getStackTrace()).replace("\"", "'") + "\"}}\n").getBytes(), 
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            } catch (Exception logEx) {}
            // #endregion
            e.printStackTrace();
            System.err.println("Error in getUserNotifications: " + e.getMessage());
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
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

