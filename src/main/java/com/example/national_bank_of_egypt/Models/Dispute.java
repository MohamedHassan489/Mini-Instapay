package com.example.national_bank_of_egypt.Models;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Dispute {
    private final StringProperty disputeId;
    private final StringProperty transactionId;
    private final StringProperty userId;
    private final StringProperty reason;
    private final StringProperty status;
    private final ObjectProperty<LocalDate> dateCreated;
    private final StringProperty resolution;

    public Dispute(String disputeId, String transactionId, String userId, 
                   String reason, String status, LocalDate dateCreated) {
        this.disputeId = new SimpleStringProperty(this, "disputeId", disputeId);
        this.transactionId = new SimpleStringProperty(this, "transactionId", transactionId);
        this.userId = new SimpleStringProperty(this, "userId", userId);
        this.reason = new SimpleStringProperty(this, "reason", reason);
        this.status = new SimpleStringProperty(this, "status", status);
        this.dateCreated = new SimpleObjectProperty<>(this, "dateCreated", dateCreated);
        this.resolution = new SimpleStringProperty(this, "resolution", "");
    }

    public StringProperty disputeIdProperty() {
        return disputeId;
    }

    public StringProperty transactionIdProperty() {
        return transactionId;
    }

    public StringProperty userIdProperty() {
        return userId;
    }

    public StringProperty reasonProperty() {
        return reason;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public ObjectProperty<LocalDate> dateCreatedProperty() {
        return dateCreated;
    }

    public StringProperty resolutionProperty() {
        return resolution;
    }

    public String getDisputeId() {
        return disputeId.get();
    }

    public String getTransactionId() {
        return transactionId.get();
    }

    public String getUserId() {
        return userId.get();
    }

    public String getReason() {
        return reason.get();
    }

    public String getStatus() {
        return status.get();
    }

    public LocalDate getDateCreated() {
        return dateCreated.get();
    }

    public String getResolution() {
        return resolution.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public void setResolution(String resolution) {
        this.resolution.set(resolution);
    }
}

