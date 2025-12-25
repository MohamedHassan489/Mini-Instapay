package com.example.national_bank_of_egypt.Models;

import com.example.national_bank_of_egypt.Exceptions.RepositoryException;
import com.example.national_bank_of_egypt.Exceptions.TransactionException;
import com.example.national_bank_of_egypt.Repository.BankAccountRepository;
import com.example.national_bank_of_egypt.Repository.BankAccountRepositoryImpl;
import com.example.national_bank_of_egypt.Repository.TransactionRepository;
import com.example.national_bank_of_egypt.Repository.TransactionRepositoryImpl;
import com.example.national_bank_of_egypt.Repository.UserRepository;
import com.example.national_bank_of_egypt.Repository.UserRepositoryImpl;
import com.example.national_bank_of_egypt.Views.ViewFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.Optional;

public class Model {
    private static Model model;
    private final ViewFactory viewFactory;
    private final DataBaseDriver dataBaseDriver;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final ObservableList<User> users;
    private User currentUser;
    private final ObservableList<Transaction> transactions;
    private final ObservableList<Dispute> disputes;
    private Boolean userLoginSuccessFlag;
    private Boolean adminLoginSuccessFlag;
    private String lastErrorMessage; // Store last error for UI display
    
    // System Uptime Tracking
    private final long systemStartTime;
    private long totalDowntimeMillis;
    private int downtimeEventCount;
    private boolean systemOnline;

    private Model() {
        this.viewFactory = new ViewFactory();
        this.dataBaseDriver = new DataBaseDriver();
        this.userRepository = new UserRepositoryImpl(dataBaseDriver);
        this.transactionRepository = new TransactionRepositoryImpl(dataBaseDriver);
        this.bankAccountRepository = new BankAccountRepositoryImpl(dataBaseDriver);
        this.userLoginSuccessFlag = false;
        this.currentUser = null;
        this.transactions = FXCollections.observableArrayList();
        this.disputes = FXCollections.observableArrayList();
        this.adminLoginSuccessFlag = false;
        this.users = FXCollections.observableArrayList();
        this.lastErrorMessage = null;
        
        // Initialize uptime tracking
        this.systemStartTime = System.currentTimeMillis();
        this.totalDowntimeMillis = 0;
        this.downtimeEventCount = 0;
        this.systemOnline = true;
    }

    public static synchronized Model getInstance() {
        if (model == null) {
            model = new Model();
        }
        return model;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }

    public DataBaseDriver getDataBaseDriver() {
        return dataBaseDriver;
    }

    public Boolean getUserLoginSuccessFlag() {
        return this.userLoginSuccessFlag;
    }

    public void setUserLoginSuccessFlag(Boolean flag) {
        this.userLoginSuccessFlag = flag;
    }

    public Boolean getAdminLoginSuccessFlag() {
        return this.adminLoginSuccessFlag;
    }

