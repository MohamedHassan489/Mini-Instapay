package com.example.national_bank_of_egypt.Security;

import com.example.national_bank_of_egypt.Models.Transaction;
import java.util.List;

/**
 * Velocity-Based Fraud Detection Strategy.
 * 
 * <p>This strategy detects potentially fraudulent transactions based on the
 * velocity (speed) of transactions. Multiple transactions in a short time period
 * may indicate automated fraud attacks or account compromise.</p>
 * 
 * <h2>Design Pattern: Strategy Pattern (Concrete Strategy)</h2>
 * <p>Implements {@link RiskBasedFraudDetectionStrategy} for risk-scored assessment.</p>
 * 
 * <h2>Detection Logic:</h2>
 * <table border="1">
 *   <tr><th>Condition</th><th>Risk Points</th><th>Description</th></tr>
 *   <tr><td>≥3 transactions today</td><td>30</td><td>Unusually rapid transactions</td></tr>
 *   <tr><td>2 transactions today</td><td>15</td><td>Multiple transactions detected</td></tr>
 *   <tr><td><2 transactions today</td><td>0</td><td>Normal activity</td></tr>
 * </table>
 * 
 * <h2>Implementation Note:</h2>
 * <p>Since {@link Transaction} only stores {@code LocalDate} (not exact timestamp),
 * this strategy approximates velocity using same-day transaction count. For production
 * systems with exact timestamps, the VELOCITY_WINDOW_MINUTES constant could be used
 * for more precise detection.</p>
 * 
 * <h2>Rationale:</h2>
 * <p>Rapid-fire transactions are commonly associated with:
 * <ul>
 *   <li>Bot attacks attempting to drain accounts</li>
 *   <li>Card testing fraud patterns</li>
 *   <li>Compromised accounts being exploited quickly</li>
 * </ul>
 * </p>
 * 
 * @author Mini-Instapay Team
 * @version 1.0
 * @see RiskBasedFraudDetectionStrategy
 * @see FrequencyBasedFraudDetection
 */
public class VelocityBasedFraudDetection implements RiskBasedFraudDetectionStrategy {
    
    /**
     * Time window in minutes for velocity detection.
     * Reserved for future use when Transaction includes timestamps.
     * Currently using same-day transactions as velocity indicator.
     */
    @SuppressWarnings("unused")
    private static final int VELOCITY_WINDOW_MINUTES = 5;
    
    /**
     * Number of same-day transactions that triggers high velocity alert.
     * If a user makes this many or more transactions in one day, it indicates
     * potentially suspicious rapid activity.
     */
    private static final int SUSPICIOUS_VELOCITY_THRESHOLD = 3;
    
    /**
     * Assesses velocity-based fraud risk for a transaction.
     * 
     * <p>Counts the number of transactions made by the user today and
     * adds risk points based on the velocity:
     * <ul>
     *   <li>≥3 transactions today: 30 risk points (high velocity)</li>
     *   <li>2 transactions today: 15 risk points (moderate velocity)</li>
     *   <li><2 transactions today: No risk points</li>
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
        
        // Count transactions from today (as proxy for recent activity)
        // Since Transaction only stores LocalDate, we use same-day transactions as velocity indicator
        java.time.LocalDate today = java.time.LocalDate.now();
        long todayCount = recentTransactions.stream()
            .filter(t -> t.getSender().equals(userId))  // Only count user's outgoing transactions
            .filter(t -> t.getDate().equals(today))      // Only count today's transactions
            .count();
        
        // If user has made multiple transactions today, it indicates high velocity
        // This is a practical approximation since we don't have exact timestamps
        if (todayCount >= SUSPICIOUS_VELOCITY_THRESHOLD) {
            builder.addRiskFactor(
                "VELOCITY",
                30,
                String.format("Unusually rapid transactions: %d transactions today (indicates high velocity)", 
                    todayCount)
            );
        } else if (todayCount >= 2) {
            // Moderate velocity - user has made 2 transactions today
            builder.addRiskFactor(
                "VELOCITY",
                15,
                String.format("Multiple transactions today: %d transactions (may indicate rapid activity)", 
                    todayCount)
            );
        }
        // Less than 2 transactions today is considered normal velocity
    }
}
