package ru.itmo.sofi.base;

import ru.itmo.sofi.exception.StorageLoadException;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileValidator {
    public static void validate(Path path) throws StorageLoadException {
        if (!Files.exists(path)) {
            throw new StorageLoadException("Файл не существует: " + path);
        }

        if (!Files.isRegularFile(path)) {
            throw new StorageLoadException("Это не файл: " + path);
        }

        if (!Files.isReadable(path)) {
            throw new StorageLoadException("Файл нельзя прочитать: " + path);
        }
    }
}