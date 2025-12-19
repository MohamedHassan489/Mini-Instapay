package com.example.national_bank_of_egypt.Security;

import com.example.national_bank_of_egypt.Models.Transaction;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RecipientPatternDetection implements RiskBasedFraudDetectionStrategy {
    
    @Override
    public void assessRisk(Transaction transaction, String userId, List<Transaction> recentTransactions, FraudRiskResult result) {
        if (recentTransactions == null || recentTransactions.isEmpty()) {
            result.addRiskFactor("RECIPIENT", 20, "New recipient - no transaction history");
            return;
        }
        
        Set<String> knownRecipients = recentTransactions.stream()
            .filter(t -> t.getSender().equals(userId))
            .map(Transaction::getReceiver)
            .collect(Collectors.toSet());
        
        String currentReceiver = transaction.getReceiver();
        
        if (!knownRecipients.contains(currentReceiver)) {
            result.addRiskFactor(
                "RECIPIENT",
                20,
                String.format("New recipient: %s (never transacted with before)", currentReceiver)
            );
        }
    }
}
