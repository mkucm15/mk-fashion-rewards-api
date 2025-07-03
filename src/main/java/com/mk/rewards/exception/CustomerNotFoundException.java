package com.mk.rewards.exception;

/**
 * Custom exception thrown when a requested customer is not found
 * in the transaction records.
 */
public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String message) {
        super(message);
    }
}