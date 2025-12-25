package com.example.national_bank_of_egypt.Security;

import com.example.national_bank_of_egypt.Models.Transaction;
import java.time.LocalDate;
import java.util.List;

/**
 * Frequency-Based Fraud Detection Strategy.
 * 
 * <p>This strategy detects potentially fraudulent activity based on the
 * total number of transactions a user makes per day. Unusually high daily
 * transaction counts may indicate account abuse or fraud.</p>
 * 
 * <h2>Design Pattern: Strategy Pattern (Concrete Strategy)</h2>
 * <p>Implements both {@link FraudDetectionStrategy} for binary detection and
 * {@link RiskBasedFraudDetectionStrategy} for risk-scored assessment.</p>
 * 
 * <h2>Difference from VelocityBasedFraudDetection:</h2>
 * <table border="1">
 *   <tr><th>Aspect</th><th>Velocity</th><th>Frequency</th></tr>
 *   <tr><td>Focus</td><td>Rapid succession (minutes)</td><td>Daily total count</td></tr>
 *   <tr><td>Threshold</td><td>3 transactions</td><td>10 transactions</td></tr>
 *   <tr><td>Use Case</td><td>Automated attacks</td><td>Account abuse</td></tr>
 * </table>
 * 
 * <h2>Detection Logic:</h2>
 * <table border="1">
 *   <tr><th>Condition</th><th>Risk Points</th><th>Description</th></tr>
 *   <tr><td>≥10 transactions today</td><td>25</td><td>High transaction frequency</td></tr>
 *   <tr><td>≥8 transactions (80%)</td><td>15</td><td>Approaching frequency limit</td></tr>
 *   <tr><td><8 transactions today</td><td>0</td><td>Normal usage</td></tr>
 * </table>
 * 
 * @author Mini-Instapay Team
 * @version 1.0
 * @see FraudDetectionStrategy
 * @see RiskBasedFraudDetectionStrategy
 * @see VelocityBasedFraudDetection
 */
public class FrequencyBasedFraudDetection implements FraudDetectionStrategy, RiskBasedFraudDetectionStrategy {
    
    /**
     * Maximum number of daily transactions before flagging as suspicious.
     * Users exceeding this threshold receive the highest risk points.
     */
    private static final int SUSPICIOUS_FREQUENCY_THRESHOLD = 100;
    
    /**
     * User's recent transaction history.
     * Set via constructor or externally before calling isSuspicious().
     */
    public List<Transaction> recentTransactions;

    /**
     * Constructs a FrequencyBasedFraudDetection with transaction history.
     * 
     * @param recentTransactions List of user's recent transactions for analysis
     */
    public FrequencyBasedFraudDetection(List<Transaction> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }
    
    /**
     * Default constructor.
     * Transaction history must be set before calling isSuspicious().
     */
    public FrequencyBasedFraudDetection() {
        this.recentTransactions = null;
    }

    /**
     * Binary fraud detection method for backward compatibility.
     * 
     * <p>Checks if the user has made 10 or more transactions today.</p>
     * 
     * <p><b>Note:</b> recentTransactions must be set before calling this method.</p>
     * 
     * @param transaction The transaction to analyze
     * @param userId The user performing the transaction
     * @return {@code true} if user has ≥10 transactions today, {@code false} otherwise
     */
    @Override
    public boolean isSuspicious(Transaction transaction, String userId) {
        // Return false if no transaction history is available
        if (recentTransactions == null) return false;
        
        LocalDate today = LocalDate.now();
        
        // Count how many transactions the user has made today
        long todayCount = recentTransactions.stream()
                .filter(t -> t.getSender().equals(userId))  // Only count user's outgoing transactions
                .filter(t -> t.getDate().equals(today))      // Only count today's transactions
                .count();
        
        return todayCount >= SUSPICIOUS_FREQUENCY_THRESHOLD;
    }
    
    /**
     * Risk-based fraud assessment method.
     * 
     * <p>Provides graduated risk scoring based on daily transaction count:
     * <ul>
     *   <li>≥10 transactions today: 25 risk points (high frequency)</li>
     *   <li>8-9 transactions today: 15 risk points (approaching limit)</li>
     *   <li><8 transactions today: No risk points added</li>
     * </ul>
     * </p>
     * 
     * @param transaction The transaction to analyze
     * @param userId The user performing the transaction
     * @param recentTransactions User's recent transaction history
     * @param builder The Builder object to add risk factors to
     */
    @Override
    public void assessRisk(Transaction transaction, String userId, List<Transaction> recentTransactions, FraudRiskResult.Builder builder) {
        // Return early if no transaction history is available
        if (recentTransactions == null || recentTransactions.isEmpty()) {
            return;
        }
        
        LocalDate today = LocalDate.now();
        
        // Count how many transactions the user has made today
        long todayCount = recentTransactions.stream()
            .filter(t -> t.getSender().equals(userId))  // Only count user's outgoing transactions
            .filter(t -> t.getDate().equals(today))      // Only count today's transactions
            .count();
        
        // Check if daily transaction count exceeds threshold
        if (todayCount >= SUSPICIOUS_FREQUENCY_THRESHOLD) {
            builder.addRiskFactor(
                "FREQUENCY",
                25,
                String.format("High transaction frequency: %d transactions today (threshold: %d)", 
                    todayCount, SUSPICIOUS_FREQUENCY_THRESHOLD)
            );
        // Check if approaching threshold (80% or more)
        } else if (todayCount >= SUSPICIOUS_FREQUENCY_THRESHOLD * 0.8) {
            builder.addRiskFactor(
                "FREQUENCY",
                15,
                String.format("Approaching frequency limit: %d transactions today", todayCount)
            );
        }
        // Below 80% of threshold is considered normal frequency
    }
}

