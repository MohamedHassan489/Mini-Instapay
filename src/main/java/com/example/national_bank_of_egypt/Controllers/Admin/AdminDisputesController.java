package com.example.national_bank_of_egypt.Controllers.Admin;

import com.example.national_bank_of_egypt.Models.Dispute;
import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Views.DisputeCellFactory;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminDisputesController implements Initializable {
    public ListView<Dispute> disputes_list;
    public TextArea resolution_fld;
    public Button resolve_btn;
    public Label status_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Model.getInstance().loadAllDisputes();
        disputes_list.setItems(Model.getInstance().getAllDisputes());
        
        // Use DisputeCellFactory to display dispute details including user-provided reason
        disputes_list.setCellFactory(e -> new DisputeCellFactory());
        
        if (resolve_btn != null) {
            resolve_btn.setOnAction(event -> onResolveDispute());
        }
    }
    
    private void onResolveDispute() {
        Dispute selected = disputes_list.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            showError("Please select a dispute to resolve.");
            return;
        }
        
        if (resolution_fld.getText() == null || resolution_fld.getText().trim().isEmpty()) {
            showError("Please enter a resolution description.");
            return;
        }
        
        try {
            Model.getInstance().clearLastError();
            if (Model.getInstance().resolveDispute(selected.getDisputeId(), resolution_fld.getText().trim())) {
                showSuccess("Dispute resolved successfully!");
                resolution_fld.setText("");
                Model.getInstance().loadAllDisputes();
                disputes_list.setItems(Model.getInstance().getAllDisputes());
            } else {
                String errorMessage = Model.getInstance().getLastErrorMessage();
                if (errorMessage != null && !errorMessage.isEmpty()) {
                    showError(errorMessage);
                } else {
                    showError("Failed to resolve dispute. Please try again.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        if (status_lbl != null) {
            status_lbl.setText(message);
            status_lbl.setStyle("-fx-text-fill: red;");
        }
    }
    
    private void showSuccess(String message) {
        if (status_lbl != null) {
            status_lbl.setText(message);
            status_lbl.setStyle("-fx-text-fill: green;");
        }
    }
}

