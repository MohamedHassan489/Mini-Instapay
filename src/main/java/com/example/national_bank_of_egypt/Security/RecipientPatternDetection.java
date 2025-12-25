package com.example.national_bank_of_egypt.Security;

import com.example.national_bank_of_egypt.Models.Transaction;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Recipient Pattern Detection Strategy.
 * 
 * <p>This strategy detects potentially fraudulent transactions by analyzing
 * the recipient of the transaction. Transactions to new or unknown recipients
 * are flagged as higher risk.</p>
 * 
 * <h2>Design Pattern: Strategy Pattern (Concrete Strategy)</h2>
 * <p>Implements {@link RiskBasedFraudDetectionStrategy} for risk-scored assessment.</p>
 * 
 * <h2>Detection Logic:</h2>
 * <table border="1">
 *   <tr><th>Condition</th><th>Risk Points</th><th>Description</th></tr>
 *   <tr><td>No transaction history</td><td>20</td><td>New user - no baseline to compare</td></tr>
 *   <tr><td>Recipient never seen before</td><td>20</td><td>First transaction with this recipient</td></tr>
 *   <tr><td>Known recipient</td><td>0</td><td>Previously transacted with</td></tr>
 * </table>
 * 
 * <h2>Rationale:</h2>
 * <p>Legitimate users typically transact with a consistent set of recipients.
 * New recipients may indicate:
 * <ul>
 *   <li>Phishing victim sending to attacker's account</li>
 *   <li>Legitimate new business relationship (lower risk)</li>
 *   <li>Money mule operation</li>
 *   <li>Account takeover sending to attacker-controlled accounts</li>
 * </ul>
 * </p>
 * 
 * @author Mini-Instapay Team
 * @version 1.0
 * @see RiskBasedFraudDetectionStrategy
 */
public class RecipientPatternDetection implements RiskBasedFraudDetectionStrategy {
    
    /**
     * Assesses recipient-based fraud risk for a transaction.
     * 
     * <p>Checks if the transaction recipient is someone the user has
     * transacted with before:
     * <ul>
     *   <li>No transaction history: 20 risk points (new user)</li>
     *   <li>New recipient: 20 risk points (never transacted with before)</li>
     *   <li>Known recipient: No risk points</li>
     * </ul>
     * </p>
     * 
     * @param transaction The transaction to analyze
     * @param userId The user performing the transaction
     * @param recentTransactions User's recent transaction history
     * @param result The result object to add risk factors to
     */
    @Override
    public void assessRisk(Transaction transaction, String userId, List<Transaction> recentTransactions, FraudRiskResult result) {
        // If no transaction history exists, flag as new user
        if (recentTransactions == null || recentTransactions.isEmpty()) {
            result.addRiskFactor("RECIPIENT", 20, "New recipient - no transaction history");
            return;
        }
        
        // Build a set of all recipients the user has previously transacted with
        Set<String> knownRecipients = recentTransactions.stream()
            .filter(t -> t.getSender().equals(userId))  // Only user's outgoing transactions
            .map(Transaction::getReceiver)               // Extract recipient IDs
            .collect(Collectors.toSet());                // Collect into a Set for O(1) lookup
        
        String currentReceiver = transaction.getReceiver();
        
        // Check if the current recipient is in the set of known recipients
        if (!knownRecipients.contains(currentReceiver)) {
            result.addRiskFactor(
                "RECIPIENT",
                20,
                String.format("New recipient: %s (never transacted with before)", currentReceiver)
            );
        }
        // Known recipients do not add any risk points
    }
}
