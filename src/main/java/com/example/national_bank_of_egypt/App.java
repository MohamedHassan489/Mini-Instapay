package com.example.national_bank_of_egypt;

import com.example.national_bank_of_egypt.Models.Model;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class App extends Application  {
    @Override
    public void start(Stage stage)  {
        try {
            System.out.println("Starting Mini-InstaPay application...");
            Model.getInstance().getViewFactory().showLoginWindow();
            System.out.println("Login window should be displayed now.");
        } catch (Exception e) {
            System.err.println("ERROR: Failed to start application!");
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Startup Error");
                alert.setHeaderText("Failed to start Mini-InstaPay");
                alert.setContentText("Error: " + e.getMessage() + "\n\nCheck console for details.");
                alert.showAndWait();
                Platform.exit();
            });
        }
    }
    
    public static void main(String[] args) {
        try {
            System.out.println("Launching JavaFX Application...");
            launch(args);
        } catch (Exception e) {
            System.err.println("FATAL ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
