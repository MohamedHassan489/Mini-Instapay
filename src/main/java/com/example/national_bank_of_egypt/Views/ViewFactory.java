package com.example.national_bank_of_egypt.Views;

import com.example.national_bank_of_egypt.Controllers.Admin.AdminController;
import com.example.national_bank_of_egypt.Controllers.Client.ClientController;
import com.example.national_bank_of_egypt.Utils.AnimationUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ViewFactory {

    private AccountType loginAccountType;

    private final ObjectProperty<clientmenuoption> clientSelectedMenuItem;
    private final ObjectProperty<AdminMenuOption> adminselectedmenuitem;
    // View cache fields - assigned but may be used for caching in future
    @SuppressWarnings("unused")
    private AnchorPane dashboardview;
    @SuppressWarnings("unused")
    private AnchorPane accountsView;
    @SuppressWarnings("unused")
    private AnchorPane transactionsView;
    @SuppressWarnings("unused")
    private AnchorPane sendMoneyView;
    @SuppressWarnings("unused")
    private AnchorPane profileView;
    @SuppressWarnings("unused")
    private AnchorPane disputesView;
    @SuppressWarnings("unused")
    private AnchorPane notificationsView;
    @SuppressWarnings("unused")
    private AnchorPane bankStatementView;
    @SuppressWarnings("unused")
    private AnchorPane usersView;
    @SuppressWarnings("unused")
    private AnchorPane adminTransactionsView;
    @SuppressWarnings("unused")
    private AnchorPane adminDisputesView;
    @SuppressWarnings("unused")
    private AnchorPane reportsView;
    @SuppressWarnings("unused")
    private AnchorPane systemHealthView;

    public ViewFactory() {
        this.loginAccountType = AccountType.CLIENT;
        this.clientSelectedMenuItem = new SimpleObjectProperty<>();
        this.adminselectedmenuitem = new SimpleObjectProperty<>();
    }

    public void setLoginAccountType(AccountType loginAccountType) {
        this.loginAccountType = loginAccountType;
    }

    public AccountType getLoginAccountType() {
        return loginAccountType;
    }

    public ObjectProperty<clientmenuoption> getClientSelectedMenuItem() {

        return clientSelectedMenuItem;
    }

    public AnchorPane getDashboardview() {
        // #region agent log
        try {
            java.nio.file.Files.write(
                    java.nio.file.Paths
                            .get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"),
                    ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"D1\",\"location\":\"ViewFactory.java:54\",\"message\":\"getDashboardview entry\",\"timestamp\":"
                            + System.currentTimeMillis() + "}\n").getBytes(),
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception logEx) {
        }
        // #endregion
        // Always reload to ensure fresh state and proper initialization
        try {
            // #region agent log
            try {
                java.nio.file.Files.write(
                        java.nio.file.Paths
                                .get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"),
                        ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"D1\",\"location\":\"ViewFactory.java:57\",\"message\":\"loading Dashboard.fxml\",\"timestamp\":"
                                + System.currentTimeMillis() + "}\n").getBytes(),
                        java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            } catch (Exception logEx) {
            }
            // #endregion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Client/Dashboard.fxml"));
            AnchorPane view = loader.load();
            // #region agent log
            try {
                java.nio.file.Files.write(
                        java.nio.file.Paths
                                .get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"),
                        ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"D1\",\"location\":\"ViewFactory.java:59\",\"message\":\"Dashboard.fxml loaded successfully\",\"timestamp\":"
                                + System.currentTimeMillis() + ",\"data\":{\"viewNull\":" + (view == null) + "}}\n")
                                .getBytes(),
                        java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            } catch (Exception logEx) {
            }
            // #endregion
            dashboardview = view;
            System.out.println("Dashboard view loaded successfully");
            return view;
        } catch (Exception e) {
            // #region agent log
            try {
                java.nio.file.Files.write(
                        java.nio.file.Paths
                                .get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"),
                        ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"D1\",\"location\":\"ViewFactory.java:63\",\"message\":\"Dashboard.fxml load exception\",\"timestamp\":"
                                + System.currentTimeMillis() + ",\"data\":{\"error\":\"" + e.getMessage()
                                + "\",\"class\":\"" + e.getClass().getName() + "\"}}\n").getBytes(),
                        java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            } catch (Exception logEx) {
            }
            // #endregion
            e.printStackTrace();
            System.err.println("Error loading Dashboard view: " + e.getMessage());
            // Return error pane with message
            javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
                    "Error loading Dashboard view: " + e.getMessage() + "\nPlease check the console for details.");
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14; -fx-padding: 20;");
            errorLabel.setWrapText(true);
            AnchorPane errorPane = new AnchorPane();
            errorPane.getChildren().add(errorLabel);
            javafx.scene.layout.AnchorPane.setLeftAnchor(errorLabel, 20.0);
            javafx.scene.layout.AnchorPane.setTopAnchor(errorLabel, 20.0);
            javafx.scene.layout.AnchorPane.setRightAnchor(errorLabel, 20.0);
            dashboardview = errorPane;
            return errorPane;
        }
    }

    public AnchorPane getTransactionsView() {
        // Always reload to ensure fresh state and proper initialization
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Client/Transactions.fxml"));
            AnchorPane view = loader.load();
            transactionsView = view;
            System.out.println("Transactions view loaded successfully");
            return view;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading Transactions view: " + e.getMessage());
            System.err.println("Exception type: " + e.getClass().getName());
            if (e.getCause() != null) {
                System.err.println("Caused by: " + e.getCause().getMessage());
            }
            // Return empty AnchorPane with error message if loading fails
            AnchorPane errorPane = new AnchorPane();
            javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
                    "Error loading Transactions view.\n" +
                            "Error: " + e.getMessage() + "\n" +
                            "Please check the console for details and restart the application.");
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11; -fx-padding: 20; -fx-wrap-text: true;");
            errorLabel.setWrapText(true);
            errorPane.getChildren().add(errorLabel);
            transactionsView = errorPane;
            return errorPane;
        }
    }

    public AnchorPane getAccountsView() {
        // Always reload to ensure fresh state and proper initialization
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Client/Accounts.fxml"));
            AnchorPane view = loader.load();
            accountsView = view;
            System.out.println("Accounts view loaded successfully");
            return view;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading Accounts view: " + e.getMessage());
            System.err.println("Exception type: " + e.getClass().getName());
            if (e.getCause() != null) {
                System.err.println("Caused by: " + e.getCause().getMessage());
            }
            // Return empty AnchorPane with error message if loading fails
            AnchorPane errorPane = new AnchorPane();
            javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
                    "Error loading Accounts view.\n" +
                            "Error: " + e.getMessage() + "\n" +
                            "Please check the console for details and restart the application.");
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11; -fx-padding: 20; -fx-wrap-text: true;");
            errorLabel.setWrapText(true);
            errorPane.getChildren().add(errorLabel);
            accountsView = errorPane;
            return errorPane;
        }
    }

    public AnchorPane getSendMoneyView() {
        // Always reload to ensure fresh state and proper initialization
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Client/SendMoney.fxml"));
            AnchorPane view = loader.load();
            sendMoneyView = view;
            System.out.println("SendMoney view loaded successfully");
            return view;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading SendMoney view: " + e.getMessage());
            System.err.println("Exception type: " + e.getClass().getName());
            if (e.getCause() != null) {
                System.err.println("Caused by: " + e.getCause().getMessage());
            }
            // Return empty AnchorPane with error message if loading fails
            AnchorPane errorPane = new AnchorPane();
            javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
                    "Error loading Send Money view.\n" +
                            "Error: " + e.getMessage() + "\n" +
                            "Please check the console for details and restart the application.");
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11; -fx-padding: 20; -fx-wrap-text: true;");
            errorLabel.setWrapText(true);
            errorPane.getChildren().add(errorLabel);
            sendMoneyView = errorPane;
            return errorPane;
        }
    }

    public AnchorPane getProfileView() {
        // Always reload to ensure fresh state and proper initialization
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Client/Profile.fxml"));
            AnchorPane view = loader.load();
            profileView = view;
            System.out.println("Profile view loaded successfully");
            return view;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading Profile view: " + e.getMessage());
            System.err.println("Exception type: " + e.getClass().getName());
            if (e.getCause() != null) {
                System.err.println("Caused by: " + e.getCause().getMessage());
            }
            // Return empty AnchorPane with error message if loading fails
            AnchorPane errorPane = new AnchorPane();
            javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
                    "Error loading Profile view.\n" +
                            "Error: " + e.getMessage() + "\n" +
                            "Please check the console for details and restart the application.");
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11; -fx-padding: 20; -fx-wrap-text: true;");
            errorLabel.setWrapText(true);
            errorPane.getChildren().add(errorLabel);
            profileView = errorPane;
            return errorPane;
        }
    }

    public AnchorPane getDisputesView() {
        // Always reload to ensure fresh state and proper initialization
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Client/Disputes.fxml"));
            AnchorPane view = loader.load();
            disputesView = view;
            System.out.println("Disputes view loaded successfully");
            return view;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading Disputes view: " + e.getMessage());
            System.err.println("Exception type: " + e.getClass().getName());
            if (e.getCause() != null) {
                System.err.println("Caused by: " + e.getCause().getMessage());
            }
            // Return empty AnchorPane with error message if loading fails
            AnchorPane errorPane = new AnchorPane();
            javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
                    "Error loading Disputes view.\n" +
                            "Error: " + e.getMessage() + "\n" +
                            "Please check the console for details and restart the application.");
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11; -fx-padding: 20; -fx-wrap-text: true;");
            errorLabel.setWrapText(true);
            errorPane.getChildren().add(errorLabel);
            disputesView = errorPane;
            return errorPane;
        }
    }

    public AnchorPane getNotificationsView() {
        // Always reload to ensure fresh state and proper initialization
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Client/Notifications.fxml"));
            AnchorPane view = loader.load();
            notificationsView = view;
            System.out.println("Notifications view loaded successfully");
            return view;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading Notifications view: " + e.getMessage());
            System.err.println("Exception type: " + e.getClass().getName());
            if (e.getCause() != null) {
                System.err.println("Caused by: " + e.getCause().getMessage());
            }
            // Return empty AnchorPane with error message if loading fails
            AnchorPane errorPane = new AnchorPane();
            javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
                    "Error loading Notifications view.\n" +
                            "Error: " + e.getMessage() + "\n" +
                            "Please check the console for details and restart the application.");
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11; -fx-padding: 20; -fx-wrap-text: true;");
            errorLabel.setWrapText(true);
            errorPane.getChildren().add(errorLabel);
            notificationsView = errorPane;
            return errorPane;
        }
    }

    public AnchorPane getBankStatementView() {
        // Always reload to ensure fresh state and proper initialization
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Client/BankStatement.fxml"));
            AnchorPane view = loader.load();
            bankStatementView = view;
            System.out.println("Bank Statement view loaded successfully");
            return view;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading Bank Statement view: " + e.getMessage());
            System.err.println("Exception type: " + e.getClass().getName());
            if (e.getCause() != null) {
                System.err.println("Caused by: " + e.getCause().getMessage());
            }
            // Return empty AnchorPane with error message if loading fails
            AnchorPane errorPane = new AnchorPane();
            javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
                    "Error loading Bank Statement view.\n" +
                            "Error: " + e.getMessage() + "\n" +
                            "Please check the console for details and restart the application.");
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11; -fx-padding: 20; -fx-wrap-text: true;");
            errorLabel.setWrapText(true);
            errorPane.getChildren().add(errorLabel);
            bankStatementView = errorPane;
            return errorPane;
        }
    }

    public AnchorPane getReportsView() {
        if (reportsView == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/Reports.fxml"));
                reportsView = loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return reportsView;
    }

    public AnchorPane getSystemHealthView() {
        if (systemHealthView == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/SystemHealth.fxml"));
                systemHealthView = loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return systemHealthView;
    }

    public void showLoginWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        CreateLoginStage(loader);
    }

    public void showRegistrationWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Register.fxml"));
        CreateLoginStage(loader);
    }

    private void CreateLoginStage(FXMLLoader loader) {
        try {
            javafx.scene.Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            try {
                stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/images/icon.png"))));
            } catch (Exception e) {
                // Icon not critical, continue without it
            }
            stage.setResizable(true);
            // Login/Registration window size - wider for better layout
            stage.setWidth(900);
            stage.setHeight(700);
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setMaxWidth(1100);
            stage.setMaxHeight(800);
            stage.setScene(scene);
            stage.setTitle("Mini-InstaPay - Login");

            // Add window entrance animation
            root.setOpacity(0);
            root.setScaleX(0.95);
            root.setScaleY(0.95);
            AnimationUtils.windowEntrance(root, AnimationUtils.STANDARD_DURATION).play();

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Show error dialog instead of blank page
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load window");
            alert.setContentText("An error occurred while loading the window: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void showClinetWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Client/Client.fxml"));
        ClientController clientController = new ClientController();
        loader.setController(clientController);
        CreateStage(loader);
    }

    // Admin
    public ObjectProperty<AdminMenuOption> getAdminselectedmenuitem() {
        return adminselectedmenuitem;
    }

    public void showAdminWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/Admin.fxml"));
        AdminController controller = new AdminController();
        loader.setController(controller);
        CreateStage(loader);
    }

    public AnchorPane getUsersView() {
        if (usersView == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/Users.fxml"));
                usersView = loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return usersView;
    }

    public AnchorPane getAdminTransactionsView() {
        if (adminTransactionsView == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/AdminTransactions.fxml"));
                adminTransactionsView = loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return adminTransactionsView;
    }

    public AnchorPane getAdminDisputesView() {
        if (adminDisputesView == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/AdminDisputes.fxml"));
                adminDisputesView = loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return adminDisputesView;
    }

    private void CreateStage(FXMLLoader loader) {
        try {
            javafx.scene.Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            try {
                stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/images/icon.png"))));
            } catch (Exception e) {
                // Icon not critical, continue without it
            }
            stage.setResizable(true);
            // Increased width to 1050 to accommodate 250px sidebar + 800px content area
            stage.setWidth(1050);
            stage.setHeight(960);
            stage.setMinWidth(800);
            stage.setMinHeight(700);
            stage.setMaxWidth(1400);
            stage.setMaxHeight(1200);
            stage.setScene(scene);
            stage.setTitle("Mini-InstaPay");

            // Add window entrance animation
            root.setOpacity(0);
            root.setScaleX(0.95);
            root.setScaleY(0.95);
            AnimationUtils.windowEntrance(root, AnimationUtils.STANDARD_DURATION).play();

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Show error dialog instead of blank page
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load window");
            alert.setContentText("An error occurred while loading the window: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void closeStage(Stage stage) {
        if (stage != null && stage.getScene() != null && stage.getScene().getRoot() != null) {
            // Add window exit animation
            javafx.scene.Node root = stage.getScene().getRoot();
            javafx.animation.ParallelTransition exitAnim = AnimationUtils.windowExit(root,
                    AnimationUtils.STANDARD_DURATION);
            exitAnim.setOnFinished(e -> stage.close());
            exitAnim.play();
        } else {
            if (stage != null) {
                stage.close();
            }
        }
    }
}
