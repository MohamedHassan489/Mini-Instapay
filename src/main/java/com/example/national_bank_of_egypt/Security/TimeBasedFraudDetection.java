package com.example.national_bank_of_egypt.Security;

import com.example.national_bank_of_egypt.Models.Transaction;
import java.time.LocalTime;
import java.util.List;

/**
 * Time-Based Fraud Detection Strategy.
 * 
 * <p>This strategy detects potentially fraudulent transactions based on the
 * time of day when the transaction occurs. Transactions at unusual hours
 * (late night/early morning) are flagged as higher risk.</p>
 * 
 * <h2>Design Pattern: Strategy Pattern (Concrete Strategy)</h2>
 * <p>Implements {@link RiskBasedFraudDetectionStrategy} for risk-scored assessment.</p>
 * 
 * <h2>Time Windows:</h2>
 * <pre>
 * 12AM   3AM    6AM    9AM    12PM   3PM    5PM    10PM   12AM
 *  |      |      |      |      |      |      |      |      |
 *  |--UNUSUAL-----|      |----BUSINESS HOURS---|      |--UNUSUAL--|
 *                 |---OFF HOURS---|      |---OFF HOURS---|
 * </pre>
 * 
 * <h2>Detection Logic:</h2>
 * <table border="1">
 *   <tr><th>Time Period</th><th>Risk Points</th><th>Description</th></tr>
 *   <tr><td>10:00 PM - 6:00 AM</td><td>15</td><td>Unusual hours (late night/early morning)</td></tr>
 *   <tr><td>6:00 AM - 9:00 AM or 5:00 PM - 10:00 PM</td><td>10</td><td>Outside business hours</td></tr>
 *   <tr><td>9:00 AM - 5:00 PM</td><td>0</td><td>Normal business hours</td></tr>
 * </table>
 * 
 * <h2>Rationale:</h2>
 * <p>Late-night transactions may indicate:
 * <ul>
 *   <li>Compromised accounts accessed from different time zones</li>
 *   <li>Automated fraud attacks running overnight</li>
 *   <li>Emergency situations (legitimate but worth flagging)</li>
 * </ul>
 * </p>
 * 
 * @author Mini-Instapay Team
 * @version 1.0
 * @see RiskBasedFraudDetectionStrategy
 */
public class TimeBasedFraudDetection implements RiskBasedFraudDetectionStrategy {
    
    /** Start of business hours (9:00 AM) */
    private static final LocalTime BUSINESS_START = LocalTime.of(9, 0);
    
    /** End of business hours (5:00 PM) */
    private static final LocalTime BUSINESS_END = LocalTime.of(17, 0);
    
    /** Start of unusual hours (10:00 PM) - late night */
    private static final LocalTime UNUSUAL_START = LocalTime.of(22, 0);
    
    /** End of unusual hours (6:00 AM) - early morning */
    private static final LocalTime UNUSUAL_END = LocalTime.of(6, 0);
    
    /**
     * Assesses time-based fraud risk for a transaction.
     * 
     * <p>Evaluates the current time and adds risk points based on:
     * <ul>
     *   <li>Unusual hours (10 PM - 6 AM): 15 risk points</li>
     *   <li>Off-hours (6-9 AM or 5-10 PM): 10 risk points</li>
     *   <li>Business hours (9 AM - 5 PM): No risk points</li>
     * </ul>
     * </p>
     * 
     * @param transaction The transaction to analyze
     * @param userId The user performing the transaction (not used by this strategy)
     * @param recentTransactions Not used by this strategy
     * @param builder The Builder object to add risk factors to
     */
    @Override
    public void assessRisk(Transaction transaction, String userId, List<Transaction> recentTransactions, FraudRiskResult.Builder builder) {
        // Get the current time when the transaction is being processed
        LocalTime currentTime = LocalTime.now();
        
        // Check if current time falls within business hours (9 AM - 5 PM)
        boolean isBusinessHours = currentTime.isAfter(BUSINESS_START) && currentTime.isBefore(BUSINESS_END);
        
        // Check if current time falls within unusual hours (10 PM - 6 AM)
        // Note: This spans midnight, so we check isAfter(22:00) OR isBefore(6:00)
        boolean isUnusualHours = currentTime.isAfter(UNUSUAL_START) || currentTime.isBefore(UNUSUAL_END);
        
        // Highest risk: transactions during unusual hours (late night/early morning)
        if (isUnusualHours) {
            builder.addRiskFactor(
                "TIME",
                15,
                String.format("Transaction at unusual time: %s (outside normal business hours)", currentTime)
            );
        // Moderate risk: transactions outside business hours but not late night
        } else if (!isBusinessHours) {
            builder.addRiskFactor(
                "TIME",
                10,
                String.format("Transaction outside business hours: %s", currentTime)
            );
        }
        // Business hours transactions do not add any risk points
    }
}
