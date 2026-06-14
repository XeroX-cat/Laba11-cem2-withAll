package ru.itmo.sofi.bstorage;

import ru.itmo.sofi.datab.DatabaseBridge;
import ru.itmo.sofi.essence.booking.Booking;
import ru.itmo.sofi.essence.booking.BookingStatus;
import ru.itmo.sofi.exception.DatabaseException;

import java.sql.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class BookingBas {

    public void save(Booking booking) throws DatabaseException {
        String sql = """
                INSERT INTO booking
                (id, instrument_id, start_at, end_at, status, owner_username, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseBridge.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, booking.getId());
            statement.setLong(2, booking.getInstrumentId());
            statement.setTimestamp(3, Timestamp.from(booking.getStartAt()));
            statement.setTimestamp(4, Timestamp.from(booking.getEndAt()));
            statement.setString(5, booking.getStatus().name());
            statement.setString(6, booking.getOwnerUsername());
            statement.setTimestamp(7, Timestamp.from(booking.getCreatedAt()));
            statement.setTimestamp(8, Timestamp.from(booking.getUpdatedAt()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Не удалось сохранить бронь.");
        }
    }

    public Booking findById(long id) throws DatabaseException {
        String sql = """
                SELECT *
                FROM booking
                WHERE id = ?
                """;
        try (Connection connection = DatabaseBridge.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return toBooking(resultSet);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Не удалось найти бронь.");
        }
    }
    public Set<Booking> findAll() throws DatabaseException {
        String sql = "SELECT * FROM booking";
        try (Connection connection = DatabaseBridge.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            Set<Booking> bookings = new HashSet<>();
            while (resultSet.next()) {
                bookings.add(toBooking(resultSet));
            }
            return bookings;
        } catch (SQLException e) {
            throw new DatabaseException("Не удалось загрузить брони.");
        }
    }

    public void update(Booking booking) throws DatabaseException {
        String sql = """
                UPDATE booking
                SET instrument_id = ?,
                    start_at = ?,
                    end_at = ?,
                    status = ?,
                    owner_username = ?,
                    updated_at = ?
                WHERE id = ?
                """;
        try (Connection connection = DatabaseBridge.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, booking.getInstrumentId());
            statement.setTimestamp(2, Timestamp.from(booking.getStartAt()));
            statement.setTimestamp(3, Timestamp.from(booking.getEndAt()));
            statement.setString(4, booking.getStatus().name());
            statement.setString(5, booking.getOwnerUsername());
            statement.setTimestamp(6, Timestamp.from(booking.getUpdatedAt()));
            statement.setLong(7, booking.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Не удалось обновить бронь.");
        }
    }

    public void delete(long id) throws DatabaseException {
        String sql = "DELETE FROM booking WHERE id = ?";
        try (Connection connection = DatabaseBridge.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Не удалось удалить бронь.");
        }
    }

    public int count() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM booking";
        try (Connection connection = DatabaseBridge.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Не удалось посчитать брони.");
        }
    }

    private Booking toBooking(ResultSet resultSet) throws SQLException {
        return new Booking(resultSet.getLong("id"), resultSet.getLong("instrument_id"), resultSet.getTimestamp("start_at").toInstant(), resultSet.getTimestamp("end_at").toInstant(), BookingStatus.valueOf(resultSet.getString("status")), resultSet.getString("owner_username"), resultSet.getTimestamp("created_at").toInstant(), resultSet.getTimestamp("updated_at").toInstant());
    }
}
