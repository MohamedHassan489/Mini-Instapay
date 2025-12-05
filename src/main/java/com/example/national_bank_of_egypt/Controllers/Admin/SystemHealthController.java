package com.example.national_bank_of_egypt.Controllers.Admin;

import com.example.national_bank_of_egypt.Models.DataBaseDriver;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class SystemHealthController implements Initializable {
    public Label status_lbl;
    public Label successRate_lbl;
    public Label totalTransactions_lbl;
    public Label activeUsers_lbl;
    public Label uptime_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        status_lbl.setText("Online");
        status_lbl.setStyle("-fx-text-fill: green; -fx-font-size: 16;");
        
        try {
            DataBaseDriver db = new DataBaseDriver();
            java.sql.ResultSet stats = db.getTransactionStats(
                java.time.LocalDate.now().minusDays(1).toString(),
                java.time.LocalDate.now().toString()
            );
            
            if (stats != null && stats.next()) {
                int total = stats.getInt("total");
                totalTransactions_lbl.setText(String.valueOf(total));
            }
            
            java.sql.ResultSet userStats = db.getUserActivityStats();
            if (userStats != null && userStats.next()) {
                activeUsers_lbl.setText(String.valueOf(userStats.getInt("activeUsers")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        successRate_lbl.setText("95%");
        uptime_lbl.setText("24 hours");
    }
}

