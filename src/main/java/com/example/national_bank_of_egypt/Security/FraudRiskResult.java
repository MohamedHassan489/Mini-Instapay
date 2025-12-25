package com.example.national_bank_of_egypt.Security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable Fraud Risk Assessment Result using the Classic Builder Pattern.
 * 
 * <p>This class represents the comprehensive result of a fraud risk assessment,
 * containing the risk score, risk level, contributing factors, and recommended action.
 * The class is immutable - once built, its state cannot be changed.</p>
 * 
 * <h2>Design Pattern: Classic Builder Pattern</h2>
 * <p>The Builder Pattern is used to:</p>
 * <ul>
 *   <li>Separate the construction of a complex object from its representation</li>
 *   <li>Allow step-by-step construction with fluent API</li>
 *   <li>Ensure the final object is immutable and valid</li>
 *   <li>Accumulate risk factors from multiple detection strategies</li>
 * </ul>
 * 
 * <h2>Builder Pattern Structure:</h2>
 * <pre>
 * ┌─────────────────────────────────────────────────────────────┐
 * │                    FraudRiskResult (Product)                │
 * │  - immutable fields (riskScore, riskLevel, etc.)           │
 * │  - private constructor (only Builder can instantiate)      │
 * │  - getters only (no setters)                               │
 * └─────────────────────────────────────────────────────────────┘
 *                              ▲
 *                              │ creates
 * ┌─────────────────────────────────────────────────────────────┐
 * │                    Builder (Static Inner Class)            │
 * │  - mutable state during construction                       │
 * │  - addRiskFactor() - fluent method, returns this           │
 * │  - build() - creates immutable FraudRiskResult             │
 * └─────────────────────────────────────────────────────────────┘
 * </pre>
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Create builder and accumulate risk factors
 * FraudRiskResult.Builder builder = FraudRiskResult.builder();
 * builder.addRiskFactor("AMOUNT", 30, "Large transaction");
 * builder.addRiskFactor("TIME", 15, "Unusual hours");
 * 
 * // Build the immutable result
 * FraudRiskResult result = builder.build();
 * 
 * // Use the result
 * if (result.shouldBlock()) {
 *     // Block the transaction
 * }
 * }</pre>
 * 
 * <h2>Thread Safety:</h2>
 * <p>The FraudRiskResult object is immutable and therefore thread-safe.
 * The Builder is NOT thread-safe and should be used by a single thread.</p>
 * 
 * @author Mini-Instapay Team
 * @version 2.0 (Refactored to Classic Builder Pattern)
 * @see FraudDetectionService
 * @see RiskBasedFraudDetectionStrategy
 */
public final class FraudRiskResult {

    /**
     * Enumeration representing the risk level categories.
     * 
     * <p>Risk levels are determined by the cumulative risk score:
     * <ul>
     *   <li>LOW: 0-30 points - Transaction is safe</li>
     *   <li>MEDIUM: 31-50 points - Minor concerns, monitor</li>
     *   <li>HIGH: 51-70 points - Significant risk, may require confirmation</li>
     *   <li>CRITICAL: 71-100 points - High risk, may be blocked</li>
     * </ul>
     * </p>
     */
    public enum RiskLevel {
        LOW(0, 30, "Low Risk"),
        MEDIUM(31, 50, "Medium Risk"),
        HIGH(51, 70, "High Risk"),
        CRITICAL(71, 100, "Critical Risk");

        private final int minScore;
        private final int maxScore;
        private final String description;

        RiskLevel(int minScore, int maxScore, String description) {
            this.minScore = minScore;
            this.maxScore = maxScore;
            this.description = description;
        }

        public static RiskLevel fromScore(int score) {
            if (score <= LOW.maxScore) return LOW;
            if (score <= MEDIUM.maxScore) return MEDIUM;
            if (score <= HIGH.maxScore) return HIGH;
            return CRITICAL;
        }

        /**
         * Gets the human-readable description of this risk level.
         * @return The risk level description
         */
        public String getDescription() { return description; }
        
