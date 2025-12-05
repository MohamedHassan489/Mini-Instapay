package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Models.Dispute;
import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Models.DataBaseDriver;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.UUID;

public class DisputesController implements Initializable {
    public TextField transactionId_fld;
    public TextArea reason_fld;
    public Button submitDispute_btn;
    public ListView<Dispute> disputes_list;
    public Label error_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (Model.getInstance().getCurrentUser() != null) {
            Model.getInstance().loadDisputes();
            disputes_list.setItems(Model.getInstance().getDisputes());
        }
        
        if (submitDispute_btn != null) {
            submitDispute_btn.setOnAction(event -> onSubmitDispute());
        }
    }

    private void onSubmitDispute() {
        String transactionId = transactionId_fld.getText();
        String reason = reason_fld.getText();
        
        if (transactionId.isEmpty() || reason.isEmpty()) {
            error_lbl.setText("Please fill all fields");
            return;
        }
        
        String disputeId = UUID.randomUUID().toString();
        String userId = Model.getInstance().getCurrentUser().getUserName();
        
        if (Model.getInstance().getDataBaseDriver().createDispute(disputeId, transactionId, userId, reason, "PENDING", LocalDate.now())) {
            error_lbl.setText("Dispute submitted successfully");
            transactionId_fld.setText("");
            reason_fld.setText("");
            Model.getInstance().loadDisputes();
            disputes_list.setItems(Model.getInstance().getDisputes());
        } else {
            error_lbl.setText("Failed to submit dispute");
        }
    }
}

