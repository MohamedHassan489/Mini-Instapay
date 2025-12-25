package com.example.national_bank_of_egypt.Security;

import com.example.national_bank_of_egypt.Models.Transaction;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Amount Pattern Detection Strategy (Behavioral Analysis).
 * 
 * <p>This strategy detects potentially fraudulent transactions by comparing
 * the transaction amount against the user's historical transaction patterns.
 * Transactions that significantly deviate from normal behavior are flagged.</p>
 * 
 * <h2>Design Pattern: Strategy Pattern (Concrete Strategy)</h2>
 * <p>Implements {@link RiskBasedFraudDetectionStrategy} for risk-scored assessment.</p>
 * 
 * <h2>Detection Logic:</h2>
 * <table border="1">
 *   <tr><th>Condition</th><th>Risk Points</th><th>Description</th></tr>
 *   <tr><td>Amount > 3× user's average</td><td>25</td><td>Significantly above normal pattern</td></tr>
 *   <tr><td>Amount > 2× user's maximum</td><td>20</td><td>Exceeds historical maximum</td></tr>
 *   <tr><td>Within normal range</td><td>0</td><td>Consistent with history</td></tr>
 * </table>
 * 
 * <h2>Rationale:</h2>
 * <p>This behavioral analysis approach detects anomalies such as:
 * <ul>
 *   <li>Account takeover where attacker makes unusually large transfers</li>
 *   <li>Sudden behavior change indicating potential fraud</li>
 *   <li>Authorized user under duress making atypical transactions</li>
 * </ul>
 * </p>
 * 
 * <h2>Edge Case:</h2>
 * <p>If the user has no transaction history, this strategy does not add any
 * risk factors (graceful degradation).</p>
 * 
 * @author Mini-Instapay Team
 * @version 1.0
 * @see RiskBasedFraudDetectionStrategy
 * @see AmountBasedFraudDetection
 */
public class AmountPatternDetection implements RiskBasedFraudDetectionStrategy {
    
    /**
     * Multiplier for average amount comparison.
     * Transactions exceeding this multiple of the user's average are flagged.
     */
    private static final double SUSPICIOUS_MULTIPLIER = 3.0;
    
    /**
     * Assesses amount pattern-based fraud risk for a transaction.
     * 
     * <p>Compares the current transaction amount against the user's historical
     * transaction patterns:
     * <ul>
     *   <li>If amount exceeds 3× the user's average: 25 risk points</li>
     *   <li>If amount exceeds 2× the user's historical max: 20 risk points</li>
     *   <li>Otherwise: No risk points added</li>
     * </ul>
     * </p>
     * 
     * @param transaction The transaction to analyze
     * @param userId The user performing the transaction
     * @param recentTransactions User's recent transaction history for pattern analysis
     * @param builder The Builder object to add risk factors to
     */
    @Override
    public void assessRisk(Transaction transaction, String userId, List<Transaction> recentTransactions, FraudRiskResult.Builder builder) {
        // Return early if no transaction history is available
        if (recentTransactions == null || recentTransactions.isEmpty()) {
            return;
        }
        
        // Filter to only include the user's outgoing transactions
        List<Transaction> userTransactions = recentTransactions.stream()
            .filter(t -> t.getSender().equals(userId))
            .collect(Collectors.toList());
        
        // Return if user has no previous transactions
        if (userTransactions.isEmpty()) {
            return;
        }
        
        // Calculate the user's average transaction amount
        double averageAmount = userTransactions.stream()
            .mapToDouble(Transaction::getAmount)
            .average()
            .orElse(0.0);
        
        // Calculate the user's maximum transaction amount
        double maxAmount = userTransactions.stream()
            .mapToDouble(Transaction::getAmount)
            .max()
            .orElse(0.0);
        
        double currentAmount = transaction.getAmount();
        
        // Check if current amount significantly exceeds user's average (3x or more)
        if (averageAmount > 0 && currentAmount > averageAmount * SUSPICIOUS_MULTIPLIER) {
            builder.addRiskFactor(
                "AMOUNT_PATTERN",
                25,
                String.format("Amount ($%.2f) is %.1fx user's average ($%.2f)", 
                    currentAmount, currentAmount / averageAmount, averageAmount)
            );
        // Check if current amount exceeds 2x user's historical maximum
        } else if (maxAmount > 0 && currentAmount > maxAmount * 2.0) {
            builder.addRiskFactor(
                "AMOUNT_PATTERN",
                20,
                String.format("Amount ($%.2f) is 2x user's largest transaction ($%.2f)", 
                    currentAmount, maxAmount)
            );
        }
        // Amounts within normal pattern do not add risk points
    }
}
