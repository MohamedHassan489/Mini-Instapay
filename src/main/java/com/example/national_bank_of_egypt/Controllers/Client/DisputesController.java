package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Models.Dispute;
import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Models.Transaction;
import com.example.national_bank_of_egypt.Utils.AnimationUtils;
import com.example.national_bank_of_egypt.Utils.ErrorHandler;
import com.example.national_bank_of_egypt.Views.DisputeCellFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.UUID;

public class DisputesController implements Initializable {
    public ComboBox<Transaction> transaction_combo;
    public TextField transactionId_fld;
    public TextArea reason_fld;
    public Button submitDispute_btn;
    public ListView<Dispute> disputes_list;
    public Label error_lbl;

    @FXML
    private VBox disputes_form_container;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (Model.getInstance().getCurrentUser() != null) {
            Model.getInstance().loadDisputes();
            if (disputes_list != null) {
                disputes_list.setItems(Model.getInstance().getDisputes());
                disputes_list.setCellFactory(e -> new DisputeCellFactory());
            }

            // Load user's transactions for the dropdown
            loadUserTransactions();
        }

        // Set up transaction combo box
        if (transaction_combo != null) {
            transaction_combo.setConverter(new StringConverter<Transaction>() {
                @Override
                public String toString(Transaction transaction) {
                    if (transaction == null)
                        return "";
                    String role = transaction.getSender().equals(Model.getInstance().getCurrentUser().getUserName())
                            ? "Sent"
                            : "Received";
                    return String.format("%s - %s $%.2f on %s (ID: %s)",
                            role,
                            role.equals("Sent") ? transaction.getReceiver() : transaction.getSender(),
                            transaction.getAmount(),
                            transaction.getDate() != null ? transaction.getDate().toString() : "N/A",
                            transaction.getTransactionId());
                }

                @Override
                public Transaction fromString(String string) {
                    return null;
                }
            });

            // When a transaction is selected, populate the transaction ID field
            transaction_combo.setOnAction(e -> {
                Transaction selected = transaction_combo.getSelectionModel().getSelectedItem();
                if (selected != null && transactionId_fld != null) {
                    transactionId_fld.setText(selected.getTransactionId());
                }
            });
        }

        if (submitDispute_btn != null) {
            submitDispute_btn.setOnAction(event -> onSubmitDispute());
        }

