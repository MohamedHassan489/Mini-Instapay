package com.example.national_bank_of_egypt.Security;

import java.util.ArrayList;
import java.util.List;

/**
 * Value Object containing the result of a fraud risk assessment.
 * 
 * <p>This class accumulates risk factors from multiple detection strategies
 * and provides a comprehensive risk assessment including score, level,
 * contributing factors, and recommended actions.</p>
 * 
 * <h2>Design Pattern: Builder/Accumulator Pattern</h2>
 * <p>Risk factors are added incrementally via {@link #addRiskFactor}, automatically
 * updating the score, level, and recommendation.</p>
 * 
 * <h2>Risk Score Ranges:</h2>
 * <table border="1">
 *   <tr><th>Score</th><th>Level</th><th>Recommendation</th></tr>
 *   <tr><td>0-30</td><td>LOW</td><td>ALLOW</td></tr>
 *   <tr><td>31-50</td><td>MEDIUM</td><td>ALLOW</td></tr>
 *   <tr><td>51-69</td><td>HIGH</td><td>FLAG_SUSPICIOUS</td></tr>
 *   <tr><td>70-89</td><td>CRITICAL</td><td>REQUIRES_CONFIRMATION</td></tr>
 *   <tr><td>90-100</td><td>CRITICAL</td><td>BLOCK</td></tr>
 * </table>
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * FraudRiskResult result = new FraudRiskResult();
 * result.addRiskFactor("AMOUNT", 30, "Large transaction");
 * result.addRiskFactor("TIME", 15, "Unusual hours");
 * 
 * if (result.shouldBlock()) {
 *     rejectTransaction();
 * } else if (result.requiresConfirmation()) {
 *     requestOTP();
 * }
 * }</pre>
 * 
 * @author Mini-Instapay Team
 * @version 1.0
 * @see FraudDetectionService
 * @see RiskBasedFraudDetectionStrategy
 */
public class FraudRiskResult {
    
    /**
     * Enumeration of risk levels based on cumulative risk score.
     * 
     * <p>Each level has a defined score range and human-readable description.
     * The level is automatically determined from the score using {@link #fromScore}.</p>
     */
    public enum RiskLevel {
        /** Low risk: Score 0-30. Transaction appears safe. */
        LOW(0, 30, "Low Risk"),
        
        /** Medium risk: Score 31-50. Some risk indicators present. */
        MEDIUM(31, 50, "Medium Risk"),
        
        /** High risk: Score 51-70. Multiple risk factors detected. */
        HIGH(51, 70, "High Risk"),
        
        /** Critical risk: Score 71-100. Strong fraud indicators. */
        CRITICAL(71, 100, "Critical Risk");
        
        /** Minimum score for this risk level */
        private final int minScore;
        
        /** Maximum score for this risk level */
        private final int maxScore;
        
        /** Human-readable description of the risk level */
        private final String description;
        
        /**
         * Constructor for RiskLevel enum values.
         * 
         * @param minScore Minimum score for this level (inclusive)
         * @param maxScore Maximum score for this level (inclusive)
         * @param description Human-readable description
         */
        RiskLevel(int minScore, int maxScore, String description) {
            this.minScore = minScore;
            this.maxScore = maxScore;
            this.description = description;
        }
        
        /**
         * Determines the risk level from a numeric score.
         * 
         * @param score The risk score (0-100)
         * @return The corresponding RiskLevel enum value
         */
        public static RiskLevel fromScore(int score) {
            if (score <= LOW.maxScore) return LOW;
            if (score <= MEDIUM.maxScore) return MEDIUM;
            if (score <= HIGH.maxScore) return HIGH;
            return CRITICAL;
        }
        
        /**
         * Gets the human-readable description of this risk level.
         * @return The description string
         */
        public String getDescription() {
            return description;
        }
        
        /**
         * Gets the minimum score for this risk level.
         * @return The minimum score (inclusive)
         */
        public int getMinScore() {
            return minScore;
        }
        
        /**
         * Gets the maximum score for this risk level.
         * @return The maximum score (inclusive)
         */
        public int getMaxScore() {
            return maxScore;
        }
    }
    
    /** Cumulative risk score (0-100, capped at 100) */
    private int riskScore;
    
    /** Current risk level based on score */
    private RiskLevel riskLevel;
    
    /** List of contributing risk factors */
    private List<RiskFactor> riskFactors;
    
    /** Recommended action: ALLOW, FLAG_SUSPICIOUS, REQUIRES_CONFIRMATION, or BLOCK */
    private String recommendation;
    
    /**
     * Constructs a new FraudRiskResult with default values.
     * 
     * <p>Initial state:
     * <ul>
     *   <li>riskScore: 0</li>
     *   <li>riskLevel: LOW</li>
     *   <li>riskFactors: empty list</li>
     *   <li>recommendation: ALLOW</li>
     * </ul>
     * </p>
     */
    public FraudRiskResult() {
        this.riskScore = 0;
        this.riskLevel = RiskLevel.LOW;
        this.riskFactors = new ArrayList<>();
        this.recommendation = "ALLOW";
    }
    
