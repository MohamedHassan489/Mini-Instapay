package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {
    public TextField firstName_fld;
    public TextField lastName_fld;
    public TextField email_fld;
    public TextField phoneNumber_fld;
    public TextField address_fld;
    public Button update_btn;
    public Label error_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (Model.getInstance().getCurrentUser() != null) {
            firstName_fld.setText(Model.getInstance().getCurrentUser().getFirstName());
            lastName_fld.setText(Model.getInstance().getCurrentUser().getLastName());
            email_fld.setText(Model.getInstance().getCurrentUser().getEmail());
            phoneNumber_fld.setText(Model.getInstance().getCurrentUser().getPhoneNumber());
            address_fld.setText(Model.getInstance().getCurrentUser().addressProperty().get());
        }
        
        if (update_btn != null) {
            update_btn.setOnAction(event -> onUpdateProfile());
        }
    }

    private void onUpdateProfile() {
        String firstName = firstName_fld.getText();
        String lastName = lastName_fld.getText();
        String email = email_fld.getText();
        String phoneNumber = phoneNumber_fld.getText();
        String address = address_fld.getText();
        
        if (Model.getInstance().updateUserProfile(firstName, lastName, email, phoneNumber, address)) {
            error_lbl.setText("Profile updated successfully!");
        } else {
            error_lbl.setText("Failed to update profile");
        }
    }
}

