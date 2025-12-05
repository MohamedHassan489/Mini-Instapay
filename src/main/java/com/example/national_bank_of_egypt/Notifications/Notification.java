package com.example.national_bank_of_egypt.Notifications;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Notification {
    private final StringProperty notificationId;
    private final StringProperty userId;
    private final StringProperty title;
    private final StringProperty message;
    private final StringProperty type;
    private final ObjectProperty<LocalDateTime> timestamp;
    private final BooleanProperty isRead;

    public Notification(String notificationId, String userId, String title, 
                       String message, String type, LocalDateTime timestamp) {
        this.notificationId = new SimpleStringProperty(this, "notificationId", notificationId);
        this.userId = new SimpleStringProperty(this, "userId", userId);
        this.title = new SimpleStringProperty(this, "title", title);
        this.message = new SimpleStringProperty(this, "message", message);
        this.type = new SimpleStringProperty(this, "type", type);
        this.timestamp = new SimpleObjectProperty<>(this, "timestamp", timestamp);
        this.isRead = new SimpleBooleanProperty(this, "isRead", false);
    }

    public StringProperty notificationIdProperty() {
        return notificationId;
    }

    public StringProperty userIdProperty() {
        return userId;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty messageProperty() {
        return message;
    }

    public StringProperty typeProperty() {
        return type;
    }

    public ObjectProperty<LocalDateTime> timestampProperty() {
        return timestamp;
    }

    public BooleanProperty isReadProperty() {
        return isRead;
    }

    public String getNotificationId() {
        return notificationId.get();
    }

    public String getUserId() {
        return userId.get();
    }

    public void markAsRead() {
        isRead.set(true);
    }
}

