package ru.itmo.sofi.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ru.itmo.sofi.exception.StorageLoadException;
import ru.itmo.sofi.exception.StorageSaveException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

public class JsonCollectionStorage<T> extends AbstractFileStorage<T> {
    private final ObjectMapper objectMapper;
    private final TypeReference<Set<T>> typeReference;

    public JsonCollectionStorage(TypeReference<Set<T>> typeReference) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.typeReference = typeReference;
    }

    @Override
    public void save(Set<T> items, Path path) throws StorageSaveException {
        try {
            super.ensureParentDirectoryExists(path);
            this.objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), items);
        } catch (IOException e) {
            throw new StorageSaveException("Проверьте путь, доступ к папке и корректность сохраняемых данных.");
        }
    }

    @Override
    public Set load(Path path) throws StorageLoadException {
        try {
            FileValidator.validate(path);
            return this.objectMapper.readValue(path.toFile(), this.typeReference);
        } catch (IOException e) {
            throw new StorageLoadException("Проверьте существование и корректность файла.");
        }
    }
}
