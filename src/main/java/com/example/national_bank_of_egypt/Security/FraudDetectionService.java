package com.example.national_bank_of_egypt.Security;

import com.example.national_bank_of_egypt.Models.Transaction;
import java.util.ArrayList;
import java.util.List;

/**
 * Central Fraud Detection Service implementing Singleton and Composite patterns.
 * 
 * <p>This service coordinates multiple fraud detection strategies to provide
 * comprehensive fraud analysis for banking transactions. It supports both
 * legacy binary detection and advanced risk-scored assessment.</p>
 * 
 * <h2>Design Patterns Used:</h2>
 * <ul>
 *   <li><b>Singleton Pattern:</b> Ensures only one instance exists application-wide</li>
 *   <li><b>Composite Pattern:</b> Combines multiple strategies into unified assessment</li>
 *   <li><b>Strategy Pattern:</b> Uses interchangeable detection algorithms</li>
 * </ul>
 * 
 * <h2>Default Strategies:</h2>
 * <ul>
 *   <li>{@link AmountBasedFraudDetection} - Large transaction detection</li>
 *   <li>{@link VelocityBasedFraudDetection} - Rapid transaction detection</li>
 *   <li>{@link RecipientPatternDetection} - New recipient detection</li>
 *   <li>{@link AmountPatternDetection} - Behavioral anomaly detection</li>
 *   <li>{@link TimeBasedFraudDetection} - Unusual time detection</li>
 * </ul>
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * FraudDetectionService service = FraudDetectionService.getInstance();
 * FraudRiskResult result = service.assessRisk(transaction, userId, history);
 * if (result.shouldBlock()) {
 *     rejectTransaction();
 * }
 * }</pre>
 * 
 * @author Mini-Instapay Team
 * @version 1.0
 * @see FraudDetectionStrategy
 * @see RiskBasedFraudDetectionStrategy
 * @see FraudRiskResult
 */
public class FraudDetectionService {
    
    /** Singleton instance of the service */
    private static FraudDetectionService instance;
    
    /** List of binary (yes/no) fraud detection strategies */
    private final List<FraudDetectionStrategy> strategies;
    
    /** List of risk-scored fraud detection strategies */
    private final List<RiskBasedFraudDetectionStrategy> riskStrategies;

    /**
     * Private constructor to enforce Singleton pattern.
     * Initializes default fraud detection strategies.
     */
    private FraudDetectionService() {
        this.strategies = new ArrayList<>();
        this.riskStrategies = new ArrayList<>();
        
        // Initialize default binary strategies (for backward compatibility)
        this.strategies.add(new AmountBasedFraudDetection());
        
        // Initialize risk-based strategies for comprehensive fraud detection
        this.riskStrategies.add(new AmountBasedFraudDetection());    // Detects large amounts
        this.riskStrategies.add(new VelocityBasedFraudDetection());  // Detects rapid transactions
        this.riskStrategies.add(new RecipientPatternDetection());    // Detects new recipients
        this.riskStrategies.add(new AmountPatternDetection());       // Detects behavioral anomalies
        this.riskStrategies.add(new TimeBasedFraudDetection());      // Detects unusual times
    }

    /**
     * Returns the singleton instance of FraudDetectionService.
     * 
     * <p>Thread-safe implementation using synchronized keyword to prevent
     * race conditions during instance creation in multi-threaded environments.</p>
     * 
     * @return The singleton instance of FraudDetectionService
     */
    public static synchronized FraudDetectionService getInstance() {
        if (instance == null) {
            instance = new FraudDetectionService();
        }
        return instance;
    }

    /**
     * Registers a new binary fraud detection strategy.
     * 
     * <p>Added strategies will be evaluated during {@link #detectFraud} calls.</p>
     * 
     * @param strategy The strategy to add to the detection pipeline
     */
    public void addStrategy(FraudDetectionStrategy strategy) {
        strategies.add(strategy);
    }
    
    /**
     * Registers a new risk-based fraud detection strategy.
     * 
     * <p>Added strategies will be evaluated during {@link #assessRisk} calls.</p>
     * 
     * @param strategy The risk-based strategy to add to the detection pipeline
     */
    public void addRiskStrategy(RiskBasedFraudDetectionStrategy strategy) {
        riskStrategies.add(strategy);
    }

    /**
     * Legacy binary fraud detection method.
     * 
     * <p><b>Note:</b> This method is maintained for backward compatibility.
     * For new implementations, use {@link #assessRisk} instead for more
     * detailed risk information.</p>
     * 
     * @param transaction The transaction to analyze
     * @param userId The user performing the transaction
     * @param recentTransactions User's recent transaction history
     * @return {@code true} if any strategy detects fraud, {@code false} otherwise
     * @deprecated Use {@link #assessRisk} for more detailed risk assessment
     */
    public boolean detectFraud(Transaction transaction, String userId, List<Transaction> recentTransactions) {
        // Iterate through all registered binary strategies
        for (FraudDetectionStrategy strategy : strategies) {
            // Special handling for frequency-based strategy that needs transaction history
            if (strategy instanceof FrequencyBasedFraudDetection) {
                FrequencyBasedFraudDetection freqStrategy = (FrequencyBasedFraudDetection) strategy;
                freqStrategy.recentTransactions = recentTransactions;
            }
            // If any strategy flags the transaction as suspicious, return true immediately
            if (strategy.isSuspicious(transaction, userId)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Advanced risk-based fraud assessment method.
     * 
     * <p>Evaluates the transaction against all registered risk-based strategies
     * and returns a comprehensive risk assessment including:</p>
     * <ul>
     *   <li>Total risk score (0-100)</li>
     *   <li>Risk level (LOW, MEDIUM, HIGH, CRITICAL)</li>
     *   <li>List of contributing risk factors</li>
     *   <li>Recommended action (ALLOW, FLAG, CONFIRM, BLOCK)</li>
     * </ul>
     * 
     * @param transaction The transaction to analyze
     * @param userId The user performing the transaction
     * @param recentTransactions User's recent transaction history (may be null)
     * @return FraudRiskResult containing comprehensive risk assessment
     */
    public FraudRiskResult assessRisk(Transaction transaction, String userId, List<Transaction> recentTransactions) {
        // Create a new result object to accumulate risk factors
        FraudRiskResult result = new FraudRiskResult();
        
        // Add frequency-based strategy dynamically if we have recent transactions
        // This strategy is added dynamically because it requires transaction history
        if (recentTransactions != null && !recentTransactions.isEmpty()) {
            FrequencyBasedFraudDetection freqStrategy = new FrequencyBasedFraudDetection(recentTransactions);
            freqStrategy.assessRisk(transaction, userId, recentTransactions, result);
        }
        
        // Run all registered risk-based strategies
        // Each strategy adds its risk factors to the result object
        for (RiskBasedFraudDetectionStrategy strategy : riskStrategies) {
            try {
                strategy.assessRisk(transaction, userId, recentTransactions, result);
            } catch (Exception e) {
                // Log error but continue with other strategies
                // One failing strategy should not break the entire assessment
                e.printStackTrace();
            }
        }
        
        return result;
    }
}

