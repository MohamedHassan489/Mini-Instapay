package com.example.national_bank_of_egypt.Security;

import com.example.national_bank_of_egypt.Models.Transaction;
import java.util.List;

/**
 * Strategy Pattern Interface for Risk-Based Fraud Detection.
 * 
 * <p>This interface defines the contract for advanced fraud detection algorithms that
 * provide risk scoring instead of binary yes/no results. Implementations analyze
 * transactions and contribute risk factors to a cumulative result.</p>
 * 
 * <h2>Design Pattern: Strategy Pattern</h2>
 * <p>Allows different risk assessment algorithms to be interchangeable at runtime.
 * Each strategy evaluates specific fraud indicators and adds weighted risk factors.</p>
 * 
 * <h2>Key Difference from FraudDetectionStrategy:</h2>
 * <ul>
 *   <li><b>FraudDetectionStrategy:</b> Returns binary true/false</li>
 *   <li><b>RiskBasedFraudDetectionStrategy:</b> Adds weighted risk factors to accumulator</li>
 * </ul>
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * FraudRiskResult result = new FraudRiskResult();
 * RiskBasedFraudDetectionStrategy strategy = new AmountBasedFraudDetection();
 * strategy.assessRisk(transaction, userId, history, result);
 * // result now contains risk factors from this strategy
 * }</pre>
 * 
 * @author Mini-Instapay Team
 * @version 1.0
 * @see FraudDetectionStrategy
 * @see FraudRiskResult
 * @see FraudDetectionService
 */
public interface RiskBasedFraudDetectionStrategy {
    
    /**
     * Assesses the fraud risk of a transaction and adds risk factors to the result.
     * 
     * <p>This method analyzes the transaction using the strategy's specific detection
     * logic and adds any identified risk factors to the provided result object.
     * Multiple strategies can be applied sequentially, each contributing to the
     * cumulative risk score.</p>
     * 
     * @param transaction The transaction to analyze for potential fraud
     * @param userId The unique identifier of the user performing the transaction
     * @param recentTransactions List of user's recent transactions for pattern analysis
     *                          (may be null or empty for new users)
     * @param result The accumulator object to add risk factors to. This object is
     *               mutated by this method to include any detected risk factors.
     */
    void assessRisk(Transaction transaction, String userId, List<Transaction> recentTransactions, FraudRiskResult result);
}
