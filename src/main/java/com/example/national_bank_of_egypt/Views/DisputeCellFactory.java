package com.example.national_bank_of_egypt.Views;

import com.example.national_bank_of_egypt.Controllers.Client.DisputeCellController;
import com.example.national_bank_of_egypt.Models.Dispute;
import com.example.national_bank_of_egypt.Utils.AnimationUtils;
import javafx.application.Platform;
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
                javafx.scene.Node cellContent = loader.load();
                setGraphic(cellContent);
                
                // Add fade-in animation
                if (cellContent != null) {
                    cellContent.setOpacity(0);
                    Platform.runLater(() -> {
                        AnimationUtils.fadeIn(cellContent, AnimationUtils.STANDARD_DURATION).play();
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                setText("Error loading dispute cell");
                setGraphic(null);
            }
        }
    }
}

