package ru.itmo.sofi.base;

import ru.itmo.sofi.exception.StorageException;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileValidator {
    public static void validate(Path path) throws StorageException {
        if (!Files.exists(path)) {
            throw new StorageException("Файл не существует: " + path);
        }

        if (!Files.isRegularFile(path)) {
            throw new StorageException("Это не файл: " + path);
        }

        if (!Files.isReadable(path)) {
            throw new StorageException("Файл нельзя прочитать: " + path);
        }
    }
}