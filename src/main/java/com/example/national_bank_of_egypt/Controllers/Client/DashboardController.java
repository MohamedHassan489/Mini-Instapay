package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Models.Transaction;
import com.example.national_bank_of_egypt.Utils.AnimationUtils;
import com.example.national_bank_of_egypt.Views.TransactionCellFactory;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    public Label userName;
    public Label login_date;
    public Label income_bal;
    public Label expenses_bal;
    public ListView<Transaction> Transaction_list;
    public TextField username_fld;
    public TextField amount_fld;
    public TextArea message_fld;
    public Button send_btn;
    public Label checking_bal;
    public Label checking_acc_num;
    public Label fullName_label;
    public Label username_label;
    public Label bank_name_label;
    public Label account_type_label;
    public ComboBox<String> account_selector;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // #region agent log
        try {
            java.nio.file.Files.write(
                    java.nio.file.Paths
                            .get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"),
                    ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1\",\"location\":\"DashboardController.java:33\",\"message\":\"initialize entry\",\"timestamp\":"
                            + System.currentTimeMillis() + "}\n").getBytes(),
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception logEx) {
        }
        // #endregion
        try {
            // #region agent log
            try {
                java.nio.file.Files.write(
                        java.nio.file.Paths
                                .get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"),
                        ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1\",\"location\":\"DashboardController.java:36\",\"message\":\"checking FXML fields\",\"timestamp\":"
                                + System.currentTimeMillis() + ",\"data\":{\"userName_null\":" + (userName == null)
                                + ",\"login_date_null\":" + (login_date == null) + ",\"fullName_label_null\":"
                                + (fullName_label == null) + ",\"username_label_null\":" + (username_label == null)
                                + ",\"bank_name_label_null\":" + (bank_name_label == null)
                                + ",\"account_type_label_null\":" + (account_type_label == null) + "}}\n").getBytes(),
                        java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            } catch (Exception logEx) {
            }
            // #endregion
            // Add null checks to prevent crashes
            if (userName == null || login_date == null) {
                // #region agent log
                try {
                    java.nio.file.Files.write(
                            java.nio.file.Paths
                                    .get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"),
                            ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1\",\"location\":\"DashboardController.java:38\",\"message\":\"critical FXML fields null, returning early\",\"timestamp\":"
                                    + System.currentTimeMillis() + "}\n").getBytes(),
                            java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
                } catch (Exception logEx) {
                }
                // #endregion
                System.err.println("Warning: Some FXML fields are null in DashboardController");
                return;
            }

            // #region agent log
            try {
                java.nio.file.Files.write(
                        java.nio.file.Paths
                                .get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"),
                        ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1\",\"location\":\"DashboardController.java:42\",\"message\":\"calling ShowData\",\"timestamp\":"
                                + System.currentTimeMillis() + "}\n").getBytes(),
                        java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            } catch (Exception logEx) {
            }
            // #endregion
            ShowData();
            initAccountSelector();
            // #region agent log
            try {
                java.nio.file.Files.write(
                        java.nio.file.Paths
                                .get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"),
                        ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1\",\"location\":\"DashboardController.java:44\",\"message\":\"after ShowData, calling initLatestTransactions\",\"timestamp\":"
                                + System.currentTimeMillis() + "}\n").getBytes(),
                        java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            } catch (Exception logEx) {
            }
            // #endregion
            initLatestTransactions();
            if (Transaction_list != null) {
                Transaction_list.setItems(Model.getInstance().getTransactions());
                Transaction_list.setCellFactory(e -> new TransactionCellFactory());
            }
            accountSummary();
            if (send_btn != null) {
                send_btn.setOnAction(event -> onSendMoney());
            }

            // Add animations on load
            Platform.runLater(() -> {
                animateDashboard();
            });

            // #region agent log
            try {
                java.nio.file.Files.write(
                        java.nio.file.Paths
                                .get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"),
                        ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1\",\"location\":\"DashboardController.java:54\",\"message\":\"initialize completed successfully\",\"timestamp\":"
                                + System.currentTimeMillis() + "}\n").getBytes(),
                        java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            } catch (Exception logEx) {
            }
            // #endregion
        } catch (Exception e) {
            // #region agent log
            try {
                java.nio.file.Files.write(
                        java.nio.file.Paths
                                .get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"),
                        ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H1\",\"location\":\"DashboardController.java:56\",\"message\":\"initialize exception\",\"timestamp\":"
                                + System.currentTimeMillis() + ",\"data\":{\"error\":\"" + e.getMessage()
                                + "\",\"class\":\"" + e.getClass().getName() + "\"}}\n").getBytes(),
                        java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            } catch (Exception logEx) {
            }
            // #endregion
            e.printStackTrace();
            System.err.println("Error initializing DashboardController: " + e.getMessage());
        }
    }

    private void ShowData() {
        // #region agent log
        try {
            java.nio.file.Files.write(
                    java.nio.file.Paths
                            .get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"),
                    ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H2\",\"location\":\"DashboardController.java:57\",\"message\":\"ShowData entry\",\"timestamp\":"
                            + System.currentTimeMillis() + "}\n").getBytes(),
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception logEx) {
        }
        // #endregion
        try {
            var model = Model.getInstance();
            var currentUser = model.getCurrentUser();
            // #region agent log
            try {
                java.nio.file.Files.write(
                        java.nio.file.Paths
                                .get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"),
                        ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H2\",\"location\":\"DashboardController.java:61\",\"message\":\"checking currentUser\",\"timestamp\":"
                                + System.currentTimeMillis() + ",\"data\":{\"currentUser_null\":"
                                + (currentUser == null) + ",\"userName_null\":" + (userName == null)
                                + ",\"login_date_null\":" + (login_date == null) + "}}\n").getBytes(),
                        java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            } catch (Exception logEx) {
            }
            // #endregion
            if (currentUser != null && userName != null && login_date != null) {

                // Set welcome message with user's name
                String fullName = currentUser.getFirstName() + " " + currentUser.getLastName();
                userName.setText("Welcome, " + currentUser.getFirstName() + "!");
                login_date.setText("Today, " + LocalDate.now());

                // Display full name
                if (fullName_label != null) {
                    fullName_label.setText(fullName);
                }

                // Display username
                if (username_label != null) {
                    username_label.setText(currentUser.getUserName());
                }

                // Display first account if available
                if (!currentUser.getBankAccounts().isEmpty()) {
                    var firstAccount = currentUser.getBankAccounts().get(0);

                    // Balance
                    if (checking_bal != null) {
                        checking_bal.setText("$" + String.format("%.2f", firstAccount.getBalance()));
                    }

                    // Account number
                    if (checking_acc_num != null) {
                        checking_acc_num.setText(firstAccount.getAccountNumber());
                    }

                    // Bank name
                    if (bank_name_label != null) {
                        bank_name_label.setText(firstAccount.getBankName());
                    }

                    // Account type
                    if (account_type_label != null) {
                        account_type_label.setText(firstAccount.getAccountType());
                    }
                } else {
                    // No accounts - set default values
                    if (checking_bal != null) {
                        checking_bal.setText("$0.00");
                    }
                    if (checking_acc_num != null) {
                        checking_acc_num.setText("No Account");
                    }
                    if (bank_name_label != null) {
                        bank_name_label.setText("-");
                    }
                    if (account_type_label != null) {
                        account_type_label.setText("-");
                    }
                }
            } else {
                // User not logged in - set default values
                if (userName != null) {
                    userName.setText("Hi, Guest");
                }
                if (login_date != null) {
                    login_date.setText("Today, " + LocalDate.now());
                }
                if (fullName_label != null) {
                    fullName_label.setText("-");
                }
                if (username_label != null) {
                    username_label.setText("-");
                }
                if (checking_bal != null) {
                    checking_bal.setText("$0.00");
                }
                if (checking_acc_num != null) {
                    checking_acc_num.setText("-");
                }
                if (bank_name_label != null) {
                    bank_name_label.setText("-");
                }
                if (account_type_label != null) {
                    account_type_label.setText("-");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in ShowData: " + e.getMessage());
        }
    }

    private void initLatestTransactions() {
        if (Model.getInstance().getCurrentUser() == null)
            return;

        // Load transactions for the first account (the one displayed)
        if (!Model.getInstance().getCurrentUser().getBankAccounts().isEmpty()) {
            String accountNumber = Model.getInstance().getCurrentUser().getBankAccounts().get(0).getAccountNumber();
            String username = Model.getInstance().getCurrentUser().getUserName();
            System.out.println(
                    "DEBUG initLatestTransactions: Loading for user=" + username + ", account=" + accountNumber);
            Model.getInstance().loadTransactionsByAccount(accountNumber, 4);
            System.out.println("DEBUG initLatestTransactions: Loaded " + Model.getInstance().getTransactions().size()
                    + " transactions");
            for (Transaction t : Model.getInstance().getTransactions()) {
                System.out.println("  - " + t.getSender() + " -> " + t.getReceiver() + " ($" + t.getAmount() + ")");
            }
        } else {
            // Fallback to all transactions if no accounts
            Model.getInstance().loadTransactions(4);
        }
    }

    private void accountSummary() {
        try {
            if (Model.getInstance().getCurrentUser() == null)
                return;

            double income = 0;
            double expenses = 0;

            // Calculate income/expenses for the displayed account (first account)
            if (!Model.getInstance().getCurrentUser().getBankAccounts().isEmpty()) {
                String accountNumber = Model.getInstance().getCurrentUser().getBankAccounts().get(0).getAccountNumber();
                Model.getInstance().loadTransactionsByAccount(accountNumber, -1);
            } else {
                // Fallback to all transactions if no accounts
                Model.getInstance().loadTransactions(-1);
            }

            for (Transaction transaction : Model.getInstance().getTransactions()) {
                if (transaction.getSender() != null
                        && transaction.getSender().equals(Model.getInstance().getCurrentUser().getUserName())) {
                    expenses += transaction.getAmount();
                } else if (transaction.getReceiver() != null
                        && transaction.getReceiver().equals(Model.getInstance().getCurrentUser().getUserName())) {
                    income += transaction.getAmount();
                }
            }

            if (income_bal != null) {
                income_bal.setText("+$" + String.format("%.2f", income));
            }
            if (expenses_bal != null) {
                expenses_bal.setText("-$" + String.format("%.2f", expenses));
            }

            // Reload limited transactions for Recent Activity display after calculating
            // summary
            if (!Model.getInstance().getCurrentUser().getBankAccounts().isEmpty()) {
                String accountNumber = Model.getInstance().getCurrentUser().getBankAccounts().get(0).getAccountNumber();
                Model.getInstance().loadTransactionsByAccount(accountNumber, 4);
            } else {
                Model.getInstance().loadTransactions(4);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSendMoney() {
        if (Model.getInstance().getCurrentUser() == null)
            return;

        String receiverIdentifier = username_fld.getText();
        if (receiverIdentifier.isEmpty()) {
            return;
        }

        try {
            double amount = Double.parseDouble(amount_fld.getText());
            String message = message_fld.getText();

            if (Model.getInstance().getCurrentUser().getBankAccounts().isEmpty()) {
                return;
            }

            String senderAccount = Model.getInstance().getCurrentUser().getBankAccounts().get(0).getAccountNumber();

            if (Model.getInstance().sendMoney(receiverIdentifier, senderAccount, amount, message, "INSTANT")) {
                username_fld.setText("");
                amount_fld.setText("");
                message_fld.setText("");
                ShowData();
                initLatestTransactions();
                accountSummary();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * Animate dashboard elements on load
     */
    private void animateDashboard() {
        // Animate welcome labels with staggered fade-in and slide-up
        if (userName != null) {
            userName.setOpacity(0);
            userName.setTranslateY(20);
            AnimationUtils.fadeInSlideUp(userName, 20, AnimationUtils.ENTRANCE_DURATION).play();
        }

        if (fullName_label != null) {
            fullName_label.setOpacity(0);
            fullName_label.setTranslateY(20);
            javafx.util.Duration delay = javafx.util.Duration.millis(200);
            javafx.animation.FadeTransition fade = AnimationUtils.fadeIn(fullName_label,
                    AnimationUtils.ENTRANCE_DURATION);
            javafx.animation.TranslateTransition slide = AnimationUtils.slideUp(fullName_label, 20,
                    AnimationUtils.ENTRANCE_DURATION);
            fade.setDelay(delay);
            slide.setDelay(delay);
            fade.play();
            slide.play();
        }

        if (login_date != null) {
            login_date.setOpacity(0);
            login_date.setTranslateY(20);
            javafx.util.Duration delay = javafx.util.Duration.millis(400);
            javafx.animation.FadeTransition fade = AnimationUtils.fadeIn(login_date, AnimationUtils.ENTRANCE_DURATION);
            javafx.animation.TranslateTransition slide = AnimationUtils.slideUp(login_date, 20,
                    AnimationUtils.ENTRANCE_DURATION);
            fade.setDelay(delay);
            slide.setDelay(delay);
            fade.play();
            slide.play();
        }
    }

    /**
     * Initialize account selector ComboBox
     */
    private void initAccountSelector() {
        if (account_selector == null || Model.getInstance().getCurrentUser() == null) {
            return;
        }

        // Clear existing items
        account_selector.getItems().clear();

        // Populate with user's accounts
        for (var account : Model.getInstance().getCurrentUser().getBankAccounts()) {
            String displayText = account.getAccountNumber() + " - " + account.getBankName() +
                    " (" + account.getAccountType() + ")";
            account_selector.getItems().add(displayText);
        }

        // Select first account by default
        if (!account_selector.getItems().isEmpty()) {
            account_selector.setValue(account_selector.getItems().get(0));
        }

        // Add listener for selection changes
        account_selector.setOnAction(event -> {
            String selected = account_selector.getValue();
            if (selected != null) {
                // Extract account number from display text
                String accountNumber = selected.split(" - ")[0];
                updateDashboardForAccount(accountNumber);
            }
        });
    }

    /**
     * Update dashboard data for selected account
     */
    private void updateDashboardForAccount(String accountNumber) {
        if (Model.getInstance().getCurrentUser() == null) {
            return;
        }

        // Find the selected account
        var selectedAccount = Model.getInstance().getCurrentUser().getBankAccounts().stream()
                .filter(acc -> acc.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElse(null);

        if (selectedAccount == null) {
            return;
        }

        // Update account card display
        if (checking_bal != null) {
            checking_bal.setText("$" + String.format("%.2f", selectedAccount.getBalance()));
        }
        if (checking_acc_num != null) {
            checking_acc_num.setText(selectedAccount.getAccountNumber());
        }
        if (bank_name_label != null) {
            bank_name_label.setText(selectedAccount.getBankName());
        }
        if (account_type_label != null) {
            account_type_label.setText(selectedAccount.getAccountType());
        }

        // Reload transactions for this account
        Model.getInstance().loadTransactionsByAccount(accountNumber, 4);
        if (Transaction_list != null) {
            Transaction_list.setItems(Model.getInstance().getTransactions());
        }

        // Recalculate income/expenses for this account
        double income = 0;
        double expenses = 0;
        Model.getInstance().loadTransactionsByAccount(accountNumber, -1);
        for (var transaction : Model.getInstance().getTransactions()) {
            if (transaction.getSender() != null &&
                    transaction.getSender().equals(Model.getInstance().getCurrentUser().getUserName())) {
                expenses += transaction.getAmount();
            } else if (transaction.getReceiver() != null &&
                    transaction.getReceiver().equals(Model.getInstance().getCurrentUser().getUserName())) {
                income += transaction.getAmount();
            }
        }

        if (income_bal != null) {
            income_bal.setText("+$" + String.format("%.2f", income));
        }
        if (expenses_bal != null) {
            expenses_bal.setText("-$" + String.format("%.2f", expenses));
        }

        // Reload limited transactions for display
        Model.getInstance().loadTransactionsByAccount(accountNumber, 4);
        if (Transaction_list != null) {
            Transaction_list.setItems(Model.getInstance().getTransactions());
        }
    }
}
