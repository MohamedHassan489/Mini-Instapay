package com.example.national_bank_of_egypt.Controllers.Admin;

import com.example.national_bank_of_egypt.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.Random;
import java.util.ResourceBundle;

public class CreateClientController implements Initializable{
    public Label error_lbl;
    public Label pAddress_lbl;
    public TextField fName_fld;
    public TextField lName_fld;
    public TextField password_fld;
    public CheckBox pAddress_box;
    public CheckBox ch_acc_box;
    public TextField ch_amount_fld;
    public CheckBox sv_acc_box;
    public TextField sv_amount_fld;
    public Button create_client_btn;
    private String payeeAddress;
    private boolean CreateCheckingAccountFlag = false;
    private boolean CreateSavingAccountFlag = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (create_client_btn != null) {
            create_client_btn.setOnAction(actionEvent -> createUser());
        }
        if (pAddress_box != null) {
            pAddress_box.selectedProperty().addListener((observableValue, OldVal, newVal) -> {
                if (newVal){
                    payeeAddress = createpayeeAddress();
                    onCreatePayeeAddress();
                }
            });
        }
        if (ch_acc_box != null) {
            ch_acc_box.selectedProperty().addListener((observableValue1, OldV, newV) -> {
                if (newV){
                    CreateCheckingAccountFlag = true;
                }
            });
        }
        if (sv_acc_box != null) {
            sv_acc_box.selectedProperty().addListener((observableValue, OldVal, newVal) -> {
                if(newVal){
                    CreateSavingAccountFlag = true;
                }
            });
        }
    }

    private void createUser(){
        String firstName = fName_fld.getText();
        String lastName = lName_fld.getText();
        String password = password_fld.getText();
        
        if (firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()) {
            error_lbl.setText("Please fill all required fields");
            return;
        }
        
        if (payeeAddress == null || payeeAddress.isEmpty()) {
            payeeAddress = createpayeeAddress();
        }
        
        String email = payeeAddress + "@instapay.com";
        String phoneNumber = "1" + String.format("%09d", new Random().nextInt(999999999));
        String address = "Not provided";
        String userName = payeeAddress;
        
        if (Model.getInstance().registerUser(firstName, lastName, email, phoneNumber, address, userName, password)) {
            if (CreateCheckingAccountFlag && ch_amount_fld != null && !ch_amount_fld.getText().isEmpty()) {
                createAccount("Checking");
            }
            if (CreateSavingAccountFlag && sv_amount_fld != null && !sv_amount_fld.getText().isEmpty()) {
                createAccount("Savings");
            }
            error_lbl.setText("User created successfully!");
            emptyFields();
        } else {
            error_lbl.setText("Error: Username already exists");
        }
    }

    private void createAccount(String accountType){
        String firstSection = "3201";
        String lastSection = Integer.toString((new Random()).nextInt(9999)+ 1000);
        String accountNumber = firstSection + " " + lastSection;
        double balance = 0.0;
        
        if (accountType.equals("Checking") && ch_amount_fld != null && !ch_amount_fld.getText().isEmpty()) {
            balance = Double.parseDouble(ch_amount_fld.getText());
            Model.getInstance().addBankAccount(accountNumber, "Default Bank", balance, "Checking");
        } else if (accountType.equals("Savings") && sv_amount_fld != null && !sv_amount_fld.getText().isEmpty()) {
            balance = Double.parseDouble(sv_amount_fld.getText());
            Model.getInstance().addBankAccount(accountNumber, "Default Bank", balance, "Savings");
        }
    }

    private void onCreatePayeeAddress(){
        if (fName_fld.getText() != null && !fName_fld.getText().isEmpty() && 
            lName_fld.getText() != null && !lName_fld.getText().isEmpty()){
            pAddress_lbl.setText(payeeAddress);
        }
    }

    private String createpayeeAddress(){
        int id = new Random().nextInt(9999) + 1000;
        if (fName_fld.getText() != null && !fName_fld.getText().isEmpty() && 
            lName_fld.getText() != null && !lName_fld.getText().isEmpty()) {
            char fChar = Character.toLowerCase(fName_fld.getText().charAt(0));
            return "@" + fChar + lName_fld.getText() + id;
        }
        return "@user" + id;
    }

    private void emptyFields(){
        fName_fld.setText("");
        lName_fld.setText("");
        password_fld.setText("");
        if (pAddress_box != null) pAddress_box.setSelected(false);
        if (pAddress_lbl != null) pAddress_lbl.setText("");
        if (ch_amount_fld != null) ch_amount_fld.setText("");
        if (ch_acc_box != null) ch_acc_box.setSelected(false);
        if (sv_acc_box != null) sv_acc_box.setSelected(false);
        if (sv_amount_fld != null) sv_amount_fld.setText("");
        payeeAddress = null;
        CreateCheckingAccountFlag = false;
        CreateSavingAccountFlag = false;
    }
}
