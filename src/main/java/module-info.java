module com.example.national_bank_of_egypt {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive javafx.base;
    requires transitive java.sql;
    requires org.xerial.sqlitejdbc;
    requires java.mail;
    requires java.net.http;

    opens com.example.national_bank_of_egypt to javafx.fxml;
    opens com.example.national_bank_of_egypt.Controllers to javafx.fxml;
    opens com.example.national_bank_of_egypt.Controllers.Admin to javafx.fxml;
    opens com.example.national_bank_of_egypt.Controllers.Client to javafx.fxml;
    opens com.example.national_bank_of_egypt.Security to javafx.fxml;
    opens com.example.national_bank_of_egypt.Notifications to javafx.fxml;
    opens com.example.national_bank_of_egypt.Transactions to javafx.fxml;
    opens com.example.national_bank_of_egypt.Reports to javafx.fxml;
    
    exports com.example.national_bank_of_egypt;
    exports com.example.national_bank_of_egypt.Controllers;
    exports com.example.national_bank_of_egypt.Controllers.Admin;
    exports com.example.national_bank_of_egypt.Controllers.Client;
    exports com.example.national_bank_of_egypt.Views;
    exports com.example.national_bank_of_egypt.Models;
    exports com.example.national_bank_of_egypt.Security;
    exports com.example.national_bank_of_egypt.Notifications;
    exports com.example.national_bank_of_egypt.Transactions;
    exports com.example.national_bank_of_egypt.Reports;
    exports com.example.national_bank_of_egypt.Repository;
    exports com.example.national_bank_of_egypt.Exceptions;
    exports com.example.national_bank_of_egypt.Utils;
}