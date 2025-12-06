package com.example.national_bank_of_egypt.Models;

import com.example.national_bank_of_egypt.Views.ViewFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.time.LocalDate;

public class Model {
    private static Model model;
    private final ViewFactory viewFactory;
    private final DataBaseDriver dataBaseDriver;
    private final ObservableList<User> users;
    private User currentUser;
    private final ObservableList<Transaction> transactions;
    private final ObservableList<Dispute> disputes;
    private Boolean userLoginSuccessFlag;
    private Boolean adminLoginSuccessFlag;

    private Model(){
        this.viewFactory = new ViewFactory();
        this.dataBaseDriver = new DataBaseDriver();
        this.userLoginSuccessFlag = false;
        this.currentUser = null;
        this.transactions = FXCollections.observableArrayList();
        this.disputes = FXCollections.observableArrayList();
        this.adminLoginSuccessFlag = false;
        this.users = FXCollections.observableArrayList();
    }

    public static synchronized Model getInstance(){
        if (model == null){
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

    public void evaluateUserCred(String userName, String password) {
        // Reset login flag
        this.userLoginSuccessFlag = false;
        this.currentUser = null;
        
        if (userName == null || userName.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return;
        }
        
        ResultSet resultSet = dataBaseDriver.getUserData(userName, password);
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
                            Integer.parseInt(dateParts[2])
                        );
                        
                        User user = new User(firstName, lastName, email, phoneNumber, address, userName, password, dateCreated);
                        if (twoFactorEnabled != null && "true".equalsIgnoreCase(twoFactorEnabled)) {
                            user.twoFactorEnabledProperty().set("true");
                        }
                        loadUserBankAccounts(user);
                        this.currentUser = user;
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
        }
    }

    private void loadUserBankAccounts(User user) {
        user.getBankAccounts().clear();
        ResultSet rs = dataBaseDriver.getBankAccounts(user.getUserName());
        try {
            if (rs != null) {
                while (rs.next()) {
                    String accountNumber = rs.getString("AccountNumber");
                    String bankName = rs.getString("BankName");
                    double balance = rs.getDouble("Balance");
                    String accountType = rs.getString("AccountType");
                    BankAccount account = new BankAccount(user.getUserName(), accountNumber, bankName, balance, accountType);
                    user.getBankAccounts().add(account);
                }
            }
        } catch (Exception e) {
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
        }
    }

    public boolean registerUser(String firstName, String lastName, String email, String phoneNumber,
                                String address, String userName, String password) {
        // Check if username already exists
        if (dataBaseDriver.userExists(userName)) {
            return false;
        }
        // Check if phone number already exists
        if (dataBaseDriver.phoneNumberExists(phoneNumber)) {
            return false;
        }
        // Check if email already exists
        if (dataBaseDriver.emailExists(email)) {
            return false;
        }
        
        // Create user
        boolean success = dataBaseDriver.createUser(firstName, lastName, email, phoneNumber, address, userName, password, LocalDate.now());
        
        if (success) {
            // Create default transaction limits for new user (daily: $5000, weekly: $20000)
            dataBaseDriver.createTransactionLimit(userName, 5000.0, 20000.0);
        }
        
        return success;
    }

    public boolean updateUserProfile(String firstName, String lastName, String email, String phoneNumber, String address) {
        if (currentUser == null) return false;
        boolean success = dataBaseDriver.updateUser(currentUser.getUserName(), firstName, lastName, email, phoneNumber, address);
        if (success) {
            currentUser.firstNameProperty().set(firstName);
            currentUser.lastNameProperty().set(lastName);
            currentUser.emailProperty().set(email);
            currentUser.phoneNumberProperty().set(phoneNumber);
            currentUser.addressProperty().set(address);
        }
        return success;
    }

    public boolean addBankAccount(String accountNumber, String bankName, double balance, String accountType) {
        if (currentUser == null) return false;
        boolean success = dataBaseDriver.createBankAccount(currentUser.getUserName(), accountNumber, bankName, balance, accountType);
        if (success) {
            // Reload from database to ensure consistency
            loadUserBankAccounts(currentUser);
        }
        return success;
    }

    public boolean removeBankAccount(String accountNumber) {
        if (currentUser == null) return false;
        boolean success = dataBaseDriver.deleteBankAccount(accountNumber);
        if (success) {
            // Reload from database to ensure consistency
            loadUserBankAccounts(currentUser);
        }
        return success;
    }

