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
        ResultSet resultSet = dataBaseDriver.getUserData(userName, password);
        try {
            if (resultSet != null && resultSet.next()) {
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                String email = resultSet.getString("Email");
                String phoneNumber = resultSet.getString("PhoneNumber");
                String address = resultSet.getString("Address");
                String[] dateParts = resultSet.getString("DateCreated").split("-");
                LocalDate dateCreated = LocalDate.of(
                    Integer.parseInt(dateParts[0]),
                    Integer.parseInt(dateParts[1]),
                    Integer.parseInt(dateParts[2])
                );
                
                User user = new User(firstName, lastName, email, phoneNumber, address, userName, password, dateCreated);
                loadUserBankAccounts(user);
                this.currentUser = user;
                this.userLoginSuccessFlag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUserBankAccounts(User user) {
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

    public void evaluateAdminCred(String userName, String password) {
        ResultSet resultSet = dataBaseDriver.getAdminData(userName, password);
        try {
            if (resultSet != null && resultSet.next()) {
                this.adminLoginSuccessFlag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean registerUser(String firstName, String lastName, String email, String phoneNumber,
                                String address, String userName, String password) {
        if (dataBaseDriver.userExists(userName)) {
            return false;
        }
        if (dataBaseDriver.phoneNumberExists(phoneNumber)) {
            return false;
        }
        return dataBaseDriver.createUser(firstName, lastName, email, phoneNumber, address, userName, password, LocalDate.now());
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
            BankAccount account = new BankAccount(currentUser.getUserName(), accountNumber, bankName, balance, accountType);
            currentUser.getBankAccounts().add(account);
        }
        return success;
    }

    public boolean removeBankAccount(String accountNumber) {
        if (currentUser == null) return false;
        boolean success = dataBaseDriver.deleteBankAccount(accountNumber);
        if (success) {
            currentUser.getBankAccounts().removeIf(acc -> acc.getAccountNumber().equals(accountNumber));
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
        users.clear();
        ResultSet rs = dataBaseDriver.getAllUsers();
        try {
            if (rs != null) {
                while (rs.next()) {
                    String firstName = rs.getString("FirstName");
                    String lastName = rs.getString("LastName");
                    String email = rs.getString("Email");
                    String phoneNumber = rs.getString("PhoneNumber");
                    String address = rs.getString("Address");
                    String userName = rs.getString("UserName");
                    String password = rs.getString("Password");
                    String[] dateParts = rs.getString("DateCreated").split("-");
                    LocalDate dateCreated = LocalDate.of(
                        Integer.parseInt(dateParts[0]),
                        Integer.parseInt(dateParts[1]),
                        Integer.parseInt(dateParts[2])
                    );
                    User user = new User(firstName, lastName, email, phoneNumber, address, userName, password, dateCreated);
                    loadUserBankAccounts(user);
                    users.add(user);
                }
            }
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

    public boolean sendMoney(String receiverIdentifier, String senderAccount, double amount, String message, String transactionType) {
        if (currentUser == null) return false;
        
        ResultSet receiverRs = null;
        String receiver = null;
        String receiverAccount = null;
        
        if (receiverIdentifier.startsWith("@") || receiverIdentifier.contains("@")) {
            receiverRs = dataBaseDriver.getUserData(receiverIdentifier, "");
        } else if (receiverIdentifier.matches("\\d+")) {
            if (receiverIdentifier.length() == 10) {
                receiverRs = dataBaseDriver.getUserByPhoneNumber(receiverIdentifier);
            } else {
                receiverRs = dataBaseDriver.getUserByAccountNumber(receiverIdentifier);
                if (receiverRs != null) {
                    try {
                        if (receiverRs.next()) {
                            receiverAccount = receiverIdentifier;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
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
        if (limit.isDailyLimitExceeded(amount) || limit.isWeeklyLimitExceeded(amount)) {
            com.example.national_bank_of_egypt.Notifications.NotificationService.getInstance()
                .sendNotification(currentUser.getUserName(), "Transaction Limit Exceeded", 
                    "Your transaction exceeds your daily or weekly limit", "LIMIT");
            return false;
        }
        
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
                
                Transaction transaction = com.example.national_bank_of_egypt.Transactions.TransactionFactory
                    .createInstantTransaction(currentUser.getUserName(), receiver, senderAccount, 
                        receiverAccount, amount, message);
                
                dataBaseDriver.createTransaction(transaction.getTransactionId(), transaction.getSender(), 
                    transaction.getReceiver(), transaction.getSenderAccount(), transaction.getReceiverAccount(),
                    transaction.getAmount(), transaction.getDate(), transaction.getMessage(),
                    "SUCCESS", transaction.getTransactionType());
                
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
}
