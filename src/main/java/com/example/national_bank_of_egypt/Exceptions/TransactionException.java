package com.example.national_bank_of_egypt.Exceptions;

/**
 * Custom exception for transaction operations
 */
public class TransactionException extends RuntimeException {
    
    public TransactionException(String message) {
        super(message);
    }
    
    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