    public boolean updateBankAccount(String accountNumber, String bankName, String accountType) {
        if (currentUser == null) return false;
        boolean success = dataBaseDriver.updateBankAccount(accountNumber, bankName, accountType);
        if (success) {
            // Reload from database to ensure consistency
            loadUserBankAccounts(currentUser);
        }
        return success;
    }

    public void loadTransactions(int limit) {
        if (currentUser == null) return;
        transactions.clear();
        ResultSet rs = dataBaseDriver.getTransactions(currentUser.getUserName(), limit);
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
                        Integer.parseInt(dateParts[2])
                    );
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
        }
    }

    public ObservableList<Transaction> getTransactions() {
        return transactions;
    }

    public void loadAllUsers() {
        try {
            // Create temporary list to avoid clearing while being accessed
            ObservableList<User> tempUsers = FXCollections.observableArrayList();
            ResultSet rs = dataBaseDriver.getAllUsers();
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
                        Integer.parseInt(dateParts[2])
                    );
                    User user = new User(firstName, lastName, email, phoneNumber, address, userName, password, dateCreated);
                    if (twoFactorEnabled != null && "true".equalsIgnoreCase(twoFactorEnabled)) {
                        user.twoFactorEnabledProperty().set("true");
                    }
                    loadUserBankAccounts(user);
                    tempUsers.add(user);
                }
            }
            // Replace all items at once using setAll() instead of clear() to avoid IndexOutOfBoundsException
            // This is thread-safe and avoids issues with FilteredList
            users.setAll(tempUsers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ObservableList<User> getUsers() {
        return users;
    }

    public void loadDisputes() {
        if (currentUser == null) return;
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
                        Integer.parseInt(dateParts[2])
                    );
                    Dispute dispute = new Dispute(disputeId, transactionId, userId, reason, status, dateCreated);
                    if (rs.getString("Resolution") != null) {
                        dispute.setResolution(rs.getString("Resolution"));
                    }
                    disputes.add(dispute);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        }
    }
    
    public boolean updateTransactionLimits(String userName, double dailyLimit, double weeklyLimit) {
        return dataBaseDriver.updateTransactionLimit(userName, dailyLimit, weeklyLimit);
    }

    public boolean sendMoney(String receiverIdentifier, String senderAccount, double amount, String message, String transactionType) {
        return sendMoney(receiverIdentifier, senderAccount, amount, message, transactionType, null);
    }
    
    public boolean sendMoney(String receiverIdentifier, String senderAccount, double amount, String message, String transactionType, LocalDate scheduledDate) {
        if (currentUser == null) return false;
        
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
                        if (accRs != null && accRs.next()) {
                            receiverAccount = accRs.getString("AccountNumber");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (receiver == null || receiverAccount == null) {
            return false;
        }
        
        TransactionLimit limit = getTransactionLimit(currentUser.getUserName());
        
        // Check if limit is approaching (80% threshold)
        if (limit.isDailyLimitApproaching(amount)) {
            double remaining = limit.getDailyLimitRemaining() - amount;
            com.example.national_bank_of_egypt.Notifications.NotificationService.getInstance()
                .sendNotification(currentUser.getUserName(), "Daily Limit Approaching", 
                    String.format("You are approaching your daily limit. Remaining: $%.2f", remaining), "LIMIT");
        }
        
        if (limit.isWeeklyLimitApproaching(amount)) {
            double remaining = limit.getWeeklyLimitRemaining() - amount;
            com.example.national_bank_of_egypt.Notifications.NotificationService.getInstance()
                .sendNotification(currentUser.getUserName(), "Weekly Limit Approaching", 
                    String.format("You are approaching your weekly limit. Remaining: $%.2f", remaining), "LIMIT");
        }
        
        // Check if limit is exceeded
        if (limit.isDailyLimitExceeded(amount) || limit.isWeeklyLimitExceeded(amount)) {
            com.example.national_bank_of_egypt.Notifications.NotificationService.getInstance()
                .sendNotification(currentUser.getUserName(), "Transaction Limit Exceeded", 
                    "Your transaction exceeds your daily or weekly limit", "LIMIT");
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
                        "Your transaction of $" + amount + " to " + receiver + " is scheduled for " + scheduledDate, "TRANSACTION");
                
                return true;
            }
            return false;
        }
        
        // For instant transactions, proceed with balance check and transfer
        ResultSet senderAccRs = dataBaseDriver.getBankAccountByNumber(senderAccount);
        if (senderAccRs == null) return false;
        
        try {
            if (senderAccRs.next()) {
                double senderBalance = senderAccRs.getDouble("Balance");
                if (senderBalance < amount) {
                    return false;
                }
                
                double newSenderBalance = senderBalance - amount;
                dataBaseDriver.updateBankAccountBalance(senderAccount, newSenderBalance);
                
                ResultSet receiverAccRs = dataBaseDriver.getBankAccountByNumber(receiverAccount);
                if (receiverAccRs != null && receiverAccRs.next()) {
                    double receiverBalance = receiverAccRs.getDouble("Balance");
                    double newReceiverBalance = receiverBalance + amount;
                    dataBaseDriver.updateBankAccountBalance(receiverAccount, newReceiverBalance);
                }
                
                // Create instant transaction
                Transaction transaction = com.example.national_bank_of_egypt.Transactions.TransactionFactory
                    .createInstantTransaction(currentUser.getUserName(), receiver, senderAccount, 
                        receiverAccount, amount, message);
                
                // Fraud Detection - Check for suspicious activity before processing
                com.example.national_bank_of_egypt.Security.FraudDetectionService fraudService = 
                    com.example.national_bank_of_egypt.Security.FraudDetectionService.getInstance();
                
                // Get recent transactions for frequency-based detection
                java.util.List<Transaction> recentTransactions = new java.util.ArrayList<>();
                try {
                    ResultSet recentRs = dataBaseDriver.getTransactions(currentUser.getUserName(), 20);
                    if (recentRs != null) {
                        while (recentRs.next()) {
                            Transaction recentTxn = new Transaction(
                                recentRs.getString("TransactionID"),
                                recentRs.getString("Sender"),
                                recentRs.getString("Receiver"),
                                recentRs.getString("SenderAccount"),
                                recentRs.getString("ReceiverAccount"),
                                recentRs.getDouble("Amount"),
                                recentRs.getString("Date"),
                                recentRs.getString("Message"),
                                recentRs.getString("Status"),
                                recentRs.getString("TransactionType")
                            );
                            recentTransactions.add(recentTxn);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                // Add frequency-based strategy for this check
                if (!recentTransactions.isEmpty()) {
                    fraudService.addStrategy(new com.example.national_bank_of_egypt.Security.FrequencyBasedFraudDetection(recentTransactions));
                }
                
                // Detect fraud
                boolean isSuspicious = fraudService.detectFraud(transaction, currentUser.getUserName(), recentTransactions);
                String transactionStatus = isSuspicious ? "SUSPICIOUS" : "SUCCESS";
                
                // If suspicious, send alerts but still process transaction (admin can review later)
                if (isSuspicious) {
                    // Notify user
                    com.example.national_bank_of_egypt.Notifications.NotificationService.getInstance()
                        .sendNotification(currentUser.getUserName(), "Suspicious Transaction Detected", 
                            "Your transaction of $" + amount + " has been flagged for review. Please contact support if this is legitimate.", "FRAUD");
                    
                    // Notify admin - send to "admin" user (admin notifications go to admin account)
                    com.example.national_bank_of_egypt.Notifications.NotificationService.getInstance()
                        .sendNotification("admin", "Suspicious Transaction Alert", 
                            "Transaction " + transaction.getTransactionId() + " from " + currentUser.getUserName() + 
                            " for $" + amount + " has been flagged as suspicious. Please review.", "FRAUD");
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
                        Integer.parseInt(dateParts[2])
                    );
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
                        Integer.parseInt(dateParts[2])
                    );
                    Dispute dispute = new Dispute(disputeId, transactionId, userId, reason, status, dateCreated);
                    if (rs.getString("Resolution") != null) {
                        dispute.setResolution(rs.getString("Resolution"));
                    }
                    disputes.add(dispute);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Dispute> getAllDisputes() {
        return disputes;
    }

    public boolean flagTransactionAsSuspicious(String transactionId) {
        return dataBaseDriver.updateTransactionStatus(transactionId, "SUSPICIOUS");
    }

    public boolean resolveDispute(String disputeId, String resolution) {
        return dataBaseDriver.resolveDispute(disputeId, resolution);
    }

    public boolean updateDisputeStatus(String disputeId, String status, String resolution) {
        return dataBaseDriver.updateDisputeStatus(disputeId, status, resolution);
    }

    public boolean suspendAccount(String userName) {
        return dataBaseDriver.suspendUser(userName);
    }
}
