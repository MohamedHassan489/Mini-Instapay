package com.example.national_bank_of_egypt.Models;

import java.sql.*;
import java.time.LocalDate;

public class DataBaseDriver {
    private Connection con;

    public DataBaseDriver(){
        try {
            this.con = DriverManager.getConnection("jdbc:sqlite:MiniInstaPay.db");
            initializeDatabase();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void initializeDatabase() {
        try {
            Statement statement = this.con.createStatement();
            
            statement.execute("CREATE TABLE IF NOT EXISTS Users (" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "FirstName VARCHAR(50) NOT NULL, " +
                    "LastName VARCHAR(50) NOT NULL, " +
                    "Email VARCHAR(100) UNIQUE NOT NULL, " +
                    "PhoneNumber VARCHAR(20) UNIQUE NOT NULL, " +
                    "Address TEXT, " +
                    "UserName VARCHAR(100) UNIQUE NOT NULL, " +
                    "Password VARCHAR(100) NOT NULL, " +
                    "DateCreated VARCHAR(20) NOT NULL, " +
                    "TwoFactorEnabled VARCHAR(5) DEFAULT 'false'" +
                    ")");
            
            statement.execute("CREATE TABLE IF NOT EXISTS Admins (" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Username VARCHAR(50) UNIQUE NOT NULL, " +
                    "Password VARCHAR(100) NOT NULL" +
                    ")");
            
            statement.execute("CREATE TABLE IF NOT EXISTS BankAccounts (" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Owner VARCHAR(100) NOT NULL, " +
                    "AccountNumber VARCHAR(50) UNIQUE NOT NULL, " +
                    "BankName VARCHAR(100) NOT NULL, " +
                    "Balance REAL NOT NULL DEFAULT 0.0, " +
                    "AccountType VARCHAR(50) NOT NULL" +
                    ")");
            
            statement.execute("CREATE TABLE IF NOT EXISTS Transactions (" +
                    "TransactionID VARCHAR(100) PRIMARY KEY, " +
                    "Sender VARCHAR(100) NOT NULL, " +
                    "Receiver VARCHAR(100) NOT NULL, " +
                    "SenderAccount VARCHAR(50) NOT NULL, " +
                    "ReceiverAccount VARCHAR(50) NOT NULL, " +
                    "Amount REAL NOT NULL, " +
                    "Date VARCHAR(20) NOT NULL, " +
                    "Message TEXT, " +
                    "Status VARCHAR(20) NOT NULL, " +
                    "TransactionType VARCHAR(20) NOT NULL" +
                    ")");
            
            statement.execute("CREATE TABLE IF NOT EXISTS TransactionLimits (" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "UserName VARCHAR(100) UNIQUE NOT NULL, " +
                    "DailyLimit REAL NOT NULL DEFAULT 5000.0, " +
                    "WeeklyLimit REAL NOT NULL DEFAULT 20000.0, " +
                    "DailyUsed REAL NOT NULL DEFAULT 0.0, " +
                    "WeeklyUsed REAL NOT NULL DEFAULT 0.0" +
                    ")");
            
            statement.execute("CREATE TABLE IF NOT EXISTS Disputes (" +
                    "DisputeID VARCHAR(100) PRIMARY KEY, " +
                    "TransactionID VARCHAR(100) NOT NULL, " +
                    "UserId VARCHAR(100) NOT NULL, " +
                    "Reason TEXT NOT NULL, " +
                    "Status VARCHAR(20) NOT NULL DEFAULT 'PENDING', " +
                    "DateCreated VARCHAR(20) NOT NULL, " +
                    "Resolution TEXT" +
                    ")");
            
            statement.execute("CREATE TABLE IF NOT EXISTS Notifications (" +
                    "NotificationID VARCHAR(100) PRIMARY KEY, " +
                    "UserId VARCHAR(100) NOT NULL, " +
                    "Title VARCHAR(200) NOT NULL, " +
                    "Message TEXT NOT NULL, " +
                    "Type VARCHAR(50) NOT NULL, " +
                    "Timestamp VARCHAR(30) NOT NULL, " +
                    "IsRead VARCHAR(5) DEFAULT 'false'" +
                    ")");
            
            ResultSet adminCheck = statement.executeQuery("SELECT COUNT(*) as count FROM Admins WHERE Username = 'admin'");
            if (adminCheck.getInt("count") == 0) {
                statement.executeUpdate("INSERT INTO Admins (Username, Password) VALUES ('admin', 'admin123')");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getUserData(String userName, String password) {
        try {
            Statement statement = this.con.createStatement();
            return statement.executeQuery("SELECT * FROM Users WHERE UserName = '" + userName + "' AND Password='" + password + "';");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getUserByPhoneNumber(String phoneNumber) {
        try {
            Statement statement = this.con.createStatement();
            return statement.executeQuery("SELECT * FROM Users WHERE PhoneNumber = '" + phoneNumber + "';");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getUserByAccountNumber(String accountNumber) {
        try {
            Statement statement = this.con.createStatement();
            return statement.executeQuery("SELECT u.* FROM Users u JOIN BankAccounts ba ON u.UserName = ba.Owner WHERE ba.AccountNumber = '" + accountNumber + "';");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean createUser(String firstName, String lastName, String email, String phoneNumber,
                              String address, String userName, String password, LocalDate dateCreated) {
        try {
            Statement statement = this.con.createStatement();
            statement.executeUpdate("INSERT INTO Users(FirstName, LastName, Email, PhoneNumber, Address, UserName, Password, DateCreated) " +
                    "VALUES ('" + firstName + "', '" + lastName + "', '" + email + "', '" + phoneNumber + 
                    "', '" + address + "', '" + userName + "', '" + password + "', '" + dateCreated.toString() + "')");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(String userName, String firstName, String lastName, String email, String phoneNumber, String address) {
        try {
            Statement statement = this.con.createStatement();
            statement.executeUpdate("UPDATE Users SET FirstName='" + firstName + "', LastName='" + lastName + 
                    "', Email='" + email + "', PhoneNumber='" + phoneNumber + "', Address='" + address + 
                    "' WHERE UserName='" + userName + "'");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean userExists(String userName) {
        try {
            Statement statement = this.con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) as count FROM Users WHERE UserName = '" + userName + "'");
            return rs.getInt("count") > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean phoneNumberExists(String phoneNumber) {
        try {
            Statement statement = this.con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) as count FROM Users WHERE PhoneNumber = '" + phoneNumber + "'");
            return rs.getInt("count") > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet getAdminData(String username, String password) {
        try {
            Statement statement = this.con.createStatement();
            return statement.executeQuery("SELECT * FROM Admins WHERE Username = '" + username + "' AND Password='" + password + "';");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean createBankAccount(String owner, String accountNumber, String bankName, double balance, String accountType) {
        try {
            Statement statement = this.con.createStatement();
            statement.executeUpdate("INSERT INTO BankAccounts(Owner, AccountNumber, BankName, Balance, AccountType) " +
                    "VALUES ('" + owner + "', '" + accountNumber + "', '" + bankName + "', " + balance + ", '" + accountType + "')");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet getBankAccounts(String owner) {
        try {
            Statement statement = this.con.createStatement();
            return statement.executeQuery("SELECT * FROM BankAccounts WHERE Owner = '" + owner + "';");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getBankAccountByNumber(String accountNumber) {
        try {
            Statement statement = this.con.createStatement();
            return statement.executeQuery("SELECT * FROM BankAccounts WHERE AccountNumber = '" + accountNumber + "';");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateBankAccountBalance(String accountNumber, double balance) {
        try {
            Statement statement = this.con.createStatement();
            statement.executeUpdate("UPDATE BankAccounts SET Balance = " + balance + " WHERE AccountNumber = '" + accountNumber + "'");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBankAccount(String accountNumber) {
        try {
            Statement statement = this.con.createStatement();
            statement.executeUpdate("DELETE FROM BankAccounts WHERE AccountNumber = '" + accountNumber + "'");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createTransaction(String transactionId, String sender, String receiver,
                                    String senderAccount, String receiverAccount, double amount,
                                    LocalDate date, String message, String status, String transactionType) {
        try {
            Statement statement = this.con.createStatement();
            statement.executeUpdate("INSERT INTO Transactions(TransactionID, Sender, Receiver, SenderAccount, ReceiverAccount, Amount, Date, Message, Status, TransactionType) " +
                    "VALUES ('" + transactionId + "', '" + sender + "', '" + receiver + "', '" + senderAccount + 
                    "', '" + receiverAccount + "', " + amount + ", '" + date.toString() + "', '" + message + 
                    "', '" + status + "', '" + transactionType + "')");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet getTransactions(String userId, int limit) {
        try {
            Statement statement = this.con.createStatement();
            String query = "SELECT * FROM Transactions WHERE Sender = '" + userId + "' OR Receiver = '" + userId + "' ORDER BY Date DESC";
            if (limit > 0) {
                query += " LIMIT " + limit;
            }
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getTransactionById(String transactionId) {
        try {
            Statement statement = this.con.createStatement();
            return statement.executeQuery("SELECT * FROM Transactions WHERE TransactionID = '" + transactionId + "'");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateTransactionStatus(String transactionId, String status) {
        try {
            Statement statement = this.con.createStatement();
            statement.executeUpdate("UPDATE Transactions SET Status = '" + status + "' WHERE TransactionID = '" + transactionId + "'");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createTransactionLimit(String userName, double dailyLimit, double weeklyLimit) {
        try {
            Statement statement = this.con.createStatement();
            statement.executeUpdate("INSERT INTO TransactionLimits(UserName, DailyLimit, WeeklyLimit) " +
                    "VALUES ('" + userName + "', " + dailyLimit + ", " + weeklyLimit + ")");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet getTransactionLimit(String userName) {
        try {
            Statement statement = this.con.createStatement();
            return statement.executeQuery("SELECT * FROM TransactionLimits WHERE UserName = '" + userName + "'");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateTransactionLimit(String userName, double dailyLimit, double weeklyLimit) {
        try {
            Statement statement = this.con.createStatement();
            statement.executeUpdate("UPDATE TransactionLimits SET DailyLimit = " + dailyLimit + ", WeeklyLimit = " + weeklyLimit + 
                    " WHERE UserName = '" + userName + "'");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTransactionLimitUsage(String userName, double dailyUsed, double weeklyUsed) {
        try {
            Statement statement = this.con.createStatement();
            statement.executeUpdate("UPDATE TransactionLimits SET DailyUsed = " + dailyUsed + ", WeeklyUsed = " + weeklyUsed + 
                    " WHERE UserName = '" + userName + "'");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createDispute(String disputeId, String transactionId, String userId, String reason, String status, LocalDate dateCreated) {
        try {
            Statement statement = this.con.createStatement();
            statement.executeUpdate("INSERT INTO Disputes(DisputeID, TransactionID, UserId, Reason, Status, DateCreated) " +
                    "VALUES ('" + disputeId + "', '" + transactionId + "', '" + userId + "', '" + reason + 
                    "', '" + status + "', '" + dateCreated.toString() + "')");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet getDisputes(String userId) {
        try {
            Statement statement = this.con.createStatement();
            return statement.executeQuery("SELECT * FROM Disputes WHERE UserId = '" + userId + "' ORDER BY DateCreated DESC");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getAllDisputes() {
        try {
            Statement statement = this.con.createStatement();
            return statement.executeQuery("SELECT * FROM Disputes ORDER BY DateCreated DESC");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateDisputeStatus(String disputeId, String status, String resolution) {
        try {
            Statement statement = this.con.createStatement();
            statement.executeUpdate("UPDATE Disputes SET Status = '" + status + "', Resolution = '" + resolution + 
                    "' WHERE DisputeID = '" + disputeId + "'");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet getAllUsers() {
        try {
            Statement statement = this.con.createStatement();
            return statement.executeQuery("SELECT * FROM Users ORDER BY DateCreated DESC");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean suspendUser(String userName) {
        try {
            Statement statement = this.con.createStatement();
            statement.executeUpdate("UPDATE Users SET TwoFactorEnabled = 'suspended' WHERE UserName = '" + userName + "'");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet getTransactionStats(String startDate, String endDate) {
        try {
            Statement statement = this.con.createStatement();
            return statement.executeQuery("SELECT COUNT(*) as total, SUM(Amount) as totalAmount, " +
                    "AVG(Amount) as avgAmount FROM Transactions WHERE Date >= '" + startDate + "' AND Date <= '" + endDate + "' AND Status = 'SUCCESS'");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getUserActivityStats() {
        try {
            Statement statement = this.con.createStatement();
            return statement.executeQuery("SELECT COUNT(DISTINCT UserName) as totalUsers, " +
                    "COUNT(DISTINCT Sender) + COUNT(DISTINCT Receiver) as activeUsers FROM Users, Transactions");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
