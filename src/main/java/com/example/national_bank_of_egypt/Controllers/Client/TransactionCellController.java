package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Models.Transaction;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class TransactionCellController implements Initializable {
    public Label trans_date_lbl;
    public Label sender_lbl;
    public Label receiver_lbl;
    public Label amount_lbl;
    public Label status_lbl;
    private final Transaction transaction;

    public TransactionCellController(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (transaction != null) {
            if (sender_lbl != null) {
                sender_lbl.textProperty().bind(transaction.senderProperty());
            }
            if (receiver_lbl != null) {
                receiver_lbl.textProperty().bind(transaction.receiverProperty());
            }
            if (amount_lbl != null) {
                amount_lbl.textProperty().bind(javafx.beans.binding.Bindings.concat("$").concat(
                    transaction.amountProperty().asString("%.2f")));
            }
            if (trans_date_lbl != null) {
                trans_date_lbl.textProperty().bind(transaction.dateProperty().asString());
            }
            if (status_lbl != null) {
                status_lbl.textProperty().bind(transaction.statusProperty());
                // Color code status
                transaction.statusProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        if ("SUCCESS".equalsIgnoreCase(newVal)) {
                            status_lbl.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                        } else if ("FAILED".equalsIgnoreCase(newVal) || "SUSPICIOUS".equalsIgnoreCase(newVal)) {
                            status_lbl.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        } else if ("PENDING".equalsIgnoreCase(newVal)) {
                            status_lbl.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                        } else {
                            status_lbl.setStyle("-fx-text-fill: black;");
                        }
                    }
                });
                // Set initial color
                String status = transaction.getStatus();
                if (status != null) {
                    if ("SUCCESS".equalsIgnoreCase(status)) {
                        status_lbl.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else if ("FAILED".equalsIgnoreCase(status) || "SUSPICIOUS".equalsIgnoreCase(status)) {
                        status_lbl.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else if ("PENDING".equalsIgnoreCase(status)) {
                        status_lbl.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    }
                }
            }
        }
    }
}