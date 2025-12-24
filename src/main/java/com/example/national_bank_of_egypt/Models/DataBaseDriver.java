package com.example.national_bank_of_egypt.Models;

import java.sql.*;
import java.time.LocalDate;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class DataBaseDriver {
    private Connection con;

    public DataBaseDriver() {
        try {
            // Configure SQLite connection for better concurrency
            // Enable WAL mode for better concurrent access
            // Set busy timeout to 30 seconds (30000ms) to wait for locks to be released
            this.con = DriverManager.getConnection("jdbc:sqlite:MiniInstaPay.db");

            // Set busy timeout - SQLite will wait up to 30 seconds for locks to be released
            try (Statement stmt = this.con.createStatement()) {
                stmt.execute("PRAGMA busy_timeout = 30000"); // 30 seconds
                stmt.execute("PRAGMA journal_mode = WAL"); // Write-Ahead Logging for better concurrency
                stmt.execute("PRAGMA synchronous = NORMAL"); // Balance between safety and performance
            }

            initializeDatabase();
        } catch (SQLException e) {
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

            // Create default admin account if it doesn't exist (plain text password -
            // encryption disabled)
            ResultSet adminCheck = statement
                    .executeQuery("SELECT COUNT(*) as count FROM Admins WHERE Username = 'admin'");
            if (adminCheck.getInt("count") == 0) {
                statement.executeUpdate("INSERT INTO Admins (Username, Password) VALUES ('admin', 'admin123')");
            }

            // Create default user account if it doesn't exist (plain text password -
            // encryption disabled)
            ResultSet userCheck = statement.executeQuery("SELECT COUNT(*) as count FROM Users WHERE UserName = 'user'");
            if (userCheck.getInt("count") == 0) {
                String defaultDate = LocalDate.now().toString();
                statement.executeUpdate(
                        "INSERT INTO Users(FirstName, LastName, Email, PhoneNumber, Address, UserName, Password, DateCreated, TwoFactorEnabled) "
                                +
                                "VALUES ('John', 'Doe', 'user@example.com', '1234567890', '123 Main St', 'user', 'user123', '"
                                + defaultDate + "', 'false')");

                // Create a default bank account for the user
                statement.executeUpdate(
                        "INSERT INTO BankAccounts(Owner, AccountNumber, BankName, Balance, AccountType) " +
                                "VALUES ('user', 'ACC001', 'Mini-InstaPay Bank', 1000.0, 'Checking')");

                // Create default transaction limits for the user
                statement.executeUpdate(
                        "INSERT INTO TransactionLimits(UserName, DailyLimit, WeeklyLimit, DailyUsed, WeeklyUsed) " +
                                "VALUES ('user', 5000.0, 20000.0, 0.0, 0.0)");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getUserData(String userName, String password) {
        try {
            Statement statement = this.con.createStatement();
            if (password == null || password.isEmpty()) {
                // If no password provided, just get user by username
                return statement.executeQuery("SELECT * FROM Users WHERE UserName = '" + userName + "';");
            }

            // Get user data - using plain text password comparison (encryption disabled)
            // First check if user exists with matching password
            ResultSet checkRs = statement.executeQuery("SELECT COUNT(*) as count FROM Users WHERE UserName = '"
                    + userName + "' AND Password='" + password + "';");
            if (checkRs.next() && checkRs.getInt("count") > 0) {
                // Password matches, return the user data
                return statement.executeQuery(
                        "SELECT * FROM Users WHERE UserName = '" + userName + "' AND Password='" + password + "';");
            }
            return null;
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
            return statement.executeQuery(
                    "SELECT u.* FROM Users u JOIN BankAccounts ba ON u.UserName = ba.Owner WHERE ba.AccountNumber = '"
                            + accountNumber + "';");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean createUser(String firstName, String lastName, String email, String phoneNumber,
            String address, String userName, String password, LocalDate dateCreated) {
        try {
            // Store password as plain text (encryption disabled)
            Statement statement = this.con.createStatement();
            statement.executeUpdate(
                    "INSERT INTO Users(FirstName, LastName, Email, PhoneNumber, Address, UserName, Password, DateCreated, TwoFactorEnabled) "
                            +
                            "VALUES ('" + firstName.replace("'", "''") + "', '" + lastName.replace("'", "''") + "', '"
                            + email.replace("'", "''") + "', '" + phoneNumber.replace("'", "''") +
                            "', '" + address.replace("'", "''") + "', '" + userName.replace("'", "''") + "', '"
                            + password.replace("'", "''") + "', '" + dateCreated.toString() + "', 'false')");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(String userName, String firstName, String lastName, String email, String phoneNumber,
            String address) {
        // #region agent log
        long updateStartTime = System.currentTimeMillis();
        logDbDebug("DataBaseDriver.java:179", "updateUser entry", "updateUser_start", "H1,H2,H3");
        // #endregion
        int maxRetries = 5;
        int baseRetryDelay = 200; // milliseconds

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            // #region agent log
            logDbDebug("DataBaseDriver.java:186", "updateUser attempt", "attempt=" + attempt, "H1,H2,H3");
            // #endregion
            try (Statement statement = this.con.createStatement()) {
                String sql = "UPDATE Users SET FirstName='" + firstName.replace("'", "''") + "', LastName='"
                        + lastName.replace("'", "''") +
                        "', Email='" + email.replace("'", "''") + "', PhoneNumber='" + phoneNumber.replace("'", "''")
                        + "', Address='" + address.replace("'", "''") +
                        "' WHERE UserName='" + userName.replace("'", "''") + "'";
                logDbDebug("DataBaseDriver.java:189", "updateUser SQL", sql, userName);
                int rowsAffected = statement.executeUpdate(sql);
                logDbDebug("DataBaseDriver.java:191", "updateUser result", "rowsAffected=" + rowsAffected, userName);
                // #region agent log
                long updateEndTime = System.currentTimeMillis();
                logDbDebug("DataBaseDriver.java:193", "updateUser success",
                        "duration=" + (updateEndTime - updateStartTime) + "ms", "H1,H2,H3");
                // #endregion
                return rowsAffected > 0;
            } catch (SQLException e) {
                if (e.getMessage() != null && e.getMessage().contains("database is locked")
                        && attempt < maxRetries - 1) {
                    logDbDebug("DataBaseDriver.java:197", "updateUser SQLITE_BUSY retry",
                            "attempt " + (attempt + 1) + " of " + maxRetries, userName);
                    try {
                        // Exponential backoff: 200ms, 400ms, 800ms, 1600ms, 3200ms
                        Thread.sleep(baseRetryDelay * (1L << attempt));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                } else {
                    logDbDebug("DataBaseDriver.java:204", "updateUser SQLException", e.getMessage(), userName);
                    // #region agent log
                    long updateEndTime = System.currentTimeMillis();
                    logDbDebug("DataBaseDriver.java:206", "updateUser failed",
                            "duration=" + (updateEndTime - updateStartTime) + "ms, error=" + e.getMessage(),
                            "H1,H2,H3");
                    // #endregion
                    e.printStackTrace();
                    return false;
                }
            }
        }
        // #region agent log
        long updateEndTime = System.currentTimeMillis();
        logDbDebug("DataBaseDriver.java:214", "updateUser exhausted retries",
                "duration=" + (updateEndTime - updateStartTime) + "ms", "H1,H2,H3");
        // #endregion
        return false;
    }

    public boolean updateTwoFactorEnabled(String userName, boolean enabled) {
        int maxRetries = 3;
        int retryDelay = 100; // milliseconds

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try (Statement statement = this.con.createStatement()) {
                String value = enabled ? "true" : "false";
                String sql = "UPDATE Users SET TwoFactorEnabled='" + value + "' WHERE UserName='"
                        + userName.replace("'", "''") + "'";
                int rowsAffected = statement.executeUpdate(sql);
                return rowsAffected > 0;
            } catch (SQLException e) {
                if (e.getMessage() != null && e.getMessage().contains("database is locked")
                        && attempt < maxRetries - 1) {
                    try {
                        Thread.sleep(retryDelay * (attempt + 1)); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                } else {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    public boolean userExists(String userName) {
        try (Statement statement = this.con.createStatement()) {
            ResultSet rs = statement.executeQuery(
                    "SELECT COUNT(*) as count FROM Users WHERE UserName = '" + userName.replace("'", "''") + "'");
            boolean exists = rs.getInt("count") > 0;
            rs.close();
            return exists;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean phoneNumberExists(String phoneNumber) {
        try (Statement statement = this.con.createStatement()) {
            ResultSet rs = statement.executeQuery(
                    "SELECT COUNT(*) as count FROM Users WHERE PhoneNumber = '" + phoneNumber.replace("'", "''") + "'");
            boolean exists = rs.getInt("count") > 0;
            rs.close();
            return exists;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean emailExists(String email) {
        // #region agent log
        logDbDebug("DataBaseDriver.java:262", "emailExists entry", "emailExists_start", "H1,H2");
        // #endregion
        try (Statement statement = this.con.createStatement()) {
            ResultSet rs = statement.executeQuery(
                    "SELECT COUNT(*) as count FROM Users WHERE Email = '" + email.replace("'", "''") + "'");
            boolean exists = rs.getInt("count") > 0;
            rs.close();
            // #region agent log
            logDbDebug("DataBaseDriver.java:267", "emailExists exit", "emailExists_end", "H1,H2");
            // #endregion
            return exists;
        } catch (SQLException e) {
            // #region agent log
            logDbDebug("DataBaseDriver.java:270", "emailExists exception", e.getMessage(), "H1,H2");
            // #endregion
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet getAdminData(String username, String password) {
        try {
            Statement statement = this.con.createStatement();
            // Using plain text password comparison (encryption disabled)
            return statement.executeQuery(
                    "SELECT * FROM Admins WHERE Username = '" + username + "' AND Password='" + password + "';");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getUserByEmail(String email) {
        try {
            Statement statement = this.con.createStatement();
            return statement.executeQuery("SELECT * FROM Users WHERE Email = '" + email + "';");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean accountNumberExists(String accountNumber) {
        try (Statement statement = this.con.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) as count FROM BankAccounts WHERE AccountNumber = '"
                    + accountNumber.replace("'", "''") + "'");
            boolean exists = rs.next() && rs.getInt("count") > 0;
            rs.close();
            return exists;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createBankAccount(String owner, String accountNumber, String bankName, double balance,
            String accountType) {
        try {
            // Check if account number already exists
            if (accountNumberExists(accountNumber)) {
                return false;
            }
            Statement statement = this.con.createStatement();
            statement.executeUpdate("INSERT INTO BankAccounts(Owner, AccountNumber, BankName, Balance, AccountType) " +
                    "VALUES ('" + owner + "', '" + accountNumber + "', '" + bankName + "', " + balance + ", '"
                    + accountType + "')");
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
            statement.executeUpdate(
                    "UPDATE BankAccounts SET Balance = " + balance + " WHERE AccountNumber = '" + accountNumber + "'");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBankAccount(String accountNumber, String bankName, String accountType) {
        try {
            Statement statement = this.con.createStatement();
            statement.executeUpdate(
                    "UPDATE BankAccounts SET BankName = '" + bankName + "', AccountType = '" + accountType +
                            "' WHERE AccountNumber = '" + accountNumber + "'");
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
            statement.executeUpdate(
                    "INSERT INTO Transactions(TransactionID, Sender, Receiver, SenderAccount, ReceiverAccount, Amount, Date, Message, Status, TransactionType) "
                            +
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
            String query = "SELECT * FROM Transactions WHERE Sender = '" + userId.replace("'", "''")
                    + "' OR Receiver = '" + userId.replace("'", "''") + "' ORDER BY Date DESC";
            if (limit > 0) {
                query += " LIMIT " + limit;
            }
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getTransactionsByAccount(String userId, String accountNumber, int limit) {
        try {
            Statement statement = this.con.createStatement();
            StringBuilder query = new StringBuilder("SELECT * FROM Transactions WHERE (Sender = '");
            query.append(userId.replace("'", "''"));
            query.append("' OR Receiver = '");
            query.append(userId.replace("'", "''"));
            query.append("') AND (SenderAccount = '");
            query.append(accountNumber.replace("'", "''"));
            query.append("' OR ReceiverAccount = '");
            query.append(accountNumber.replace("'", "''"));
            query.append("') ORDER BY Date DESC");
            if (limit > 0) {
                query.append(" LIMIT ").append(limit);
            }
            return statement.executeQuery(query.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getTransactionsByDateRange(String userId, String startDate, String endDate, String accountNumber) {
        try {
            Statement statement = this.con.createStatement();
            StringBuilder query = new StringBuilder("SELECT * FROM Transactions WHERE (Sender = '");
            query.append(userId.replace("'", "''"));
            query.append("' OR Receiver = '");
            query.append(userId.replace("'", "''"));
            query.append("')");

            if (accountNumber != null && !accountNumber.isEmpty()) {
                query.append(" AND (SenderAccount = '");
                query.append(accountNumber.replace("'", "''"));
                query.append("' OR ReceiverAccount = '");
                query.append(accountNumber.replace("'", "''"));
                query.append("')");
            }

            if (startDate != null && !startDate.isEmpty()) {
                query.append(" AND Date >= '");
                query.append(startDate.replace("'", "''"));
                query.append("'");
            }

            if (endDate != null && !endDate.isEmpty()) {
                query.append(" AND Date <= '");
                query.append(endDate.replace("'", "''"));
                query.append("'");
            }

            query.append(" ORDER BY Date ASC");

            return statement.executeQuery(query.toString());
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
            statement.executeUpdate(
                    "UPDATE Transactions SET Status = '" + status + "' WHERE TransactionID = '" + transactionId + "'");
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
        int maxRetries = 3;
        int retryDelay = 100; // milliseconds

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try (Statement statement = this.con.createStatement()) {
                String sql = "UPDATE TransactionLimits SET DailyLimit = " + dailyLimit + ", WeeklyLimit = "
                        + weeklyLimit +
                        " WHERE UserName = '" + userName.replace("'", "''") + "'";
                logDbDebug("DataBaseDriver.java:415", "updateTransactionLimit SQL", sql, userName);
                int rowsAffected = statement.executeUpdate(sql);
                logDbDebug("DataBaseDriver.java:417", "updateTransactionLimit result", "rowsAffected=" + rowsAffected,
                        userName);
                return rowsAffected > 0;
            } catch (SQLException e) {
                if (e.getMessage() != null && e.getMessage().contains("database is locked")
                        && attempt < maxRetries - 1) {
                    logDbDebug("DataBaseDriver.java:419", "updateTransactionLimit SQLITE_BUSY retry",
                            "attempt " + (attempt + 1) + " of " + maxRetries, userName);
                    try {
                        Thread.sleep(retryDelay * (attempt + 1)); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                } else {
                    logDbDebug("DataBaseDriver.java:419", "updateTransactionLimit SQLException", e.getMessage(),
                            userName);
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    public boolean updateTransactionLimitUsage(String userName, double dailyUsed, double weeklyUsed) {
        try {
            Statement statement = this.con.createStatement();
            statement.executeUpdate(
                    "UPDATE TransactionLimits SET DailyUsed = " + dailyUsed + ", WeeklyUsed = " + weeklyUsed +
                            " WHERE UserName = '" + userName + "'");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createDispute(String disputeId, String transactionId, String userId, String reason, String status,
            LocalDate dateCreated) {
        try {
            Statement statement = this.con.createStatement();
            statement.executeUpdate(
                    "INSERT INTO Disputes(DisputeID, TransactionID, UserId, Reason, Status, DateCreated) " +
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
            return statement
                    .executeQuery("SELECT * FROM Disputes WHERE UserId = '" + userId + "' ORDER BY DateCreated DESC");
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
            statement.executeUpdate(
                    "UPDATE Users SET TwoFactorEnabled = 'suspended' WHERE UserName = '" + userName + "'");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet getTransactionStats(String startDate, String endDate) {
        try {
            Statement statement = this.con.createStatement();
            String query = "SELECT COUNT(*) as total, " +
                    "COALESCE(SUM(Amount), 0) as totalAmount, " +
                    "COALESCE(AVG(Amount), 0) as avgAmount, " +
                    "COALESCE(MAX(Amount), 0) as maxAmount, " +
                    "COALESCE(MIN(Amount), 0) as minAmount " +
                    "FROM Transactions WHERE Date >= '" + startDate + "' AND Date <= '" + endDate
                    + "' AND Status = 'SUCCESS'";
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getUserActivityStats() {
        try {
            Statement statement = this.con.createStatement();
            // Calculate date 30 days ago as string (YYYY-MM-DD format)
            LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
            String dateThreshold = thirtyDaysAgo.toString();

            // Count total users
            // Count active users (users who have made or received transactions in the last
            // 30 days)
            String query = "SELECT " +
                    "(SELECT COUNT(*) FROM Users) as totalUsers, " +
                    "(SELECT COUNT(DISTINCT CASE WHEN t.Date >= '" + dateThreshold + "' THEN t.Sender END) + " +
                    "COUNT(DISTINCT CASE WHEN t.Date >= '" + dateThreshold + "' THEN t.Receiver END) " +
                    "FROM Transactions t) as activeUsers, " +
                    "(SELECT COUNT(*) FROM Transactions WHERE Date >= '" + dateThreshold + "') as recentTransactions, "
                    +
                    "(SELECT COUNT(*) FROM Transactions) as totalTransactions, " +
                    "(SELECT COALESCE(AVG(Amount), 0) FROM Transactions WHERE Status = 'SUCCESS') as avgTransactionAmount";
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getAccountUsageDetails() {
        try {
            Statement statement = this.con.createStatement();
            // Get detailed account usage statistics
            String query = "SELECT " +
                    "u.UserName, " +
                    "u.DateCreated, " +
                    "COUNT(DISTINCT t.TransactionID) as transactionCount, " +
                    "COALESCE(SUM(CASE WHEN t.Sender = u.UserName THEN t.Amount ELSE 0 END), 0) as totalSent, " +
                    "COALESCE(SUM(CASE WHEN t.Receiver = u.UserName THEN t.Amount ELSE 0 END), 0) as totalReceived, " +
                    "COUNT(DISTINCT ba.AccountNumber) as accountCount " +
                    "FROM Users u " +
                    "LEFT JOIN Transactions t ON (t.Sender = u.UserName OR t.Receiver = u.UserName) " +
                    "LEFT JOIN BankAccounts ba ON ba.Owner = u.UserName " +
                    "GROUP BY u.UserName, u.DateCreated " +
                    "ORDER BY transactionCount DESC";
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getAllTransactions() {
        try {
            Statement statement = this.con.createStatement();
            return statement.executeQuery("SELECT * FROM Transactions ORDER BY Date DESC");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean resolveDispute(String disputeId, String resolution) {
        try {
            Statement statement = this.con.createStatement();
            statement.executeUpdate("UPDATE Disputes SET Status = 'RESOLVED', Resolution = '" + resolution
                    + "' WHERE DisputeID = '" + disputeId + "'");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createNotification(String notificationId, String userId, String title, String message, String type,
            String timestamp) {
        try {
            Statement statement = this.con.createStatement();
            statement.executeUpdate(
                    "INSERT INTO Notifications(NotificationID, UserId, Title, Message, Type, Timestamp, IsRead) " +
                            "VALUES ('" + notificationId + "', '" + userId + "', '" + title.replace("'", "''") + "', '"
                            +
                            message.replace("'", "''") + "', '" + type + "', '" + timestamp + "', 'false')");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet getNotifications(String userId) {
        // #region agent log
        try {
            java.nio.file.Files.write(
                    java.nio.file.Paths
                            .get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"),
                    ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"N4\",\"location\":\"DataBaseDriver.java:734\",\"message\":\"getNotifications entry\",\"timestamp\":"
                            + System.currentTimeMillis() + ",\"data\":{\"userId\":\"" + userId + "\"}}\n").getBytes(),
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception logEx) {
        }
        // #endregion
        try {
            Statement statement = this.con.createStatement();
            String query = "SELECT * FROM Notifications WHERE UserId = '" + userId.replace("'", "''")
                    + "' ORDER BY Timestamp DESC";
            // #region agent log
            try {
                java.nio.file.Files.write(
                        java.nio.file.Paths
                                .get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"),
                        ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"N4\",\"location\":\"DataBaseDriver.java:738\",\"message\":\"getNotifications SQL\",\"timestamp\":"
                                + System.currentTimeMillis() + ",\"data\":{\"query\":\"" + query + "\"}}\n").getBytes(),
                        java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            } catch (Exception logEx) {
            }
            // #endregion
            ResultSet rs = statement.executeQuery(query);
            // #region agent log
            try {
                java.nio.file.Files.write(
                        java.nio.file.Paths
                                .get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"),
                        ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"N4\",\"location\":\"DataBaseDriver.java:741\",\"message\":\"getNotifications ResultSet created\",\"timestamp\":"
                                + System.currentTimeMillis() + ",\"data\":{\"resultSetNull\":" + (rs == null) + "}}\n")
                                .getBytes(),
                        java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            } catch (Exception logEx) {
            }
            // #endregion
            return rs;
        } catch (SQLException e) {
            // #region agent log
            try {
                java.nio.file.Files.write(
                        java.nio.file.Paths
                                .get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"),
                        ("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"N4\",\"location\":\"DataBaseDriver.java:745\",\"message\":\"getNotifications SQLException\",\"timestamp\":"
                                + System.currentTimeMillis() + ",\"data\":{\"error\":\"" + e.getMessage() + "\"}}\n")
                                .getBytes(),
                        java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            } catch (Exception logEx) {
            }
            // #endregion
            e.printStackTrace();
            return null;
        }
    }

    public boolean markNotificationAsRead(String notificationId) {
        try {
            Statement statement = this.con.createStatement();
            statement.executeUpdate(
                    "UPDATE Notifications SET IsRead = 'true' WHERE NotificationID = '" + notificationId + "'");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // #region agent log
    private void logDbDebug(String location, String message, String data, String userName) {
        try {
            StringBuilder json = new StringBuilder();
            json.append("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"C\",\"location\":\"")
                    .append(location.replace("\"", "\\\""))
                    .append("\",\"message\":\"").append(message.replace("\"", "\\\""))
                    .append("\",\"timestamp\":").append(System.currentTimeMillis())
                    .append(",\"data\":{\"sqlOrError\":\"")
                    .append(data.replace("\"", "\\\"").replace("\n", " ").replace("\r", ""))
                    .append("\",\"userName\":\"").append(userName != null ? userName.replace("\"", "\\\"") : "null")
                    .append("\"}}\n");
            Files.write(Paths.get("c:\\Users\\DELL\\Downloads\\National_Bank_of_Egypt_work\\.cursor\\debug.log"),
                    json.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) {
            // Silently fail to avoid disrupting the application
        }
    }
    // #endregion
}
