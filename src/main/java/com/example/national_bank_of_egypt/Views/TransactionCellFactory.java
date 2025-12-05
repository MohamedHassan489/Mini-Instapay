package com.example.national_bank_of_egypt.Views;

import com.example.national_bank_of_egypt.Controllers.Client.TransactionCellController;
import com.example.national_bank_of_egypt.Models.Transaction;
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
                setGraphic(loader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}