package com.example.national_bank_of_egypt.Notifications;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationService extends NotificationSubject {
    private static NotificationService instance;
    private final ObservableList<Notification> notifications;

    private NotificationService() {
        this.notifications = FXCollections.observableArrayList();
    }

    public static synchronized NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    public void sendNotification(String userId, String title, String message, String type) {
        String notificationId = UUID.randomUUID().toString();
        Notification notification = new Notification(
            notificationId, userId, title, message, type, LocalDateTime.now()
        );
        notifications.add(notification);
        notifyObservers(notification);
    }

    public ObservableList<Notification> getUserNotifications(String userId) {
        return notifications.filtered(n -> n.getUserId().equals(userId));
    }

    public void markAsRead(String notificationId) {
        notifications.stream()
            .filter(n -> n.getNotificationId().equals(notificationId))
            .findFirst()
            .ifPresent(Notification::markAsRead);
    }
}

