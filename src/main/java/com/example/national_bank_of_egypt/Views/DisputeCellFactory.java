package com.example.national_bank_of_egypt.Views;

import com.example.national_bank_of_egypt.Controllers.Client.DisputeCellController;
import com.example.national_bank_of_egypt.Models.Dispute;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;

import java.io.IOException;

public class DisputeCellFactory extends ListCell<Dispute> {
    @Override
    protected void updateItem(Dispute dispute, boolean empty) {
        super.updateItem(dispute, empty);
        if (empty || dispute == null) {
            setText(null);
            setGraphic(null);
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Client/DisputeCell.fxml"));
            try {
                DisputeCellController controller = new DisputeCellController(dispute);
                loader.setController(controller);
                setText(null);
                setGraphic(loader.load());
            } catch (IOException e) {
                e.printStackTrace();
                setText("Error loading dispute cell");
                setGraphic(null);
            }
        }
    }
}

