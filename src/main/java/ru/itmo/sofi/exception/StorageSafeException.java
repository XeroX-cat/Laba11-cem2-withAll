package ru.itmo.sofi.exception;

public class StorageSafeException extends RuntimeException{
    public StorageSafeException(String message) {
        super("Ошибка системы паролей: " + message);
    }
}
