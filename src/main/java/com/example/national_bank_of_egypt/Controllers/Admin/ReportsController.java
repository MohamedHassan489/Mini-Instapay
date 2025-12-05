package com.example.national_bank_of_egypt.Controllers.Admin;

import com.example.national_bank_of_egypt.Reports.ReportService;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {
    public ComboBox<String> reportType_combo;
    public Button generateReport_btn;
    public TextArea reportContent_area;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reportType_combo.setItems(javafx.collections.FXCollections.observableArrayList("Monthly Report", "Annual Report", "Account Usage Analysis"));
        
        if (generateReport_btn != null) {
            generateReport_btn.setOnAction(event -> onGenerateReport());
        }
    }

    private void onGenerateReport() {
        String reportType = reportType_combo.getValue();
        ReportService reportService = ReportService.getInstance();
        
        if (reportType == null) {
            reportContent_area.setText("Please select a report type");
            return;
        }
        
        if (reportType.equals("Monthly Report")) {
            LocalDate now = LocalDate.now();
            var summary = reportService.generateMonthlyReport(now.getYear(), now.getMonthValue());
            reportContent_area.setText(String.format("Monthly Report (%s to %s)\n\nTotal Transactions: %d\nTotal Amount: $%.2f\nAverage Amount: $%.2f",
                summary.getStartDate(), summary.getEndDate(), summary.getTotalTransactions(), summary.getTotalAmount(), summary.getAverageAmount()));
        } else if (reportType.equals("Annual Report")) {
            LocalDate now = LocalDate.now();
            var summary = reportService.generateAnnualReport(now.getYear());
            reportContent_area.setText(String.format("Annual Report (%s to %s)\n\nTotal Transactions: %d\nTotal Amount: $%.2f\nAverage Amount: $%.2f",
                summary.getStartDate(), summary.getEndDate(), summary.getTotalTransactions(), summary.getTotalAmount(), summary.getAverageAmount()));
        } else if (reportType.equals("Account Usage Analysis")) {
            var analysis = reportService.generateAccountUsageAnalysis();
            reportContent_area.setText(String.format("Account Usage Analysis\n\nTotal Users: %d\nActive Users: %d\nEngagement Rate: %.2f%%",
                analysis.getTotalUsers(), analysis.getActiveUsers(), analysis.getEngagementRate()));
        }
    }
}