        // Add page load animations
        Platform.runLater(() -> {
            animatePageLoad();
            hideMessage();
        });
    }

    /**
     * Animate page load with fade-in and slide-up
     */
    private void animatePageLoad() {
        if (transaction_combo != null && transaction_combo.getScene() != null) {
            javafx.scene.Parent root = transaction_combo.getScene().getRoot();
            if (root != null) {
                root.setOpacity(0);
                root.setTranslateY(20);
                AnimationUtils.fadeInSlideUp(root, 20, AnimationUtils.ENTRANCE_DURATION).play();
            }
        }
    }

    private void loadUserTransactions() {
        if (transaction_combo == null || Model.getInstance().getCurrentUser() == null) {
            return;
        }

        try {
            // Load all transactions for the current user
            Model.getInstance().loadTransactions(-1);

            // Filter transactions that belong to the current user (as sender or receiver)
            javafx.collections.ObservableList<Transaction> userTransactions = javafx.collections.FXCollections
                    .observableArrayList();

            String currentUsername = Model.getInstance().getCurrentUser().getUserName();
            for (Transaction t : Model.getInstance().getTransactions()) {
                if (t.getSender() != null && t.getSender().equals(currentUsername) ||
                        t.getReceiver() != null && t.getReceiver().equals(currentUsername)) {
                    userTransactions.add(t);
                }
            }

            transaction_combo.setItems(userTransactions);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading transactions for dispute: " + e.getMessage());
        }
    }

    private void onSubmitDispute() {
        // Clear previous error
        hideMessage();

        String transactionId = transactionId_fld != null ? transactionId_fld.getText().trim() : "";
        String reason = reason_fld != null ? reason_fld.getText().trim() : "";

        // Validate input
        if (transactionId.isEmpty() || reason.isEmpty()) {
            showError("Please fill all fields");
            return;
        }

        // Validate transaction exists and belongs to user
        if (Model.getInstance().getCurrentUser() == null) {
            showError("User not logged in");
            return;
        }

        // Check if transaction exists and belongs to current user
        boolean transactionExists = false;
        for (com.example.national_bank_of_egypt.Models.Transaction t : Model.getInstance().getTransactions()) {
            if (t.getTransactionId().equals(transactionId) &&
                    (t.getSender().equals(Model.getInstance().getCurrentUser().getUserName()) ||
                            t.getReceiver().equals(Model.getInstance().getCurrentUser().getUserName()))) {
                transactionExists = true;
                break;
            }
        }

        if (!transactionExists) {
            // Load transactions to check
            Model.getInstance().loadTransactions(-1);
            for (com.example.national_bank_of_egypt.Models.Transaction t : Model.getInstance().getTransactions()) {
                if (t.getTransactionId().equals(transactionId) &&
                        (t.getSender().equals(Model.getInstance().getCurrentUser().getUserName()) ||
                                t.getReceiver().equals(Model.getInstance().getCurrentUser().getUserName()))) {
                    transactionExists = true;
                    break;
                }
            }
        }

        if (!transactionExists) {
            showError("Transaction not found or does not belong to you");
            return;
        }

        // Check if dispute already exists for this transaction
        for (Dispute d : Model.getInstance().getDisputes()) {
            if (d.getTransactionId().equals(transactionId) &&
                    d.getUserId().equals(Model.getInstance().getCurrentUser().getUserName())) {
                showError("A dispute already exists for this transaction");
                return;
            }
        }

        String disputeId = UUID.randomUUID().toString();
        String userId = Model.getInstance().getCurrentUser().getUserName();

        try {
            Model.getInstance().clearLastError();
            if (Model.getInstance().getDataBaseDriver().createDispute(disputeId, transactionId, userId, reason, "PENDING",
                    LocalDate.now())) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText("Dispute Submitted");
                successAlert.setContentText("Your dispute has been submitted successfully. An admin will review it.");
                successAlert.showAndWait();

                showSuccess("Dispute submitted successfully");

                // Clear form and refresh list
                if (transactionId_fld != null)
                    transactionId_fld.setText("");
                if (reason_fld != null)
                    reason_fld.setText("");
                if (transaction_combo != null)
                    transaction_combo.getSelectionModel().clearSelection();
                Model.getInstance().loadDisputes();
                loadUserTransactions(); // Reload transactions in case new ones were added
                if (disputes_list != null) {
                    disputes_list.setItems(Model.getInstance().getDisputes());
                    disputes_list.setCellFactory(e -> new DisputeCellFactory());
                }
            } else {
                String errorMessage = Model.getInstance().getLastErrorMessage();
                if (errorMessage != null && !errorMessage.isEmpty()) {
                    showError(errorMessage);
                } else {
                    showError("Failed to submit dispute. Please try again.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred: " + ErrorHandler.getUserFriendlyMessage(e));
        }
    }

    private void showMessage(String message, String style) {
        if (error_lbl != null) {
            error_lbl.setText(message);
            error_lbl.setStyle(style);
            error_lbl.setVisible(true);
            error_lbl.setManaged(true);
            // Slide down and fade in animation
            error_lbl.setTranslateY(-10);
            error_lbl.setOpacity(0);
            AnimationUtils.fadeInSlideUp(error_lbl, 10, AnimationUtils.STANDARD_DURATION).play();
        }
    }

    private void showError(String message) {
        showMessage(message, "-fx-text-fill: red;");
    }

    private void showSuccess(String message) {
        showMessage(message, "-fx-text-fill: green;");
    }

    private void hideMessage() {
        if (error_lbl != null) {
            error_lbl.setText("");
            error_lbl.setVisible(false);
            error_lbl.setManaged(false);
        }
    }
}
