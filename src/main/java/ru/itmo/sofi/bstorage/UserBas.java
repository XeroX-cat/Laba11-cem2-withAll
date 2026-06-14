package ru.itmo.sofi.bstorage;

import ru.itmo.sofi.datab.DatabaseBridge;
import ru.itmo.sofi.essence.user.User;
import ru.itmo.sofi.exception.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class UserBas {
    public void save(User user) throws DatabaseException {
        String sql = """
                INSERT INTO users (login, password, admin)
                VALUES (?, ?, ?)
                """;
        try (Connection connection = DatabaseBridge.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());
            statement.setBoolean(3, user.getAdmin());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Не удалось сохранить пользователя.");
        }
    }

    public User findByLogin(String login) throws DatabaseException {
        String sql = """
                SELECT login, password, admin
                FROM users
                WHERE login = ?
                """;
        try (Connection connection = DatabaseBridge.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(resultSet.getString("login"), resultSet.getString("password"), resultSet.getBoolean("admin"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Не удалось найти пользователя.");
        }
    }

    public boolean existsByLogin(String login) throws DatabaseException {
        return findByLogin(login) != null;
    }

    public void update(User user) throws DatabaseException {
        String sql = """
                UPDATE users
                SET password = ?, admin = ?
                WHERE login = ?
                """;
        try (Connection connection = DatabaseBridge.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getPassword());
            statement.setBoolean(2, user.getAdmin());
            statement.setString(3, user.getLogin());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Не удалось обновить пользователя.");
        }
    }

    public Collection<User> findAll() throws DatabaseException {
        String sql = """
            SELECT login, password, admin
            FROM users
            """;
        try (Connection connection = DatabaseBridge.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            Set<User> users = new HashSet<>();
            while (resultSet.next()) {
                User user = new User(resultSet.getString("login"), resultSet.getString("password"), resultSet.getBoolean("admin"));
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            throw new DatabaseException("Не удалось загрузить пользователей.");
        }
    }
}
