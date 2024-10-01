package io.codingogo.redissondemo.exception;

public class InsufficienFundsException extends RuntimeException {
    public InsufficienFundsException(String message) {
        super(message);
    }
}
