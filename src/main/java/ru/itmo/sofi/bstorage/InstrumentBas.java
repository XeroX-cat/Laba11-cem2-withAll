package ru.itmo.sofi.bstorage;

import ru.itmo.sofi.datab.DatabaseBridge;
import ru.itmo.sofi.essence.instrument.Instrument;
import ru.itmo.sofi.essence.instrument.InstrumentStatus;
import ru.itmo.sofi.essence.instrument.InstrumentType;
import ru.itmo.sofi.exception.DatabaseException;

import java.sql.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class InstrumentBas {

    public void save(Instrument instrument) throws DatabaseException {
        String sql = """
                INSERT INTO instruments
                (id, name, type, inventory_number, location, status, owner_username, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseBridge.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, instrument.getId());
            statement.setString(2, instrument.getName());
            statement.setString(3, instrument.getType().name());
            statement.setString(4, instrument.getInventoryNumber());
            statement.setString(5, instrument.getLocation());
            statement.setString(6, instrument.getStatus().name());
            statement.setString(7, instrument.getOwnerUsername());
            statement.setTimestamp(8, Timestamp.from(instrument.getCreatedAt()));
            statement.setTimestamp(9, Timestamp.from(instrument.getUpdatedAt()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Не удалось сохранить прибор.");
        }
    }

    public Instrument findById(long id) throws DatabaseException {
        String sql = """
                SELECT id, name, type, inventory_number, location, status, owner_username, created_at, updated_at
                FROM instruments
                WHERE id = ?
                """;
        try (Connection connection = DatabaseBridge.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return toInstrument(resultSet);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Не удалось найти прибор.");
        }
    }

    public Set<Instrument> findAll() throws DatabaseException {
        String sql = """
                SELECT id, name, type, inventory_number, location, status, owner_username, created_at, updated_at
                FROM instruments
                """;
        try (Connection connection = DatabaseBridge.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            Set<Instrument> instruments = new HashSet<>();
            while (resultSet.next()) {
                instruments.add(toInstrument(resultSet));
            }
            return instruments;
        } catch (SQLException e) {
            throw new DatabaseException("Не удалось загрузить приборы.");
        }
    }

    public void update(Instrument instrument) throws DatabaseException {
        String sql = """
                UPDATE instruments
                SET name = ?,
                    inventory_number = ?,
                    location = ?,
                    status = ?,
                    owner_username = ?,
                    updated_at = ?
                WHERE id = ?
                """;
        try (Connection connection = DatabaseBridge.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, instrument.getName());
            statement.setString(2, instrument.getInventoryNumber());
            statement.setString(3, instrument.getLocation());
            statement.setString(4, instrument.getStatus().name());
            statement.setString(5, instrument.getOwnerUsername());
            statement.setTimestamp(6, Timestamp.from(instrument.getUpdatedAt()));
            statement.setLong(7, instrument.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Не удалось обновить прибор.");
        }
    }

    public void delete(long id) throws DatabaseException {
        String sql = """
                DELETE FROM instruments
                WHERE id = ?
                """;
        try (Connection connection = DatabaseBridge.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Не удалось удалить прибор.");
        }
    }

    private Instrument toInstrument(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        InstrumentType type = InstrumentType.valueOf(resultSet.getString("type"));
        String inventoryNumber = resultSet.getString("inventory_number");
        String location = resultSet.getString("location");
        InstrumentStatus status = InstrumentStatus.valueOf(resultSet.getString("status"));
        String ownerUsername = resultSet.getString("owner_username");
        Instant createdAt = resultSet.getTimestamp("created_at").toInstant();
        Instant updatedAt = resultSet.getTimestamp("updated_at").toInstant();
        return new Instrument(id, name, type, inventoryNumber, location, status, ownerUsername, createdAt, updatedAt);
    }

    public int count() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM instruments";
        try (Connection connection = DatabaseBridge.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Не удалось посчитать приборы.");
        }
    }
}
