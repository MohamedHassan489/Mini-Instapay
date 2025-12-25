package com.example.national_bank_of_egypt.Controllers.Admin;

import com.example.national_bank_of_egypt.Reports.ReportService;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {
    public ComboBox<String> reportType_combo;
    public Button generateReport_btn;
    public TextArea reportContent_area;
    public DatePicker monthDatePicker;
    public DatePicker yearDatePicker;
    public Label monthLabel;
    public Label yearLabel;
    public Button detailedReport_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reportType_combo.setItems(javafx.collections.FXCollections.observableArrayList(
            "Monthly Report", 
            "Annual Report", 
            "Account Usage Analysis",
            "Detailed Account Usage"
        ));
        
        // Set default dates
        LocalDate now = LocalDate.now();
        monthDatePicker.setValue(now);
        yearDatePicker.setValue(now);
        
        // Initially hide date pickers
        monthDatePicker.setVisible(false);
        monthLabel.setVisible(false);
        yearDatePicker.setVisible(false);
        yearLabel.setVisible(false);
        detailedReport_btn.setVisible(false);
        
        // Show/hide date pickers based on report type
        reportType_combo.setOnAction(event -> {
            String selected = reportType_combo.getValue();
            if (selected != null) {
                if (selected.equals("Monthly Report")) {
                    monthDatePicker.setVisible(true);
                    monthLabel.setVisible(true);
                    yearDatePicker.setVisible(false);
                    yearLabel.setVisible(false);
                    detailedReport_btn.setVisible(false);
                } else if (selected.equals("Annual Report")) {
                    monthDatePicker.setVisible(false);
                    monthLabel.setVisible(false);
                    yearDatePicker.setVisible(true);
                    yearLabel.setVisible(true);
                    detailedReport_btn.setVisible(false);
                } else {
                    monthDatePicker.setVisible(false);
                    monthLabel.setVisible(false);
                    yearDatePicker.setVisible(false);
                    yearLabel.setVisible(false);
                    if (selected.equals("Detailed Account Usage")) {
                        detailedReport_btn.setVisible(true);
                    } else {
                        detailedReport_btn.setVisible(false);
                    }
                }
            }
        });
        
        if (generateReport_btn != null) {
            generateReport_btn.setOnAction(event -> onGenerateReport());
        }
        
        if (detailedReport_btn != null) {
            detailedReport_btn.setOnAction(event -> onGenerateDetailedReport());
        }
    }

    private void onGenerateReport() {
        String reportType = reportType_combo.getValue();
        ReportService reportService = ReportService.getInstance();
        
        if (reportType == null) {
            reportContent_area.setText("Please select a report type");
            return;
        }
        
        try {
            if (reportType.equals("Monthly Report")) {
                LocalDate selectedDate = monthDatePicker.getValue();
                if (selectedDate == null) {
                    selectedDate = LocalDate.now();
                }
                var summary = reportService.generateMonthlyReport(selectedDate.getYear(), selectedDate.getMonthValue());
                reportContent_area.setText(formatMonthlyReport(summary));
            } else if (reportType.equals("Annual Report")) {
                LocalDate selectedDate = yearDatePicker.getValue();
                if (selectedDate == null) {
                    selectedDate = LocalDate.now();
                }
                var summary = reportService.generateAnnualReport(selectedDate.getYear());
                reportContent_area.setText(formatAnnualReport(summary));
            } else if (reportType.equals("Account Usage Analysis")) {
                var analysis = reportService.generateAccountUsageAnalysis();
                reportContent_area.setText(formatAccountUsageAnalysis(analysis));
            } else if (reportType.equals("Detailed Account Usage")) {
                // Generate summary report for Detailed Account Usage
                var analysis = reportService.generateAccountUsageAnalysis();
                reportContent_area.setText(formatDetailedAccountUsageSummary(analysis));
            }
        } catch (Exception e) {
            reportContent_area.setText("Error generating report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void onGenerateDetailedReport() {
        ReportService reportService = ReportService.getInstance();
        try {
            String detailedReport = reportService.generateDetailedAccountUsageReport();
            reportContent_area.setText(detailedReport);
        } catch (Exception e) {
            reportContent_area.setText("Error generating detailed report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String formatMonthlyReport(com.example.national_bank_of_egypt.Reports.TransactionSummary summary) {
        return String.format(
            "═══════════════════════════════════════════════════════════\n" +
            "           MONTHLY TRANSACTION SUMMARY REPORT\n" +
            "═══════════════════════════════════════════════════════════\n\n" +
            "Period: %s to %s\n\n" +
            "Transaction Statistics:\n" +
            "  • Total Transactions: %d\n" +
            "  • Total Amount: $%.2f\n" +
            "  • Average Amount: $%.2f\n" +
            "  • Daily Average: $%.2f\n\n" +
            "═══════════════════════════════════════════════════════════\n",
            summary.getStartDate().format(DateTimeFormatter.ofPattern("MMMM yyyy")),
            summary.getEndDate().format(DateTimeFormatter.ofPattern("MMMM yyyy")),
            summary.getTotalTransactions(),
            summary.getTotalAmount(),
            summary.getAverageAmount(),
            summary.getTotalAmount() / summary.getEndDate().getDayOfMonth()
        );
    }

    private String formatAnnualReport(com.example.national_bank_of_egypt.Reports.TransactionSummary summary) {
        return String.format(
            "═══════════════════════════════════════════════════════════\n" +
            "           ANNUAL TRANSACTION SUMMARY REPORT\n" +
            "═══════════════════════════════════════════════════════════\n\n" +
            "Year: %d\n" +
            "Period: %s to %s\n\n" +
            "Transaction Statistics:\n" +
            "  • Total Transactions: %d\n" +
            "  • Total Amount: $%.2f\n" +
            "  • Average Amount: $%.2f\n" +
            "  • Monthly Average: $%.2f\n" +
            "  • Daily Average: $%.2f\n\n" +
            "═══════════════════════════════════════════════════════════\n",
            summary.getStartDate().getYear(),
            summary.getStartDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
            summary.getEndDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
            summary.getTotalTransactions(),
            summary.getTotalAmount(),
            summary.getAverageAmount(),
            summary.getTotalAmount() / 12.0,
            summary.getTotalAmount() / 365.0
        );
    }

    private String formatAccountUsageAnalysis(com.example.national_bank_of_egypt.Reports.AccountUsageAnalysis analysis) {
        return String.format(
            "═══════════════════════════════════════════════════════════\n" +
            "           ACCOUNT USAGE ANALYSIS REPORT\n" +
            "═══════════════════════════════════════════════════════════\n\n" +
            "User Statistics:\n" +
            "  • Total Users: %d\n" +
            "  • Active Users (Last 30 Days): %d\n" +
            "  • Engagement Rate: %.2f%%\n\n" +
            "Transaction Statistics:\n" +
            "  • Total Transactions: %d\n" +
            "  • Recent Transactions (Last 30 Days): %d\n" +
            "  • Average Transaction Amount: $%.2f\n" +
            "  • Transactions per User: %.2f\n\n" +
            "Insights:\n" +
            "  • %.1f%% of users are actively using the platform\n" +
            "  • Average user performs %.2f transactions\n" +
            "  • Recent activity shows %d transactions in the last month\n\n" +
            "═══════════════════════════════════════════════════════════\n",
            analysis.getTotalUsers(),
            analysis.getActiveUsers(),
            analysis.getEngagementRate(),
            analysis.getTotalTransactions(),
            analysis.getRecentTransactions(),
            analysis.getAvgTransactionAmount(),
            analysis.getTransactionsPerUser(),
            analysis.getEngagementRate(),
            analysis.getTransactionsPerUser(),
            analysis.getRecentTransactions()
        );
    }
    
    private String formatDetailedAccountUsageSummary(com.example.national_bank_of_egypt.Reports.AccountUsageAnalysis analysis) {
        return String.format(
            "═══════════════════════════════════════════════════════════\n" +
            "         DETAILED ACCOUNT USAGE SUMMARY REPORT\n" +
            "═══════════════════════════════════════════════════════════\n\n" +
            "Generated: %s\n\n" +
            "┌─────────────────────────────────────────────────────────┐\n" +
            "│  USER OVERVIEW                                          │\n" +
            "├─────────────────────────────────────────────────────────┤\n" +
            "│  Total Registered Users:     %d                         \n" +
            "│  Active Users (30 Days):     %d                         \n" +
            "│  Engagement Rate:            %.2f%%                      \n" +
            "└─────────────────────────────────────────────────────────┘\n\n" +
            "┌─────────────────────────────────────────────────────────┐\n" +
            "│  TRANSACTION OVERVIEW                                   │\n" +
            "├─────────────────────────────────────────────────────────┤\n" +
            "│  Total Transactions:         %d                         \n" +
            "│  Recent (Last 30 Days):      %d                         \n" +
            "│  Avg Transaction Amount:     $%.2f                      \n" +
            "│  Transactions per User:      %.2f                       \n" +
            "└─────────────────────────────────────────────────────────┘\n\n" +
            "┌─────────────────────────────────────────────────────────┐\n" +
            "│  KEY INSIGHTS                                          │\n" +
            "├─────────────────────────────────────────────────────────┤\n" +
            "│  • User engagement is at %.1f%%                          \n" +
            "│  • Average %.2f transactions per user                   \n" +
            "│  • %d recent transactions in last month                 \n" +
            "│  • Platform shows %s activity level                     \n" +
            "└─────────────────────────────────────────────────────────┘\n\n" +
            "═══════════════════════════════════════════════════════════\n" +
            "        Use 'Detailed Report' for per-user breakdown\n" +
            "═══════════════════════════════════════════════════════════\n",
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            analysis.getTotalUsers(),
            analysis.getActiveUsers(),
            analysis.getEngagementRate(),
            analysis.getTotalTransactions(),
            analysis.getRecentTransactions(),
            analysis.getAvgTransactionAmount(),
            analysis.getTransactionsPerUser(),
            analysis.getEngagementRate(),
            analysis.getTransactionsPerUser(),
            analysis.getRecentTransactions(),
            analysis.getEngagementRate() >= 50 ? "HEALTHY" : (analysis.getEngagementRate() >= 25 ? "MODERATE" : "LOW")
        );
    }
}

