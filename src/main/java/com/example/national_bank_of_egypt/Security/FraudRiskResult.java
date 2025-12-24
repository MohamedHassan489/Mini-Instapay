package com.example.national_bank_of_egypt.Security;

import java.util.ArrayList;
import java.util.List;

public class FraudRiskResult {
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
        
        public String getDescription() {
            return description;
        }
        
        public int getMinScore() {
            return minScore;
        }
        
        public int getMaxScore() {
            return maxScore;
        }
    }
    
    private int riskScore;
    private RiskLevel riskLevel;
    private List<RiskFactor> riskFactors;
    private String recommendation;
    
    public FraudRiskResult() {
        this.riskScore = 0;
        this.riskLevel = RiskLevel.LOW;
        this.riskFactors = new ArrayList<>();
        this.recommendation = "ALLOW";
    }
    
    public void addRiskFactor(String factor, int points, String description) {
        riskFactors.add(new RiskFactor(factor, points, description));
        riskScore += points;
        if (riskScore > 100) riskScore = 100;
        riskLevel = RiskLevel.fromScore(riskScore);
        updateRecommendation();
    }
    
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
    
    public int getRiskScore() {
        return riskScore;
    }
    
    public RiskLevel getRiskLevel() {
        return riskLevel;
    }
    
    public List<RiskFactor> getRiskFactors() {
        return new ArrayList<>(riskFactors);
    }
    
    public String getRecommendation() {
        return recommendation;
    }
    
    public boolean shouldBlock() {
        return riskScore >= 90;
    }
    
    public boolean requiresConfirmation() {
        return riskScore >= 70 && riskScore < 90;
    }
    
    public boolean isSuspicious() {
        return riskScore >= 50;
    }
    
    public static class RiskFactor {
        private final String factor;
        private final int points;
        private final String description;
        
        public RiskFactor(String factor, int points, String description) {
            this.factor = factor;
            this.points = points;
            this.description = description;
        }
        
        public String getFactor() {
            return factor;
        }
        
        public int getPoints() {
            return points;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
