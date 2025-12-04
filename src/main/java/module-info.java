module com.example.national_bank_of_egypt {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;



    opens com.example.national_bank_of_egypt to javafx.fxml;
    exports com.example.national_bank_of_egypt;
    exports com.example.national_bank_of_egypt.Controllers;
    exports com.example.national_bank_of_egypt.Controllers.Admin;
    exports com.example.national_bank_of_egypt.Controllers.Client;
    exports com.example.national_bank_of_egypt.Views;
    exports com.example.national_bank_of_egypt.Models;


}