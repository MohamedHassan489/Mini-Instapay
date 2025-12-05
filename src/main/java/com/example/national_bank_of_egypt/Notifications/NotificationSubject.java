package com.example.national_bank_of_egypt.Notifications;

import java.util.ArrayList;
import java.util.List;

public class NotificationSubject {
    private final List<NotificationObserver> observers;

    public NotificationSubject() {
        this.observers = new ArrayList<>();
    }

    public void addObserver(NotificationObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(NotificationObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Notification notification) {
        for (NotificationObserver observer : observers) {
            observer.update(notification);
        }
    }
}

