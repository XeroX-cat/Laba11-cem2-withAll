package ru.itmo.sofi.exception;

public class UserInputException extends RuntimeException {
    public UserInputException(String message) {
        super("Ошибка ввода: " + message);
    }
}