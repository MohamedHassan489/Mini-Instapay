package com.example.national_bank_of_egypt;

import com.example.national_bank_of_egypt.Models.Model;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application  {
    @Override
    public void start(Stage stage)  {
        Model.getInstance().getViewFactory().showLoginWindow();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
