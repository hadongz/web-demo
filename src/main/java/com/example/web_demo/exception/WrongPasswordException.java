package com.example.web_demo.exception;

public class WrongPasswordException extends RuntimeException {
    
    public WrongPasswordException() {
        super("Wrong password");
    }
}
