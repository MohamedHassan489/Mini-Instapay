package com.example.national_bank_of_egypt.Controllers.Admin;

import com.example.national_bank_of_egypt.Models.DataBaseDriver;
import com.example.national_bank_of_egypt.Models.Model;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class SystemHealthController implements Initializable {
    public Label status_lbl;
    public Label successRate_lbl;
    public Label totalTransactions_lbl;
    public Label activeUsers_lbl;
    public Label uptime_lbl;
    public Label totalUsers_lbl;
    public Label pendingDisputes_lbl;
    public Label suspiciousTransactions_lbl;
    
    private DataBaseDriver db;
    private Timeline updateTimeline;
    private long startTime;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startTime = System.currentTimeMillis();
        db = new DataBaseDriver();
        
        // Initial status
        updateSystemHealth();
        
        // Set up real-time updates every 5 seconds
        updateTimeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> updateSystemHealth()));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();
    }
    
    private void updateSystemHealth() {
        try {
            // System Status
            status_lbl.setText("Online");
            status_lbl.setStyle("-fx-text-fill: green; -fx-font-size: 16; -fx-font-weight: bold;");
            
            // Calculate uptime
            long uptimeMillis = System.currentTimeMillis() - startTime;
            long uptimeHours = uptimeMillis / (1000 * 60 * 60);
            long uptimeMinutes = (uptimeMillis % (1000 * 60 * 60)) / (1000 * 60);
            uptime_lbl.setText(uptimeHours + "h " + uptimeMinutes + "m");
            
            // Transaction Stats (last 24 hours)
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);
            java.sql.ResultSet stats = db.getTransactionStats(yesterday.toString(), today.toString());
            
            int totalTransactions = 0;
            int successfulTransactions = 0;
            
            if (stats != null && stats.next()) {
                totalTransactions = stats.getInt("total");
                // totalAmount retrieved but not currently displayed in UI
                // stats.getDouble("totalAmount");
            }
            
            // Get all transactions to calculate success rate
            Model.getInstance().loadAllTransactions();
            int allTransactions = Model.getInstance().getTransactions().size();
            successfulTransactions = (int) Model.getInstance().getTransactions().stream()
                .filter(t -> "SUCCESS".equalsIgnoreCase(t.getStatus()))
                .count();
            
            totalTransactions_lbl.setText(String.valueOf(totalTransactions));
            
            // Calculate success rate
            double successRate = allTransactions > 0 ? (successfulTransactions * 100.0 / allTransactions) : 0.0;
            successRate_lbl.setText(String.format("%.1f%%", successRate));
            if (successRate >= 95) {
                successRate_lbl.setStyle("-fx-text-fill: green;");
            } else if (successRate >= 80) {
                successRate_lbl.setStyle("-fx-text-fill: orange;");
            } else {
                successRate_lbl.setStyle("-fx-text-fill: red;");
            }
            
            // User Stats - Use existing users list, only load if empty
            int totalUsers = Model.getInstance().getUsers().size();
            if (totalUsers == 0) {
                // Only load if list is empty to avoid conflicts
                Model.getInstance().loadAllUsers();
                totalUsers = Model.getInstance().getUsers().size();
            }
            totalUsers_lbl.setText(String.valueOf(totalUsers));
            
            // Active Users (users with transactions in last 7 days)
            int activeUsers = (int) Model.getInstance().getTransactions().stream()
                .filter(t -> t.getDate().isAfter(LocalDate.now().minusDays(7)))
                .map(t -> t.getSender())
                .distinct()
                .count();
            activeUsers_lbl.setText(String.valueOf(activeUsers));
            
            // Pending Disputes
            Model.getInstance().loadAllDisputes();
            int pendingDisputes = (int) Model.getInstance().getAllDisputes().stream()
                .filter(d -> "PENDING".equalsIgnoreCase(d.getStatus()))
                .count();
            pendingDisputes_lbl.setText(String.valueOf(pendingDisputes));
            if (pendingDisputes > 10) {
                pendingDisputes_lbl.setStyle("-fx-text-fill: red;");
            } else if (pendingDisputes > 5) {
                pendingDisputes_lbl.setStyle("-fx-text-fill: orange;");
            } else {
                pendingDisputes_lbl.setStyle("-fx-text-fill: green;");
            }
            
            // Suspicious Transactions
            int suspiciousTransactions = (int) Model.getInstance().getTransactions().stream()
                .filter(t -> "SUSPICIOUS".equalsIgnoreCase(t.getStatus()))
                .count();
            suspiciousTransactions_lbl.setText(String.valueOf(suspiciousTransactions));
            if (suspiciousTransactions > 0) {
                suspiciousTransactions_lbl.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            } else {
                suspiciousTransactions_lbl.setStyle("-fx-text-fill: green;");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            status_lbl.setText("Error");
            status_lbl.setStyle("-fx-text-fill: red; -fx-font-size: 16;");
        }
    }
    
    public void stopUpdates() {
        if (updateTimeline != null) {
            updateTimeline.stop();
        }
    }
}

