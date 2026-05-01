package ru.itmo.sofi.base;

import ru.itmo.sofi.essence.booking.Booking;
import ru.itmo.sofi.essence.checkout.Checkout;
import ru.itmo.sofi.essence.instrument.Instrument;
import ru.itmo.sofi.exception.StorageException;

import java.util.Set;

public class StorageValidator {
    public static void validate(Set<Instrument> instruments, Set<Booking> bookings, Set<Checkout> checkouts) throws StorageException {
        validateBookings(instruments, bookings);
        validateCheckouts(instruments, checkouts);
    }

    private static void validateBookings(Set<Instrument> instruments, Set<Booking> bookings) throws StorageException {
        for (Booking booking : bookings) {
            boolean instrumentExists = false;
            for (Instrument instrument : instruments) {
                if (instrument.getId() == booking.getInstrumentId()) {
                    instrumentExists = true;
                    break;
                }
            }
            if (!instrumentExists) {
                throw new StorageException(
                        "Ошибка загрузки: бронь id=" + booking.getId() + " ссылается на несуществующий instrumentId=" + booking.getInstrumentId());
            }
            if (booking.getStartAt() == null || booking.getEndAt() == null) {
                throw new StorageException("Ошибка загрузки: у брони id=" + booking.getId() + " не указаны даты");
            }
            if (!booking.getStartAt().isBefore(booking.getEndAt())) {
                throw new StorageException("Ошибка загрузки: у брони id=" + booking.getId() + " некорректный период");
            }
        }
    }

    private static void validateCheckouts(Set<Instrument> instruments, Set<Checkout> checkouts) throws StorageException {
        for (Checkout checkout : checkouts) {
            boolean instrumentExists = false;
            for (Instrument instrument : instruments) {
                if (instrument.getId() == checkout.getInstrumentId()) {
                    instrumentExists = true;
                    break;
                }
            }
            if (!instrumentExists) {
                throw new StorageException("Ошибка загрузки: выдача id=" + checkout.getId()
                        + " ссылается на несуществующий instrumentId=" + checkout.getInstrumentId());
            }
        }
    }
}