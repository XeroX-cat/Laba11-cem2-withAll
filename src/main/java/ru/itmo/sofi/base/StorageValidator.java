package ru.itmo.sofi.base;

import ru.itmo.sofi.essence.booking.Booking;
import ru.itmo.sofi.essence.checkout.Checkout;
import ru.itmo.sofi.essence.instrument.Instrument;
import ru.itmo.sofi.exception.StorageLoadException;

import java.util.Set;

public class StorageValidator {
    public static void validate(Set<Instrument> instruments, Set<Booking> bookings, Set<Checkout> checkouts) throws StorageLoadException
    {
        validateBookings(instruments, bookings);
        validateCheckouts(instruments, checkouts);
    }

    private static void validateBookings(Set<Instrument> instruments, Set<Booking> bookings) throws StorageLoadException {
        for (Booking booking : bookings) {
            boolean instrumentExists = false;
            for (Instrument instrument : instruments) {
                if (instrument.getId() == booking.getInstrumentId()) {
                    instrumentExists = true;
                    break;
                }
            }
            if (!instrumentExists) {
                throw new StorageLoadException("Бронь id=" + booking.getId() + " ссылается на несуществующий instrumentId=" + booking.getInstrumentId());
            }
            if (booking.getStartAt() == null && booking.getEndAt() == null) {
                throw new StorageLoadException("У брони id=" + booking.getId() + " не указаны даты");
            }
            if (!booking.getStartAt().isBefore(booking.getEndAt())) {
                throw new StorageLoadException("У брони id=" + booking.getId() + " некорректный период");
            }
        }
    }

    private static void validateCheckouts(Set<Instrument> instruments, Set<Checkout> checkouts) throws StorageLoadException {
        for (Checkout checkout : checkouts) {
            boolean instrumentExists = false;
            for (Instrument instrument : instruments) {
                if (instrument.getId() == checkout.getInstrumentId()) {
                    instrumentExists = true;
                    break;
                }
            }
            if (!instrumentExists) {
                throw new StorageLoadException("Выдача id=" + checkout.getId()
                        + " ссылается на несуществующий instrumentId=" + checkout.getInstrumentId());
            }
        }
    }
}