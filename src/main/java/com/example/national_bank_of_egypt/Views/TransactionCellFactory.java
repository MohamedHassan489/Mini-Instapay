package com.example.national_bank_of_egypt.Views;

import com.example.national_bank_of_egypt.Controllers.Client.TransactionCellController;
import com.example.national_bank_of_egypt.Models.Transaction;
import com.example.national_bank_of_egypt.Utils.AnimationUtils;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;

import java.io.IOException;

public class TransactionCellFactory extends ListCell<Transaction> {
    @Override
    protected void updateItem(Transaction transaction, boolean b) {
        super.updateItem(transaction, b);
        if (b) {
            setText(null);
            setGraphic(null);
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Client/TransactionCell.fxml"));
            try {
                TransactionCellController controller = new TransactionCellController(transaction);
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
            }
        }
    }
}