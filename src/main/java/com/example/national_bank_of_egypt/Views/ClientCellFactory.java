package com.example.national_bank_of_egypt.Views;

import com.example.national_bank_of_egypt.Controllers.Admin.ClientCellController;
import com.example.national_bank_of_egypt.Models.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;

public class ClientCellFactory extends ListCell<User> {
    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);
        if (empty || user == null){
            setText(null);
            setGraphic(null);
        }else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/ClientCell.fxml"));
                ClientCellController controller = new ClientCellController(user);
                loader.setController(controller);
                setText(null);
                setGraphic(loader.load());
            }catch (Exception e){
                e.printStackTrace();
                setText("Error loading user cell");
                setGraphic(null);
            }
        }
    }
}
