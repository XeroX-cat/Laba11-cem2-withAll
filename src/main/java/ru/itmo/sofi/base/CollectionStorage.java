package ru.itmo.sofi.base;

import ru.itmo.sofi.exception.StorageLoadException;
import ru.itmo.sofi.exception.StorageSaveException;

import java.nio.file.Path;
import java.util.Set;

public interface CollectionStorage<T>{
    void save(Set<T> items, Path path) throws StorageSaveException;
    Set<T> load(Path path) throws StorageLoadException;
}
