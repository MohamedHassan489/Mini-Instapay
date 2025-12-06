package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Models.Dispute;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class DisputeCellController implements Initializable {
    public Label dispute_id_lbl;
    public Label transaction_id_lbl;
    public Label reason_lbl;
    public Label status_lbl;
    public Label date_lbl;
    public Label resolution_lbl;
    private final Dispute dispute;

    public DisputeCellController(Dispute dispute) {
        this.dispute = dispute;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (dispute != null) {
            if (dispute_id_lbl != null) {
                dispute_id_lbl.setText("ID: " + dispute.getDisputeId().substring(0, Math.min(8, dispute.getDisputeId().length())));
            }
            if (transaction_id_lbl != null) {
                transaction_id_lbl.setText("Transaction: " + dispute.getTransactionId());
            }
            if (reason_lbl != null) {
                reason_lbl.setText("Reason: " + dispute.getReason());
            }
            if (status_lbl != null) {
                status_lbl.setText("Status: " + dispute.getStatus());
                // Color code status
                String status = dispute.getStatus();
                if (status != null) {
                    if ("RESOLVED".equalsIgnoreCase(status)) {
                        status_lbl.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else if ("REJECTED".equalsIgnoreCase(status)) {
                        status_lbl.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else if ("PENDING".equalsIgnoreCase(status)) {
                        status_lbl.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    } else {
                        status_lbl.setStyle("-fx-text-fill: black;");
                    }
                }
            }
            if (date_lbl != null) {
                date_lbl.setText("Date: " + dispute.getDateCreated().toString());
            }
            if (resolution_lbl != null) {
                if (dispute.getResolution() != null && !dispute.getResolution().isEmpty()) {
                    resolution_lbl.setText("Resolution: " + dispute.getResolution());
                    resolution_lbl.setVisible(true);
                } else {
                    resolution_lbl.setVisible(false);
                }
            }
        }
    }
}

