package com.example.national_bank_of_egypt.Notifications;

public class NotificationFactory {
    public static Notification createTransactionNotification(String userId, String message, String status) {
        String title = status.equals("SUCCESS") ? "Transaction Successful" : "Transaction Failed";
        String type = "TRANSACTION";
        return new Notification(
            java.util.UUID.randomUUID().toString(),
            userId,
            title,
            message,
            type,
            java.time.LocalDateTime.now()
        );
    }

    public static Notification createLimitNotification(String userId, String message) {
        return new Notification(
            java.util.UUID.randomUUID().toString(),
            userId,
            "Transaction Limit Alert",
            message,
            "LIMIT",
            java.time.LocalDateTime.now()
        );
    }

    public static Notification createAccountNotification(String userId, String message) {
        return new Notification(
            java.util.UUID.randomUUID().toString(),
            userId,
            "Account Update",
            message,
            "ACCOUNT",
            java.time.LocalDateTime.now()
        );
    }

    public static Notification createDisputeNotification(String userId, String message) {
        return new Notification(
            java.util.UUID.randomUUID().toString(),
            userId,
            "Dispute Update",
            message,
            "DISPUTE",
            java.time.LocalDateTime.now()
        );
    }
}

