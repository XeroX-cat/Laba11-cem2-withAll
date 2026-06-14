package ru.itmo.sofi.datab;

import ru.itmo.sofi.exception.DatabaseException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseBridge {
    private DatabaseBridge() {
    }
    private static Properties loadProperties() throws DatabaseException {
        Properties properties = new Properties();
        try (InputStream input = DatabaseBridge.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new DatabaseException("Файл db.properties не найден.");
            }
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new DatabaseException("Не удалось загрузить настройки.");
        }
    }

    public static Connection getConnection() throws DatabaseException {
        Properties properties = loadProperties();
        try {
            return DriverManager.getConnection(
                    properties.getProperty("db.url"),
                    properties.getProperty("db.user"),
                    properties.getProperty("db.password")
            );
        } catch (SQLException e) {
            throw new DatabaseException("Не удалось подключиться.");
        }
    }
}
