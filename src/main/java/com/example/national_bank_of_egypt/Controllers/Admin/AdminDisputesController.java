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
        if (selected != null && !resolution_fld.getText().isEmpty()) {
            if (Model.getInstance().resolveDispute(selected.getDisputeId(), resolution_fld.getText())) {
                status_lbl.setText("Dispute resolved successfully");
                status_lbl.setStyle("-fx-text-fill: green;");
                resolution_fld.setText("");
                Model.getInstance().loadAllDisputes();
                disputes_list.setItems(Model.getInstance().getAllDisputes());
            } else {
                status_lbl.setText("Failed to resolve dispute");
                status_lbl.setStyle("-fx-text-fill: red;");
            }
        } else {
            status_lbl.setText("Please select a dispute and enter resolution");
            status_lbl.setStyle("-fx-text-fill: red;");
        }
    }
}