        /**
         * Gets the minimum score for this risk level.
         * @return The minimum score threshold
         */
        public int getMinScore() { return minScore; }
        
        /**
         * Gets the maximum score for this risk level.
         * @return The maximum score threshold
         */
        public int getMaxScore() { return maxScore; }
    }

    // ==================== IMMUTABLE FIELDS ====================
    // These fields can only be set through the Builder
    
    /** The cumulative risk score (0-100) */
    private final int riskScore;
    
    private final RiskLevel riskLevel;
    private final List<RiskFactor> riskFactors;
    private final String recommendation;

    // Private constructor: only Builder can create it
    private FraudRiskResult(int riskScore, RiskLevel riskLevel, List<RiskFactor> riskFactors, String recommendation) {
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.riskFactors = riskFactors;
        this.recommendation = recommendation;
    }

    // ==================== FACTORY METHOD ====================
    
    /**
     * Creates a new Builder instance to construct a FraudRiskResult.
     * 
     * <p>This is the entry point for the Builder pattern. Usage:</p>
     * <pre>{@code
     * FraudRiskResult result = FraudRiskResult.builder()
     *     .addRiskFactor("AMOUNT", 30, "Large transaction")
     *     .addRiskFactor("TIME", 15, "Unusual hours")
     *     .build();
     * }</pre>
     * 
     * @return A new Builder instance for constructing FraudRiskResult
     */
    public static Builder builder() {
        return new Builder();
    }

    // ==================== GETTERS (READ-ONLY ACCESS) ====================
    
    /**
     * Gets the cumulative risk score.
     * @return Risk score from 0 (safe) to 100 (extremely risky)
     */
    public int getRiskScore() { return riskScore; }
    
    /**
     * Gets the categorized risk level.
     * @return The RiskLevel enum value (LOW, MEDIUM, HIGH, CRITICAL)
     */
    public RiskLevel getRiskLevel() { return riskLevel; }

    /**
     * Gets the list of all contributing risk factors.
     * <p>The returned list is unmodifiable to preserve immutability.</p>
     * @return Unmodifiable list of RiskFactor objects
     */
    public List<RiskFactor> getRiskFactors() { return riskFactors; }

    /**
     * Gets the recommended action based on the risk assessment.
     * @return One of: "ALLOW", "FLAG_SUSPICIOUS", "REQUIRES_CONFIRMATION", "BLOCK"
     */
    public String getRecommendation() { return recommendation; }

    // ==================== DECISION HELPER METHODS ====================
    
    /**
     * Checks if the transaction should be blocked (score ≥ 90).
     * @return true if the transaction should be blocked
     */
    public boolean shouldBlock() { return riskScore >= 90; }
    
    /**
     * Checks if the transaction requires user confirmation (score 70-89).
     * @return true if confirmation is required
     */
    public boolean requiresConfirmation() { return riskScore >= 70 && riskScore < 90; }
    
    /**
     * Checks if the transaction is suspicious (score ≥ 50).
     * @return true if the transaction is flagged as suspicious
     */
    public boolean isSuspicious() { return riskScore >= 50; }

    // ==================== RISK FACTOR (VALUE OBJECT) ====================
    
    /**
     * Immutable value object representing a single risk factor.
     * 
     * <p>Each RiskFactor captures:</p>
     * <ul>
     *   <li><b>factor:</b> Category identifier (e.g., "AMOUNT", "TIME", "VELOCITY")</li>
     *   <li><b>points:</b> Risk points contributed (typically 10-30)</li>
     *   <li><b>description:</b> Human-readable explanation</li>
     * </ul>
     */
    public static final class RiskFactor {
        private final String factor;
        private final int points;
        private final String description;

        /**
         * Constructs a new RiskFactor.
         * 
         * @param factor The risk category identifier
         * @param points The risk points contributed
         * @param description Human-readable explanation
         */
        public RiskFactor(String factor, int points, String description) {
            this.factor = factor;
            this.points = points;
            this.description = description;
        }

        /** @return The risk category identifier */
        public String getFactor() { return factor; }
        
