package com.example.national_bank_of_egypt.Controllers.Admin;

import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Models.User;
import com.example.national_bank_of_egypt.Views.ClientCellFactory;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientsController implements Initializable {
    public ListView<User> Clients_ListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SetData();
        Clients_ListView.setItems(Model.getInstance().getUsers());
        Clients_ListView.setCellFactory(e -> new ClientCellFactory());
    }

    private void SetData(){
        if(Model.getInstance().getUsers().isEmpty()){
            Model.getInstance().loadAllUsers();
        }
    }
}