    public void setAdminLoginSuccessFlag(Boolean flag) {
        this.adminLoginSuccessFlag = flag;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public String getLastErrorMessage() {
        return lastErrorMessage;
    }
    
    public void clearLastError() {
        this.lastErrorMessage = null;
    }
    
    private void setLastError(String message) {
        this.lastErrorMessage = message;
    }

    public void evaluateUserCred(String userName, String password) {
        // Reset login flag
        this.userLoginSuccessFlag = false;
        this.currentUser = null;

        if (userName == null || userName.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return;
        }

        ResultSet resultSet = dataBaseDriver.getUserData(userName, password);
        // #region agent log
        try {
            java.nio.file.Files.write(
                    java.nio.file.Paths
                            .get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"),
                    ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H4\",\"location\":\"Model.java:80\",\"message\":\"getUserData ResultSet created\",\"timestamp\":"
                            + System.currentTimeMillis() + ",\"data\":{\"userName\":\"" + userName
                            + "\",\"resultSetNull\":" + (resultSet == null) + "}}\n").getBytes(),
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception logEx) {
        }
        // #endregion
        try {
            if (resultSet != null && resultSet.next()) {
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                String email = resultSet.getString("Email");
                String phoneNumber = resultSet.getString("PhoneNumber");
                String address = resultSet.getString("Address");
                String dateCreatedStr = resultSet.getString("DateCreated");
                String twoFactorEnabled = resultSet.getString("TwoFactorEnabled");

                // Check if account is suspended
                if ("suspended".equalsIgnoreCase(twoFactorEnabled)) {
                    this.userLoginSuccessFlag = false;
                    this.currentUser = null;
                    return;
                }

                if (dateCreatedStr != null && !dateCreatedStr.isEmpty()) {
                    String[] dateParts = dateCreatedStr.split("-");
                    if (dateParts.length == 3) {
                        LocalDate dateCreated = LocalDate.of(
                                Integer.parseInt(dateParts[0]),
                                Integer.parseInt(dateParts[1]),
                                Integer.parseInt(dateParts[2]));

                        User user = new User(firstName, lastName, email, phoneNumber, address, userName, password,
                                dateCreated);
                        if (twoFactorEnabled != null && "true".equalsIgnoreCase(twoFactorEnabled)) {
                            user.twoFactorEnabledProperty().set("true");
                        }
                        loadUserBankAccounts(user);
                        this.currentUser = user;

                        // Load notifications for the user
                        com.example.national_bank_of_egypt.Notifications.NotificationService.getInstance()
                                .loadUserNotifications(userName);

                        // Don't set login success flag yet - need OTP verification if 2FA is enabled
                        if (twoFactorEnabled == null || !"true".equalsIgnoreCase(twoFactorEnabled)) {
                            this.userLoginSuccessFlag = true;
                        } else {
                            this.userLoginSuccessFlag = false; // Will be set to true after OTP verification
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.userLoginSuccessFlag = false;
            this.currentUser = null;
        } finally {
            // #region agent log
            try {
                if (resultSet != null) {
                    resultSet.close();
                    java.nio.file.Files.write(
                            java.nio.file.Paths
                                    .get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"),
                            ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H4\",\"location\":\"Model.java:132\",\"message\":\"getUserData ResultSet closed\",\"timestamp\":"
                                    + System.currentTimeMillis() + "}\n").getBytes(),
                            java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
                }
            } catch (Exception closeEx) {
                try {
                    java.nio.file.Files.write(
                            java.nio.file.Paths
                                    .get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"),
                            ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H4\",\"location\":\"Model.java:135\",\"message\":\"getUserData ResultSet close failed\",\"timestamp\":"
                                    + System.currentTimeMillis() + ",\"data\":{\"error\":\"" + closeEx.getMessage()
                                    + "\"}}\n").getBytes(),
                            java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
                } catch (Exception logEx) {
                }
            }
            // #endregion
        }
    }

    private void loadUserBankAccounts(User user) {
        user.getBankAccounts().clear();
        try {
            java.util.List<BankAccount> accounts = bankAccountRepository.findByOwner(user.getUserName());
            user.getBankAccounts().addAll(accounts);
        } catch (RepositoryException e) {
            setLastError("Failed to load bank accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void reloadUserBankAccounts() {
        if (currentUser != null) {
            loadUserBankAccounts(currentUser);
        }
    }

    public void evaluateAdminCred(String userName, String password) {
        // Reset login flag
        this.adminLoginSuccessFlag = false;

        if (userName == null || userName.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return;
        }

        ResultSet resultSet = dataBaseDriver.getAdminData(userName, password);
        try {
            if (resultSet != null && resultSet.next()) {
                this.adminLoginSuccessFlag = true;
            } else {
                this.adminLoginSuccessFlag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.adminLoginSuccessFlag = false;
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    public boolean registerUser(String firstName, String lastName, String email, String phoneNumber,
            String address, String userName, String password) {
        try {
            clearLastError();
            
            // Validate input
            if (firstName == null || firstName.trim().isEmpty()) {
                setLastError("First name is required.");
                return false;
            }
            if (lastName == null || lastName.trim().isEmpty()) {
                setLastError("Last name is required.");
                return false;
            }
            if (email == null || email.trim().isEmpty()) {
                setLastError("Email is required.");
                return false;
            }
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                setLastError("Phone number is required.");
                return false;
            }
            if (userName == null || userName.trim().isEmpty()) {
                setLastError("Username is required.");
                return false;
            }
            if (password == null || password.trim().isEmpty()) {
                setLastError("Password is required.");
                return false;
            }
            
            // Check if username already exists
            if (userRepository.existsByUserName(userName)) {
                setLastError("Username already exists. Please choose another.");
                return false;
            }
            // Check if phone number already exists
            if (userRepository.existsByPhoneNumber(phoneNumber)) {
                setLastError("Phone number is already registered.");
                return false;
            }
            // Check if email already exists
            if (dataBaseDriver.emailExists(email)) {
                setLastError("Email is already registered.");
                return false;
            }

            // Create user
            User newUser = new User(firstName, lastName, email, phoneNumber, address, userName, password, LocalDate.now());
            boolean success = userRepository.save(newUser);

            if (success) {
                // Create default transaction limits for new user (daily: $5000, weekly: $20000)
                dataBaseDriver.createTransactionLimit(userName, 5000.0, 20000.0);
            } else {
                setLastError("Failed to create user account. Please try again.");
            }

            return success;
        } catch (RepositoryException e) {
            setLastError("Registration failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            setLastError("An unexpected error occurred during registration.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUserProfile(String firstName, String lastName, String email, String phoneNumber,
            String address) {
        clearLastError();
        
        if (currentUser == null) {
            setLastError("No user is logged in.");
            return false;
        }
        
        // Validate inputs
        if (firstName == null || firstName.trim().isEmpty()) {
            setLastError("First name is required.");
            return false;
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            setLastError("Last name is required.");
            return false;
        }
        if (email == null || email.trim().isEmpty()) {
            setLastError("Email is required.");
            return false;
        }
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            setLastError("Phone number is required.");
            return false;
        }
        
        // Update the current user object first
        currentUser.firstNameProperty().set(firstName);
        currentUser.lastNameProperty().set(lastName);
        currentUser.emailProperty().set(email);
        currentUser.phoneNumberProperty().set(phoneNumber);
        currentUser.addressProperty().set(address);
        
        boolean success = userRepository.update(currentUser);
        if (!success) {
            setLastError("Failed to update profile. Please try again.");
        }
        return success;
    }

    public boolean addBankAccount(String accountNumber, String bankName, double balance, String accountType) {
        try {
            clearLastError();
            
            if (currentUser == null) {
                setLastError("No user is logged in.");
                return false;
            }
                
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                setLastError("Account number is required.");
                return false;
            }
            if (bankName == null || bankName.trim().isEmpty()) {
                setLastError("Bank name is required.");
                return false;
            }
            if (balance < 0) {
                setLastError("Balance cannot be negative.");
                return false;
            }
            
            BankAccount newAccount = new BankAccount(currentUser.getUserName(), accountNumber, bankName, balance, accountType);
            boolean success = bankAccountRepository.save(newAccount);
            
            if (success) {
                // Reload from database to ensure consistency
                loadUserBankAccounts(currentUser);
            } else {
                setLastError("Failed to add bank account. Please try again.");
            }
            
            return success;
        } catch (RepositoryException e) {
            setLastError("Failed to add bank account: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            setLastError("An unexpected error occurred while adding bank account.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeBankAccount(String accountNumber) {
        clearLastError();
        
        if (currentUser == null) {
            setLastError("No user is logged in.");
            return false;
        }
        
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            setLastError("Account number is required.");
            return false;
        }
        
        // Check if it's the last account
        if (currentUser.getBankAccounts() != null && currentUser.getBankAccounts().size() <= 1) {
            setLastError("Cannot remove the last bank account. You must have at least one account.");
            return false;
        }
        
        try {
            boolean success = bankAccountRepository.delete(accountNumber);
            if (success) {
                // Reload from database to ensure consistency
                loadUserBankAccounts(currentUser);
            } else {
                setLastError("Failed to remove bank account. Please try again.");
            }
            return success;
        } catch (RepositoryException e) {
            setLastError("Failed to remove bank account: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            setLastError("An unexpected error occurred while removing bank account.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBankAccount(String accountNumber, String bankName, String accountType) {
        clearLastError();
        
        if (currentUser == null) {
            setLastError("No user is logged in.");
            return false;
        }
        
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            setLastError("Account number is required.");
            return false;
        }
        
        try {
            boolean success = dataBaseDriver.updateBankAccount(accountNumber, bankName, accountType);
            if (success) {
                // Reload from database to ensure consistency
                loadUserBankAccounts(currentUser);
            } else {
                setLastError("Failed to update bank account. Please try again.");
            }
            return success;
        } catch (Exception e) {
            setLastError("An unexpected error occurred while updating bank account.");
            e.printStackTrace();
            return false;
        }
    }

    public void loadTransactions(int limit) {
        try {
            if (currentUser == null)
                return;
            clearLastError();
            transactions.clear();
            java.util.List<Transaction> transactionList = transactionRepository.findByUserId(currentUser.getUserName(), limit);
            transactions.addAll(transactionList);
        } catch (RepositoryException e) {
            setLastError("Failed to load transactions: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            setLastError("An unexpected error occurred while loading transactions.");
            e.printStackTrace();
        }
    }

    public void loadTransactionsByAccount(String accountNumber, int limit) {
        if (currentUser == null || accountNumber == null || accountNumber.isEmpty())
            return;
        transactions.clear();
        ResultSet rs = dataBaseDriver.getTransactionsByAccount(currentUser.getUserName(), accountNumber, limit);
        try {
            if (rs != null) {
                while (rs.next()) {
                    String transactionId = rs.getString("TransactionID");
                    String sender = rs.getString("Sender");
                    String receiver = rs.getString("Receiver");
                    String senderAccount = rs.getString("SenderAccount");
                    String receiverAccount = rs.getString("ReceiverAccount");
                    double amount = rs.getDouble("Amount");
                    String[] dateParts = rs.getString("Date").split("-");
                    LocalDate date = LocalDate.of(
                            Integer.parseInt(dateParts[0]),
                            Integer.parseInt(dateParts[1]),
                            Integer.parseInt(dateParts[2]));
                    String message = rs.getString("Message");
                    String status = rs.getString("Status");
                    String transactionType = rs.getString("TransactionType");

                    Transaction transaction = new Transaction(transactionId, sender, receiver, senderAccount,
                            receiverAccount, amount, date, message, status, transactionType);
                    transactions.add(transaction);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    public ObservableList<Transaction> getTransactions() {
        return transactions;
    }

    public void loadAllUsers() {
        ResultSet rs = null;
        try {
            // Create temporary list to avoid clearing while being accessed
            ObservableList<User> tempUsers = FXCollections.observableArrayList();
            rs = dataBaseDriver.getAllUsers();
            if (rs != null) {
                while (rs.next()) {
                    String firstName = rs.getString("FirstName");
                    String lastName = rs.getString("LastName");
                    String email = rs.getString("Email");
                    String phoneNumber = rs.getString("PhoneNumber");
                    String address = rs.getString("Address");
                    String userName = rs.getString("UserName");
                    String password = rs.getString("Password");
                    String twoFactorEnabled = rs.getString("TwoFactorEnabled");
                    String[] dateParts = rs.getString("DateCreated").split("-");
                    LocalDate dateCreated = LocalDate.of(
                            Integer.parseInt(dateParts[0]),
                            Integer.parseInt(dateParts[1]),
                            Integer.parseInt(dateParts[2]));
                    User user = new User(firstName, lastName, email, phoneNumber, address, userName, password,
                            dateCreated);
                    if (twoFactorEnabled != null) {
                        if ("suspended".equalsIgnoreCase(twoFactorEnabled)) {
                            continue; // Skip suspended users
                        }
                        if ("true".equalsIgnoreCase(twoFactorEnabled)) {
                            user.twoFactorEnabledProperty().set("true");
                        }
                    }
                    loadUserBankAccounts(user);
                    tempUsers.add(user);
                }
            }
            // Replace all items at once using setAll() instead of clear() to avoid
            // IndexOutOfBoundsException
            // This is thread-safe and avoids issues with FilteredList
            users.setAll(tempUsers);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    public ObservableList<User> getUsers() {
        return users;
    }

    public void loadDisputes() {
        if (currentUser == null)
            return;
        disputes.clear();
        ResultSet rs = dataBaseDriver.getDisputes(currentUser.getUserName());
        try {
            if (rs != null) {
                while (rs.next()) {
                    String disputeId = rs.getString("DisputeID");
                    String transactionId = rs.getString("TransactionID");
                    String userId = rs.getString("UserId");
                    String reason = rs.getString("Reason");
                    String status = rs.getString("Status");
                    String[] dateParts = rs.getString("DateCreated").split("-");
                    LocalDate dateCreated = LocalDate.of(
                            Integer.parseInt(dateParts[0]),
                            Integer.parseInt(dateParts[1]),
                            Integer.parseInt(dateParts[2]));
                    Dispute dispute = new Dispute(disputeId, transactionId, userId, reason, status, dateCreated);
                    if (rs.getString("Resolution") != null) {
                        dispute.setResolution(rs.getString("Resolution"));
                    }
                    disputes.add(dispute);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    public ObservableList<Dispute> getDisputes() {
        return disputes;
    }

    public TransactionLimit getTransactionLimit(String userName) {
        ResultSet rs = dataBaseDriver.getTransactionLimit(userName);
        try {
            if (rs != null && rs.next()) {
                double dailyLimit = rs.getDouble("DailyLimit");
                double weeklyLimit = rs.getDouble("WeeklyLimit");
                double dailyUsed = rs.getDouble("DailyUsed");
                double weeklyUsed = rs.getDouble("WeeklyUsed");
                TransactionLimit limit = new TransactionLimit(userName, dailyLimit, weeklyLimit);
                limit.dailyUsedProperty().set(dailyUsed);
                limit.weeklyUsedProperty().set(weeklyUsed);
                return limit;
            } else {
                TransactionLimit limit = new TransactionLimit(userName, 5000.0, 20000.0);
                dataBaseDriver.createTransactionLimit(userName, 5000.0, 20000.0);
                return limit;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new TransactionLimit(userName, 5000.0, 20000.0);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    public boolean updateTransactionLimits(String userName, double dailyLimit, double weeklyLimit) {
        return dataBaseDriver.updateTransactionLimit(userName, dailyLimit, weeklyLimit);
    }

    public boolean sendMoney(String receiverIdentifier, String senderAccount, double amount, String message,
            String transactionType) {
        return sendMoney(receiverIdentifier, senderAccount, amount, message, transactionType, null);
    }

    public boolean sendMoney(String receiverIdentifier, String senderAccount, double amount, String message,
            String transactionType, LocalDate scheduledDate) {
        clearLastError();
        
        if (currentUser == null) {
            setLastError("You must be logged in to send money.");
            return false;
        }

        ResultSet receiverRs = null;
        String receiver = null;
        String receiverAccount = null;

        // Try to find receiver by different identifiers
        if (receiverIdentifier.startsWith("@")) {
            // Username format: @username
            String username = receiverIdentifier.substring(1);
            receiverRs = dataBaseDriver.getUserData(username, "");
        } else if (receiverIdentifier.contains("@")) {
            // Email format
            receiverRs = dataBaseDriver.getUserByEmail(receiverIdentifier);
        } else if (receiverIdentifier.matches("\\d+")) {
            // Numeric: could be phone number or account number
            if (receiverIdentifier.length() == 10) {
                // 10 digits = phone number
                receiverRs = dataBaseDriver.getUserByPhoneNumber(receiverIdentifier);
            } else {
                // Account number
                receiverRs = dataBaseDriver.getUserByAccountNumber(receiverIdentifier);
                if (receiverRs != null) {
                    try {
                        if (receiverRs.next()) {
                            receiverAccount = receiverIdentifier;
                            receiverRs.beforeFirst(); // Reset cursor
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            // Try as username
            receiverRs = dataBaseDriver.getUserData(receiverIdentifier, "");
        }

        if (receiverRs != null) {
            try {
                if (receiverRs.next()) {
                    receiver = receiverRs.getString("UserName");
                    if (receiverAccount == null) {
                        ResultSet accRs = dataBaseDriver.getBankAccounts(receiver);
                        try {
                            if (accRs != null && accRs.next()) {
                                receiverAccount = accRs.getString("AccountNumber");
                            }
                        } finally {
                            try {
                                if (accRs != null) {
                                    accRs.close();
                                }
                            } catch (Exception closeEx) {
                                closeEx.printStackTrace();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (receiverRs != null) {
                        receiverRs.close();
                    }
                } catch (Exception closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }

        if (receiver == null || receiverAccount == null) {
            setLastError("Receiver not found. Please check the username, phone number, email, or account number.");
            return false;
        }
        
        // Check if trying to send to self
        if (receiver.equals(currentUser.getUserName())) {
            setLastError("You cannot send money to yourself.");
            return false;
        }

        TransactionLimit limit = getTransactionLimit(currentUser.getUserName());

        // Check if limit is approaching (80% threshold)
        if (limit.isDailyLimitApproaching(amount)) {
            double remaining = limit.getDailyLimitRemaining() - amount;
            com.example.national_bank_of_egypt.Notifications.NotificationService.getInstance()
                    .sendNotification(currentUser.getUserName(), "Daily Limit Approaching",
                            String.format("You are approaching your daily limit. Remaining: $%.2f", remaining),
                            "LIMIT");
        }

        if (limit.isWeeklyLimitApproaching(amount)) {
            double remaining = limit.getWeeklyLimitRemaining() - amount;
            com.example.national_bank_of_egypt.Notifications.NotificationService.getInstance()
                    .sendNotification(currentUser.getUserName(), "Weekly Limit Approaching",
                            String.format("You are approaching your weekly limit. Remaining: $%.2f", remaining),
                            "LIMIT");
        }

        // Check if limit is exceeded
        if (limit.isDailyLimitExceeded(amount)) {
            double remaining = limit.getDailyLimitRemaining();
            setLastError(String.format("Daily transaction limit exceeded! You can only send $%.2f more today. Your daily limit is $%.2f.", 
                remaining, limit.getDailyLimit()));
            com.example.national_bank_of_egypt.Notifications.NotificationService.getInstance()
                    .sendNotification(currentUser.getUserName(), "Transaction Limit Exceeded",
                            "Your transaction exceeds your daily limit", "LIMIT");
            return false;
        }
        
        if (limit.isWeeklyLimitExceeded(amount)) {
            double remaining = limit.getWeeklyLimitRemaining();
            setLastError(String.format("Weekly transaction limit exceeded! You can only send $%.2f more this week. Your weekly limit is $%.2f.", 
                remaining, limit.getWeeklyLimit()));
            com.example.national_bank_of_egypt.Notifications.NotificationService.getInstance()
                    .sendNotification(currentUser.getUserName(), "Transaction Limit Exceeded",
                            "Your transaction exceeds your weekly limit", "LIMIT");
            return false;
        }

        // Handle scheduled transactions differently - don't transfer money immediately
        if ("SCHEDULED".equals(transactionType) && scheduledDate != null) {
            // For scheduled transactions, just create the transaction record
            // Don't transfer money or check balance yet
            Transaction transaction = com.example.national_bank_of_egypt.Transactions.TransactionFactory
                    .createScheduledTransaction(currentUser.getUserName(), receiver, senderAccount,
                            receiverAccount, amount, scheduledDate, message);

            if (dataBaseDriver.createTransaction(transaction.getTransactionId(), transaction.getSender(),
                    transaction.getReceiver(), transaction.getSenderAccount(), transaction.getReceiverAccount(),
                    transaction.getAmount(), transaction.getDate(), transaction.getMessage(),
                    "SCHEDULED", transaction.getTransactionType())) {

                // Send notification about scheduled transaction
                com.example.national_bank_of_egypt.Notifications.NotificationService.getInstance()
                        .sendNotification(currentUser.getUserName(), "Transaction Scheduled",
                                "Your transaction of $" + amount + " to " + receiver + " is scheduled for "
                                        + scheduledDate,
                                "TRANSACTION");

                return true;
            }
            return false;
        }

        // For instant transactions, proceed with balance check and transfer
        ResultSet senderAccRs = dataBaseDriver.getBankAccountByNumber(senderAccount);
        if (senderAccRs == null) {
            setLastError("Source account not found. Please select a valid account.");
            return false;
        }

        try {
            if (senderAccRs.next()) {
                double senderBalance = senderAccRs.getDouble("Balance");
                if (senderBalance < amount) {
                    setLastError(String.format("Insufficient balance! You have $%.2f but tried to send $%.2f.", 
                        senderBalance, amount));
                    return false;
                }

                double newSenderBalance = senderBalance - amount;
                dataBaseDriver.updateBankAccountBalance(senderAccount, newSenderBalance);

                ResultSet receiverAccRs = dataBaseDriver.getBankAccountByNumber(receiverAccount);
                try {
                    if (receiverAccRs != null && receiverAccRs.next()) {
                        double receiverBalance = receiverAccRs.getDouble("Balance");
                        double newReceiverBalance = receiverBalance + amount;
                        dataBaseDriver.updateBankAccountBalance(receiverAccount, newReceiverBalance);
                    }
                } finally {
                    try {
                        if (receiverAccRs != null) {
                            receiverAccRs.close();
                        }
                    } catch (Exception closeEx) {
                        closeEx.printStackTrace();
                    }
                }

                // Create instant transaction
                Transaction transaction = com.example.national_bank_of_egypt.Transactions.TransactionFactory
                        .createInstantTransaction(currentUser.getUserName(), receiver, senderAccount,
                                receiverAccount, amount, message);

                // Fraud Detection - Check for suspicious activity before processing
                com.example.national_bank_of_egypt.Security.FraudDetectionService fraudService = com.example.national_bank_of_egypt.Security.FraudDetectionService
                        .getInstance();

                // Get recent transactions for pattern analysis
                java.util.List<Transaction> recentTransactions = new java.util.ArrayList<>();
                ResultSet recentRs = null;
                try {
                    recentRs = dataBaseDriver.getTransactions(currentUser.getUserName(), 20);
                    if (recentRs != null) {
                        while (recentRs.next()) {
                            // Convert String date to LocalDate
                            String dateStr = recentRs.getString("Date");
                            LocalDate transactionDate = null;
                            try {
                                transactionDate = LocalDate.parse(dateStr);
                            } catch (Exception dateEx) {
                                // If parsing fails, use today's date as fallback
                                transactionDate = LocalDate.now();
                            }

                            Transaction recentTxn = new Transaction(
                                    recentRs.getString("TransactionID"),
                                    recentRs.getString("Sender"),
                                    recentRs.getString("Receiver"),
                                    recentRs.getString("SenderAccount"),
                                    recentRs.getString("ReceiverAccount"),
                                    recentRs.getDouble("Amount"),
                                    transactionDate,
                                    recentRs.getString("Message"),
                                    recentRs.getString("Status"),
                                    recentRs.getString("TransactionType"));
                            recentTransactions.add(recentTxn);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Assess risk using new risk-based system
                com.example.national_bank_of_egypt.Security.FraudRiskResult riskResult = fraudService
                        .assessRisk(transaction, currentUser.getUserName(), recentTransactions);

                String transactionStatus;
                String riskFactorsDescription = riskResult.getRiskFactors().stream()
                        .map(f -> f.getDescription())
                        .reduce((a, b) -> a + "; " + b)
                        .orElse("No specific factors");

                // Handle based on risk level
                if (riskResult.shouldBlock()) {
                    // CRITICAL RISK - Block transaction
                    transactionStatus = "BLOCKED";
                    String notificationMessage = String.format(
                            "Transaction BLOCKED for security. Risk Score: %d/100. Reasons: %s",
                            riskResult.getRiskScore(), riskFactorsDescription);

                    com.example.national_bank_of_egypt.Notifications.NotificationService.getInstance()
                            .sendNotification(currentUser.getUserName(), "Transaction Blocked",
                                    notificationMessage, "FRAUD");

                    com.example.national_bank_of_egypt.Notifications.NotificationService.getInstance()
                            .sendNotification("admin", "CRITICAL: Transaction Blocked",
                                    String.format("Transaction %s from %s for $%.2f was BLOCKED. Risk Score: %d. %s",
                                            transaction.getTransactionId(), currentUser.getUserName(), amount,
                                            riskResult.getRiskScore(), riskFactorsDescription),
                                    "FRAUD");

                    return false; // Block the transaction

                } else if (riskResult.requiresConfirmation()) {
                    // HIGH RISK - Flag as suspicious but process
                    transactionStatus = "SUSPICIOUS";
                    String notificationMessage = String.format(
                            "Transaction flagged for review. Risk Score: %d/100. Please verify this transaction is legitimate.",
                            riskResult.getRiskScore());

                    com.example.national_bank_of_egypt.Notifications.NotificationService.getInstance()
                            .sendNotification(currentUser.getUserName(), "Transaction Requires Confirmation",
                                    notificationMessage, "FRAUD");

                    com.example.national_bank_of_egypt.Notifications.NotificationService.getInstance()
                            .sendNotification("admin", "High Risk Transaction Alert",
                                    String.format("Transaction %s from %s for $%.2f flagged. Risk Score: %d. %s",
                                            transaction.getTransactionId(), currentUser.getUserName(), amount,
                                            riskResult.getRiskScore(), riskFactorsDescription),
                                    "FRAUD");

                } else if (riskResult.isSuspicious()) {
                    // MEDIUM RISK - Flag but process
                    transactionStatus = "SUSPICIOUS";

                    com.example.national_bank_of_egypt.Notifications.NotificationService.getInstance()
                            .sendNotification(currentUser.getUserName(), "Suspicious Transaction Detected",
                                    "Your transaction of $" + amount
                                            + " has been flagged for review. Please contact support if this is legitimate.",
                                    "FRAUD");

                    com.example.national_bank_of_egypt.Notifications.NotificationService.getInstance()
                            .sendNotification("admin", "Suspicious Transaction Alert",
                                    String.format("Transaction %s from %s for $%.2f flagged. Risk Score: %d.",
                                            transaction.getTransactionId(), currentUser.getUserName(), amount,
                                            riskResult.getRiskScore()),
                                    "FRAUD");

                } else {
                    // LOW RISK - Process normally
                    transactionStatus = "SUCCESS";
                }

                dataBaseDriver.createTransaction(transaction.getTransactionId(), transaction.getSender(),
                        transaction.getReceiver(), transaction.getSenderAccount(), transaction.getReceiverAccount(),
                        transaction.getAmount(), transaction.getDate(), transaction.getMessage(),
                        transactionStatus, transaction.getTransactionType());

                limit.addToDailyUsed(amount);
                limit.addToWeeklyUsed(amount);
                dataBaseDriver.updateTransactionLimitUsage(currentUser.getUserName(),
                        limit.getDailyUsed(), limit.getWeeklyUsed());

                com.example.national_bank_of_egypt.Notifications.NotificationService.getInstance()
                        .sendNotification(currentUser.getUserName(), "Transaction Successful",
                                "You sent $" + amount + " to " + receiver, "TRANSACTION");
                com.example.national_bank_of_egypt.Notifications.NotificationService.getInstance()
                        .sendNotification(receiver, "Money Received",
                                "You received $" + amount + " from " + currentUser.getUserName(), "TRANSACTION");

                loadUserBankAccounts(currentUser);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (senderAccRs != null) {
                    senderAccRs.close();
                }
            } catch (Exception closeEx) {
                closeEx.printStackTrace();
            }
        }

        return false;
    }

    // Admin methods
    public void loadAllTransactions() {
        transactions.clear();
        ResultSet rs = dataBaseDriver.getAllTransactions();
        try {
            if (rs != null) {
                while (rs.next()) {
                    String transactionId = rs.getString("TransactionID");
                    String sender = rs.getString("Sender");
                    String receiver = rs.getString("Receiver");
                    String senderAccount = rs.getString("SenderAccount");
                    String receiverAccount = rs.getString("ReceiverAccount");
                    double amount = rs.getDouble("Amount");
                    String[] dateParts = rs.getString("Date").split("-");
                    LocalDate date = LocalDate.of(
                            Integer.parseInt(dateParts[0]),
                            Integer.parseInt(dateParts[1]),
                            Integer.parseInt(dateParts[2]));
                    String message = rs.getString("Message");
                    String status = rs.getString("Status");
                    String transactionType = rs.getString("TransactionType");

                    Transaction transaction = new Transaction(transactionId, sender, receiver, senderAccount,
                            receiverAccount, amount, date, message, status, transactionType);
                    transactions.add(transaction);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    public void loadAllDisputes() {
        disputes.clear();
        ResultSet rs = dataBaseDriver.getAllDisputes();
        try {
            if (rs != null) {
                while (rs.next()) {
                    String disputeId = rs.getString("DisputeID");
                    String transactionId = rs.getString("TransactionID");
                    String userId = rs.getString("UserId");
                    String reason = rs.getString("Reason");
                    String status = rs.getString("Status");
                    String[] dateParts = rs.getString("DateCreated").split("-");
                    LocalDate dateCreated = LocalDate.of(
                            Integer.parseInt(dateParts[0]),
                            Integer.parseInt(dateParts[1]),
                            Integer.parseInt(dateParts[2]));
                    Dispute dispute = new Dispute(disputeId, transactionId, userId, reason, status, dateCreated);
                    if (rs.getString("Resolution") != null) {
                        dispute.setResolution(rs.getString("Resolution"));
                    }
                    disputes.add(dispute);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    public ObservableList<Dispute> getAllDisputes() {
        return disputes;
    }

    public boolean flagTransactionAsSuspicious(String transactionId) {
        return dataBaseDriver.updateTransactionStatus(transactionId, "SUSPICIOUS");
    }

    public boolean resolveDispute(String disputeId, String resolution) {
        clearLastError();
        
        if (disputeId == null || disputeId.trim().isEmpty()) {
            setLastError("Dispute ID is required.");
            return false;
        }
        if (resolution == null || resolution.trim().isEmpty()) {
            setLastError("Resolution description is required.");
            return false;
        }
        
        try {
            boolean success = dataBaseDriver.resolveDispute(disputeId, resolution);
            if (!success) {
                setLastError("Failed to resolve dispute. Please try again.");
            }
            return success;
        } catch (Exception e) {
            setLastError("An error occurred while resolving dispute: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDisputeStatus(String disputeId, String status, String resolution) {
        clearLastError();
        
        try {
            boolean success = dataBaseDriver.updateDisputeStatus(disputeId, status, resolution);
            if (!success) {
                setLastError("Failed to update dispute status.");
            }
            return success;
        } catch (Exception e) {
            setLastError("An error occurred while updating dispute status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean suspendAccount(String userName) {
        clearLastError();
        
        if (userName == null || userName.trim().isEmpty()) {
            setLastError("Username is required.");
            return false;
        }
        
        try {
            boolean success = dataBaseDriver.suspendUser(userName);
            if (!success) {
                setLastError("Failed to suspend account. User may not exist or is already suspended.");
            }
            return success;
        } catch (Exception e) {
            setLastError("An error occurred while suspending account: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // ==================== SYSTEM UPTIME TRACKING ====================
    
    /**
     * Get the system start time (when application was launched)
     */
    public long getSystemStartTime() {
        return systemStartTime;
    }
    
    /**
     * Get total time the system has been running (in milliseconds)
     */
    public long getTotalRunTimeMillis() {
        return System.currentTimeMillis() - systemStartTime;
    }
    
    /**
     * Get total downtime (in milliseconds)
     */
    public long getTotalDowntimeMillis() {
        return totalDowntimeMillis;
    }
    
    /**
     * Get actual uptime (total runtime minus downtime)
     */
    public long getActualUptimeMillis() {
        return getTotalRunTimeMillis() - totalDowntimeMillis;
    }
    
    /**
     * Calculate uptime percentage: (Total time - Downtime) / Total time × 100
     */
    public double getUptimePercentage() {
        long totalTime = getTotalRunTimeMillis();
        if (totalTime <= 0) return 100.0;
        return ((totalTime - totalDowntimeMillis) * 100.0) / totalTime;
    }
    
    /**
     * Get formatted uptime string (e.g., "2h 45m")
     */
    public String getFormattedUptime() {
        long uptimeMillis = getActualUptimeMillis();
        long hours = uptimeMillis / (1000 * 60 * 60);
        long minutes = (uptimeMillis % (1000 * 60 * 60)) / (1000 * 60);
        return hours + "h " + minutes + "m";
    }
    
    /**
     * Get formatted total runtime string
     */
    public String getFormattedTotalRuntime() {
        long runtimeMillis = getTotalRunTimeMillis();
        long hours = runtimeMillis / (1000 * 60 * 60);
        long minutes = (runtimeMillis % (1000 * 60 * 60)) / (1000 * 60);
        return hours + "h " + minutes + "m";
    }
    
    /**
     * Record a downtime event (when an error/failure occurs)
     * @param durationMillis estimated duration of the downtime event
     */
    public void recordDowntimeEvent(long durationMillis) {
        this.totalDowntimeMillis += durationMillis;
        this.downtimeEventCount++;
    }
    
    /**
     * Record a downtime event with default duration (30 seconds per event)
     */
    public void recordDowntimeEvent() {
        recordDowntimeEvent(30000); // 30 seconds default downtime per event
    }
    
    /**
     * Get total number of downtime events
     */
    public int getDowntimeEventCount() {
        return downtimeEventCount;
    }
    
    /**
     * Check if system is currently online
     */
    public boolean isSystemOnline() {
        // Check database connectivity as primary health indicator
        try {
            return dataBaseDriver != null && dataBaseDriver.getConnection() != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get system status string
     */
    public String getSystemStatus() {
        return isSystemOnline() ? "Online" : "Offline";
    }
}
