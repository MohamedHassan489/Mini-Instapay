package com.example.national_bank_of_egypt.Controllers.Admin;

import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Models.User;
import com.example.national_bank_of_egypt.Views.ClientCellFactory;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientsController implements Initializable {
    public ListView<User> Clients_ListView;
    public TextField search_fld;
    public Label userDetails_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SetData();
        Clients_ListView.setItems(Model.getInstance().getUsers());
        Clients_ListView.setCellFactory(e -> new ClientCellFactory());
        
        // Add selection listener to show user profile details
        Clients_ListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showUserProfile(newValue);
            }
        });
        
        // Add search functionality
        if (search_fld != null) {
            search_fld.textProperty().addListener((observable, oldValue, newValue) -> filterUsers(newValue));
        }
    }

    private void SetData(){
        if(Model.getInstance().getUsers().isEmpty()){
            Model.getInstance().loadAllUsers();
        }
    }
    
    private void filterUsers(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            Clients_ListView.setItems(Model.getInstance().getUsers());
            return;
        }
        
        javafx.collections.transformation.FilteredList<User> filtered = 
            new javafx.collections.transformation.FilteredList<>(Model.getInstance().getUsers(), user -> {
                String lowerSearch = searchText.toLowerCase();
                return user.getUserName().toLowerCase().contains(lowerSearch) ||
                       user.getFirstName().toLowerCase().contains(lowerSearch) ||
                       user.getLastName().toLowerCase().contains(lowerSearch) ||
                       user.getEmail().toLowerCase().contains(lowerSearch) ||
                       user.getPhoneNumber().toLowerCase().contains(lowerSearch);
            });
        
        Clients_ListView.setItems(filtered);
    }
    
    private void showUserProfile(User user) {
        if (userDetails_lbl != null) {
            StringBuilder details = new StringBuilder();
            details.append("User Profile Details:\n\n");
            details.append("Name: ").append(user.getFirstName()).append(" ").append(user.getLastName()).append("\n");
            details.append("Username: ").append(user.getUserName()).append("\n");
            details.append("Email: ").append(user.getEmail()).append("\n");
            details.append("Phone: ").append(user.getPhoneNumber()).append("\n");
            details.append("Address: ").append(user.getAddress()).append("\n");
            details.append("Date Created: ").append(user.dateCreatedProperty().get()).append("\n");
            details.append("2FA Enabled: ").append(user.twoFactorEnabledProperty().get()).append("\n");
            details.append("Bank Accounts: ").append(user.getBankAccounts().size()).append("\n");
            
            if (!user.getBankAccounts().isEmpty()) {
                details.append("\nBank Accounts:\n");
                user.getBankAccounts().forEach(account -> {
                    details.append("  - ").append(account.getAccountType())
                           .append(" (").append(account.getAccountNumber())
                           .append("): $").append(String.format("%.2f", account.getBalance())).append("\n");
                });
            }
            
            userDetails_lbl.setText(details.toString());
        }
    }
}
