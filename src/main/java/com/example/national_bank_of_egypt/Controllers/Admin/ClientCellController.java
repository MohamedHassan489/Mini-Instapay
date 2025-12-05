package com.example.national_bank_of_egypt.Controllers.Admin;

import com.example.national_bank_of_egypt.Models.User;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientCellController implements Initializable {
    public Label fName_lbl;
    public Label lName_lbl;
    public Label email_lbl;
    public Label phone_lbl;
    public Label data_lbl;
    public Label UserName_lbl;
    public Button delete_btn;
    private final User user;
    
    public ClientCellController(User user){
        this.user = user;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fName_lbl.textProperty().bind(user.firstNameProperty());
        lName_lbl.textProperty().bind(user.lastNameProperty());
        UserName_lbl.textProperty().bind(user.userNameProperty());
        email_lbl.textProperty().bind(user.emailProperty());
        phone_lbl.textProperty().bind(user.phoneNumberProperty());
        data_lbl.textProperty().bind(user.dateCreatedProperty().asString());
    }
}
