package ru.itmo.sofi.exception;

public class StorageSaveException extends RuntimeException {
    public StorageSaveException(String message) {
        super("Ошибка сохранения: " + message);
    }
}
