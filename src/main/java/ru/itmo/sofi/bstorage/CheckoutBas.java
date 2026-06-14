package ru.itmo.sofi.bstorage;

import ru.itmo.sofi.datab.DatabaseBridge;
import ru.itmo.sofi.essence.checkout.Checkout;
import ru.itmo.sofi.essence.checkout.ReturnCondition;
import ru.itmo.sofi.exception.DatabaseException;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class CheckoutBas {
    public void save(Checkout checkout) throws DatabaseException {
        String sql = """
                INSERT INTO checkout
                (id, instrument_id, username, comment, taken_at, returned_at, return_condition, owner_username, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseBridge.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, checkout.getId());
            statement.setLong(2, checkout.getInstrumentId());
            statement.setString(3, checkout.getUsername());
            statement.setString(4, checkout.getComment());
            statement.setTimestamp(5, Timestamp.from(checkout.getTakenAt()));
            if (checkout.getReturnedAt() == null) {
                statement.setNull(6, Types.TIMESTAMP);
            } else {
                statement.setTimestamp(6, Timestamp.from(checkout.getReturnedAt()));
            }
            if (checkout.getReturnCondition() == null) {
                statement.setNull(7, Types.VARCHAR);
            } else {
                statement.setString(7, checkout.getReturnCondition().name());
            }
            statement.setString(8, checkout.getOwnerUsername());
            statement.setTimestamp(9, Timestamp.from(checkout.getCreatedAt()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Не удалось сохранить выдачу.");
        }
    }

    public Checkout findById(long id) throws DatabaseException {
        String sql = "SELECT * FROM checkout WHERE id = ?";
        try (Connection connection = DatabaseBridge.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return toCheckout(resultSet);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Не удалось найти выдачу.");
        }
    }

    public Set<Checkout> findAll() throws DatabaseException {
        String sql = "SELECT * FROM checkout";
        try (Connection connection = DatabaseBridge.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            Set<Checkout> checkouts = new HashSet<>();
            while (resultSet.next()) {
                checkouts.add(toCheckout(resultSet));
            }
            return checkouts;
        } catch (SQLException e) {
            throw new DatabaseException("Не удалось загрузить выдачи.");
        }
    }

    public void update(Checkout checkout) throws DatabaseException {
        String sql = """
                UPDATE checkout
                SET instrument_id = ?,
                    username = ?,
                    comment = ?,
                    taken_at = ?,
                    returned_at = ?,
                    return_condition = ?,
                    owner_username = ?
                WHERE id = ?
                """;
        try (Connection connection = DatabaseBridge.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, checkout.getInstrumentId());
            statement.setString(2, checkout.getUsername());
            statement.setString(3, checkout.getComment());
            statement.setTimestamp(4, Timestamp.from(checkout.getTakenAt()));
            if (checkout.getReturnedAt() == null) {
                statement.setNull(5, Types.TIMESTAMP);
            } else {
                statement.setTimestamp(5, Timestamp.from(checkout.getReturnedAt()));
            }
            if (checkout.getReturnCondition() == null) {
                statement.setNull(6, Types.VARCHAR);
            } else {
                statement.setString(6, checkout.getReturnCondition().name());
            }
            statement.setString(7, checkout.getOwnerUsername());
            statement.setLong(8, checkout.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Не удалось обновить выдачу.");
        }
    }

    public void delete(long id) throws DatabaseException {
        String sql = "DELETE FROM checkout WHERE id = ?";
        try (Connection connection = DatabaseBridge.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Не удалось удалить выдачу.");
        }
    }

    public int count() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM checkout";
        try (Connection connection = DatabaseBridge.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Не удалось посчитать выдачи.");
        }
    }

    private Checkout toCheckout(ResultSet resultSet) throws SQLException {
        Timestamp returnedTimestamp = resultSet.getTimestamp("returned_at");
        String conditionString = resultSet.getString("return_condition");
        return new Checkout(resultSet.getLong("id"), resultSet.getLong("instrument_id"), resultSet.getString("username"), resultSet.getString("comment"), resultSet.getTimestamp("taken_at").toInstant(), returnedTimestamp == null ? null : returnedTimestamp.toInstant(), conditionString == null ? null : ReturnCondition.valueOf(conditionString), resultSet.getString("owner_username"), resultSet.getTimestamp("created_at").toInstant());
    }
}
