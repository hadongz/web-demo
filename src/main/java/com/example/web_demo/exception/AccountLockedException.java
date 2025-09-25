package com.example.web_demo.exception;

public class AccountLockedException extends RuntimeException {
    
    public AccountLockedException() {
        super("Account Locked");
    }
}
