package com.example.national_bank_of_egypt.Security;

import com.example.national_bank_of_egypt.Models.Transaction;

/**
 * Strategy Pattern Interface for Fraud Detection.
 * 
 * <p>This interface defines the contract for binary (yes/no) fraud detection algorithms.
 * Implementations of this interface determine whether a given transaction is suspicious
 * based on their specific detection logic.</p>
 * 
 * <h2>Design Pattern: Strategy Pattern</h2>
 * <p>The Strategy Pattern allows the fraud detection algorithm to be selected at runtime.
 * Different strategies can be swapped without changing the client code that uses them.</p>
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * FraudDetectionStrategy strategy = new AmountBasedFraudDetection();
 * boolean isFraud = strategy.isSuspicious(transaction, userId);
 * }</pre>
 * 
 * @author Mini-Instapay Team
 * @version 1.0
 * @see RiskBasedFraudDetectionStrategy
 * @see FraudDetectionService
 * @see AmountBasedFraudDetection
 * @see FrequencyBasedFraudDetection
 */
public interface FraudDetectionStrategy {
    
    /**
     * Determines if a transaction is suspicious based on the strategy's detection logic.
     * 
     * <p>This is a binary detection method that returns true/false. For more detailed
     * risk assessment with scoring, use {@link RiskBasedFraudDetectionStrategy} instead.</p>
     * 
     * @param transaction The transaction to analyze for potential fraud
     * @param userId The unique identifier of the user performing the transaction
     * @return {@code true} if the transaction is considered suspicious, {@code false} otherwise
     */
    boolean isSuspicious(Transaction transaction, String userId);
}