        /** @return The risk points contributed */
        public int getPoints() { return points; }
        
        /** @return Human-readable explanation of the risk */
        public String getDescription() { return description; }
    }

    // ==================== CLASSIC BUILDER PATTERN ====================
    
    /**
     * Builder class for constructing FraudRiskResult objects.
     * 
     * <p>The Builder accumulates risk factors from multiple detection strategies,
     * then constructs an immutable FraudRiskResult with computed fields.</p>
     * 
     * <h2>Builder Pattern Benefits:</h2>
     * <ul>
     *   <li>Fluent API with method chaining</li>
     *   <li>Encapsulates complex construction logic</li>
     *   <li>Automatically computes derived fields (riskLevel, recommendation)</li>
     *   <li>Ensures immutability of the final product</li>
     * </ul>
     * 
     * <h2>Usage:</h2>
     * <pre>{@code
     * FraudRiskResult result = FraudRiskResult.builder()
     *     .addRiskFactor("AMOUNT", 30, "Large amount")
     *     .addRiskFactor("VELOCITY", 20, "Rapid transactions")
     *     .build();
     * }</pre>
     */
    public static final class Builder {
        
        /** Accumulated risk score (mutable during building) */
        private int riskScore = 0;
        
        /** List of risk factors (mutable during building) */
        private final List<RiskFactor> riskFactors = new ArrayList<>();

        /**
         * Private constructor - use FraudRiskResult.builder() to create.
         */
        private Builder() {}

        /**
         * Adds a risk factor and updates the cumulative score.
         * 
         * <p>This is a fluent method that returns the Builder for chaining:
         * <pre>{@code
         * builder.addRiskFactor("A", 10, "desc").addRiskFactor("B", 20, "desc");
         * }</pre>
         * </p>
         * 
         * <p>The risk score is capped at 100 to prevent overflow.</p>
         * 
         * @param factor The risk category identifier (e.g., "AMOUNT", "TIME")
         * @param points The risk points to add (must be > 0, otherwise ignored)
         * @param description Human-readable explanation of the risk
         * @return This Builder instance for method chaining
         */
        public Builder addRiskFactor(String factor, int points, String description) {
            // Ignore invalid points (must be positive)
            if (points <= 0) return this;
            
            // Add the risk factor to the list
            riskFactors.add(new RiskFactor(factor, points, description));
            
            // Update cumulative score (capped at 100)
            riskScore += points;
            if (riskScore > 100) riskScore = 100;
            
            return this; // Return this for fluent chaining
        }

        /**
         * Builds the final immutable FraudRiskResult.
         * 
         * <p>This method:</p>
         * <ol>
         *   <li>Computes the RiskLevel from the accumulated score</li>
         *   <li>Computes the recommendation based on the score</li>
         *   <li>Creates an unmodifiable copy of the risk factors list</li>
         *   <li>Constructs and returns the immutable FraudRiskResult</li>
         * </ol>
         * 
         * <p>After calling build(), the Builder can still be used to create
         * additional FraudRiskResult objects (though this is not typical usage).</p>
         * 
         * @return A new immutable FraudRiskResult object
         */
        public FraudRiskResult build() {
            // Compute derived fields
            RiskLevel level = RiskLevel.fromScore(riskScore);
            String recommendation = computeRecommendation(riskScore);

            // Create immutable copy of risk factors list
            List<RiskFactor> immutableFactors =
                    Collections.unmodifiableList(new ArrayList<>(riskFactors));

            // Construct and return the immutable product
            return new FraudRiskResult(riskScore, level, immutableFactors, recommendation);
        }

        /**
         * Computes the recommended action based on the risk score.
         * 
         * @param score The cumulative risk score
         * @return The recommended action string
         */
        private static String computeRecommendation(int score) {
            if (score >= 90) return "BLOCK";
            if (score >= 70) return "REQUIRES_CONFIRMATION";
            if (score >= 50) return "FLAG_SUSPICIOUS";
            return "ALLOW";
        }
    }
}
