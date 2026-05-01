package ru.itmo.sofi.base;

import ru.itmo.sofi.exception.StorageException;

import java.nio.file.Path;
import java.util.Set;

public interface CollectionStorage<T>{
    void save(Set<T> items, Path path) throws StorageException;
    Set<T> load(Path path) throws StorageException;
}
