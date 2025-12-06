package com.example.national_bank_of_egypt.Controllers.Admin;

import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Models.User;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
    public Button suspend_btn;
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
        
        if (suspend_btn != null) {
            suspend_btn.setOnAction(event -> onSuspend());
        }
    }
    
    private void onSuspend() {
        if (user != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Suspend Account");
            confirmAlert.setHeaderText("Confirm Account Suspension");
            confirmAlert.setContentText("Are you sure you want to suspend account: " + user.getUserName() + "?");
            
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (Model.getInstance().suspendAccount(user.getUserName())) {
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Success");
                        successAlert.setHeaderText("Account Suspended");
                        successAlert.setContentText("Account " + user.getUserName() + " has been suspended.");
                        successAlert.showAndWait();
                    } else {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText("Failed to Suspend Account");
                        errorAlert.setContentText("Could not suspend account. Please try again.");
                        errorAlert.showAndWait();
                    }
                }
            });
        }
    }
}