    /**
     * Adds a risk factor to the assessment result.
     * 
     * <p>This method:
     * <ol>
     *   <li>Creates a new RiskFactor and adds it to the list</li>
     *   <li>Adds the points to the cumulative risk score</li>
     *   <li>Caps the score at 100 if exceeded</li>
     *   <li>Recalculates the risk level</li>
     *   <li>Updates the recommendation</li>
     * </ol>
     * </p>
     * 
     * @param factor Category of the risk factor (e.g., "AMOUNT", "TIME", "VELOCITY")
     * @param points Risk points to add (positive integer, typically 10-30)
     * @param description Human-readable explanation of why this factor was triggered
     */
    public void addRiskFactor(String factor, int points, String description) {
        riskFactors.add(new RiskFactor(factor, points, description));
        riskScore += points;
        // Cap the score at 100 to prevent overflow
        if (riskScore > 100) riskScore = 100;
        // Recalculate risk level based on new score
        riskLevel = RiskLevel.fromScore(riskScore);
        // Update recommendation based on new score
        updateRecommendation();
    }
    
    /**
     * Updates the recommendation based on the current risk score.
     * 
     * <p>Recommendation thresholds:
     * <ul>
     *   <li>Score >= 90: BLOCK (reject transaction)</li>
     *   <li>Score >= 70: REQUIRES_CONFIRMATION (request OTP)</li>
     *   <li>Score >= 50: FLAG_SUSPICIOUS (allow but flag for review)</li>
     *   <li>Score < 50: ALLOW (process normally)</li>
     * </ul>
     * </p>
     */
    private void updateRecommendation() {
        if (riskScore >= 90) {
            recommendation = "BLOCK";
        } else if (riskScore >= 70) {
            recommendation = "REQUIRES_CONFIRMATION";
        } else if (riskScore >= 50) {
            recommendation = "FLAG_SUSPICIOUS";
        } else {
            recommendation = "ALLOW";
        }
    }
    
    /**
     * Gets the cumulative risk score.
     * @return Risk score from 0 to 100
     */
    public int getRiskScore() {
        return riskScore;
    }
    
    /**
     * Gets the risk level based on the current score.
     * @return The RiskLevel enum value (LOW, MEDIUM, HIGH, or CRITICAL)
     */
    public RiskLevel getRiskLevel() {
        return riskLevel;
    }
    
    /**
     * Gets a copy of all contributing risk factors.
     * 
     * <p>Returns a defensive copy to prevent external modification.</p>
     * 
     * @return New ArrayList containing all risk factors
     */
    public List<RiskFactor> getRiskFactors() {
        return new ArrayList<>(riskFactors);
    }
    
    /**
     * Gets the recommended action based on risk assessment.
     * @return One of: "ALLOW", "FLAG_SUSPICIOUS", "REQUIRES_CONFIRMATION", "BLOCK"
     */
    public String getRecommendation() {
        return recommendation;
    }
    
    /**
     * Checks if the transaction should be blocked.
     * @return {@code true} if risk score >= 90
     */
    public boolean shouldBlock() {
        return riskScore >= 90;
    }
    
    /**
     * Checks if the transaction requires additional confirmation (e.g., OTP).
     * @return {@code true} if risk score is between 70 and 89 (inclusive)
     */
    public boolean requiresConfirmation() {
        return riskScore >= 70 && riskScore < 90;
    }
    
    /**
     * Checks if the transaction is suspicious and should be flagged.
     * @return {@code true} if risk score >= 50
     */
    public boolean isSuspicious() {
        return riskScore >= 50;
    }
    
    /**
     * Immutable data class representing a single risk factor.
     * 
     * <p>Contains the category, point contribution, and description
     * of a detected fraud indicator.</p>
     */
    public static class RiskFactor {
        /** Category of the risk factor (e.g., "AMOUNT", "TIME") */
        private final String factor;
        
        /** Points contributed to the total risk score */
        private final int points;
        
        /** Human-readable explanation of the risk factor */
        private final String description;
        
        /**
         * Constructs a new RiskFactor.
         * 
         * @param factor Category identifier (e.g., "AMOUNT", "VELOCITY")
         * @param points Risk points contributed
         * @param description Human-readable explanation
         */
        public RiskFactor(String factor, int points, String description) {
            this.factor = factor;
            this.points = points;
            this.description = description;
        }
        
        /**
         * Gets the risk factor category.
         * @return Factor category string
         */
        public String getFactor() {
            return factor;
        }
        
        /**
         * Gets the points contributed by this factor.
         * @return Point value
         */
        public int getPoints() {
            return points;
        }
        
        /**
         * Gets the human-readable description.
         * @return Description string
         */
        public String getDescription() {
            return description;
        }
    }
}
