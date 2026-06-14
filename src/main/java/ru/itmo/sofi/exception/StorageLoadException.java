package ru.itmo.sofi.exception;

public class StorageLoadException extends RuntimeException {
    public StorageLoadException(String message) {
        super("Ошибка загрузки: " + message);
    }
}
