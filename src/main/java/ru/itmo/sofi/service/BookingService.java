package ru.itmo.sofi.service;

import ru.itmo.sofi.bstorage.BookingBas;
import ru.itmo.sofi.essence.booking.Booking;
import ru.itmo.sofi.essence.booking.BookingStatus;
import ru.itmo.sofi.essence.instrument.Instrument;
import ru.itmo.sofi.essence.instrument.InstrumentStatus;
import ru.itmo.sofi.exception.DatabaseException;
import ru.itmo.sofi.exception.UserInputException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.time.ZoneId;

public class BookingService {
//    private static final Set<Booking> bookingCollection = new HashSet<>();
//    private static final Map<Long, Booking> byId = new HashMap<>();
    private final InstrumentService instrumentService;
    private final BookingBas bookingBas = new BookingBas();

    public BookingService(InstrumentService instrumentService) {
        this.instrumentService = instrumentService;
    }

//    private long getBookingNextId() {
//        return System.currentTimeMillis() + bookingCollection.size();
//    }
    private long getBookingNextId() throws DatabaseException {
        return System.currentTimeMillis() + bookingBas.count();
    }

    public Booking add(long instrumentId, Instant startAt, Instant endAt, BookingStatus status, String ownerUsername) throws UserInputException{
        try {
            instrumentService.getById(instrumentId);
            long id = getBookingNextId();
            Instant now = Instant.now();
            Booking booking = new Booking(id, instrumentId, startAt, endAt, status, ownerUsername, now, now);
//            if (byId.containsKey(id)) {
//                throw new UserInputException(booking.toString() + " уже существует.");
//            }
            //        bookingCollection.add(booking);
            //        byId.put(id, booking);
            bookingBas.save(booking);
            return booking;
        } catch (DatabaseException e) {
            throw new UserInputException("Ошибка базы данных при создании брони.");
        }
    }

    public Booking getById(long id) throws UserInputException{
        try {
//        Booking booking = byId.get(id);
            Booking booking = bookingBas.findById(id);
            if (booking == null) {
                throw new UserInputException("Брони с id " + id + " не существует.");
            }
            return booking;
        } catch (DatabaseException e) {
            throw new UserInputException("Ошибка базы данных при поиске прибора.");
        }
    }

    public Set<Booking> getAll() {
//        return new HashSet<>(bookingCollection);
        try {
            return bookingBas.findAll();
        } catch (DatabaseException e) {
            return new HashSet<>();
        }
    }

    public Booking update(long id, long instrumentId, Instant startAt, Instant endAt, BookingStatus status, String ownerUsername) {
        Booking old = getById(id);
//        bookingCollection.remove(old);
        Instant now = Instant.now();
        Booking updated = new Booking(old.getId(), instrumentId, startAt, endAt, status, ownerUsername, old.getCreatedAt(), now);
//        bookingCollection.add(updated);
//        byId.put(id, updated);
        try {
            bookingBas.update(updated);
        } catch (DatabaseException e) {
            throw new UserInputException("Ошибка базы данных при обновлении брони.");
        }
        return updated;
    }

    public void remove(long id) throws UserInputException{
        try {
            Booking booking = bookingBas.findById(id);
            if (booking == null) {
                throw new UserInputException("Брони с id " + id + " не существует.");
            }
            Instant now = Instant.now();
            if (!booking.getStartAt().isAfter(now)) {
                throw new UserInputException("Нельзя удалить бронь, которая уже началась или завершилась.");
            }
            bookingBas.delete(id);
        } catch (DatabaseException e) {
            throw new UserInputException("Ошибка базы данных при удалении брони.");
        }
//        Booking booking = byId.remove(id);
//        if (booking == null) {
//            throw new UserInputException("Брони с id " + id + " не существует.");
//        }
//        Instant now = Instant.now();
//        if (!booking.getStartAt().isAfter(now)) {
//            throw new UserInputException("Нельзя удалить бронь, которая уже началась или завершилась.");
//        }
//        bookingCollection.remove(booking);
//        byId.remove(id);
    }

    public void bookCreate(long instrumentId, String startStr, String endStr, String ownerUsername) throws UserInputException {
        Instrument instrument;
        instrument = instrumentService.getById(instrumentId);
        if (instrument.getStatus() == InstrumentStatus.OUT_OF_SERVICE) {
            throw new UserInputException("Прибор OUT_OF_SERVICE");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Instant startAt;
        Instant endAt;
        try {
            LocalDateTime startLocal = LocalDateTime.parse(startStr, formatter);
            LocalDateTime endLocal = LocalDateTime.parse(endStr, formatter);
            ZoneId zone = ZoneId.systemDefault();
            startAt = startLocal.atZone(zone).toInstant();
            endAt = endLocal.atZone(zone).toInstant();
        } catch (DateTimeParseException e) {
            throw new UserInputException("Формат даты неверный (YYYY-MM-DD HH:MM)");
        }
        if (!endAt.isAfter(startAt)) {
            throw new UserInputException("Конец раньше начала");
        }
        for (Booking old : getAll()) {
            if (old.getStatus() != BookingStatus.ACTIVE) {
                continue;
            }
            if (old.getInstrumentId() == instrumentId) {
                boolean conflict = startAt.isBefore(old.getEndAt()) && endAt.isAfter(old.getStartAt());
                if (conflict) {
                    throw new UserInputException("На это время прибор уже забронирован.");
                }
            }
        }
        Booking b = add(instrumentId, startAt, endAt, BookingStatus.ACTIVE, ownerUsername);
        System.out.println("OK booking_id=" + b.getId());
    }

    public void bookList(long instrumentId, String fromDateStrOrNull){
        instrumentService.getById(instrumentId);
        Instant fromInstant = null;
        if (fromDateStrOrNull != null) {
            LocalDate fromDate = LocalDate.parse(fromDateStrOrNull);
            fromInstant = fromDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        }
        List<Booking> list = new ArrayList<>();
        for (Booking b : getAll()) {
            if (b.getInstrumentId() != instrumentId) continue;
            if (fromInstant != null && b.getStartAt().isBefore(fromInstant)) continue;
            list.add(b);
        }
        list.sort(Comparator.comparing(Booking::getStartAt));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneOffset.UTC);
        System.out.println("ID                   Start             End        Status");
        for (Booking b : list) {
            System.out.printf("%-13d %-16s %-16s %s%n", b.getId(), dtf.format(b.getStartAt()), dtf.format(b.getEndAt()), b.getStatus());
        }
    }

    public void bookCancel(long bookingId) throws UserInputException {
        Booking b;
        try {
            b = getById(bookingId);
        } catch (UserInputException e) {
            throw new UserInputException("Запись не найдена");
        }
        Instant now = Instant.now();
        if (!now.isBefore(b.getStartAt())) {
            throw new UserInputException("Нельзя отменить начавшуюся бронь");
        }
        if (b.getStatus() == BookingStatus.CANCELLED) {
            throw new UserInputException("Выдача уже отменена");
        }
//        bookingCollection.remove(b);
        Booking updated = new Booking(b.getId(), b.getInstrumentId(), b.getStartAt(), b.getEndAt(), BookingStatus.CANCELLED, b.getOwnerUsername(), b.getCreatedAt(), now);
//        bookingCollection.add(updated);
//        byId.put(bookingId, updated);
        try {
            bookingBas.update(updated);
        } catch (DatabaseException e) {
            throw new UserInputException("Ошибка базы данных при отмене брони.");
        }
        System.out.println("OK cancelled");
    }

    public void bookShow(long bookingId) throws UserInputException {
        Booking b;
        try {
            b = getById(bookingId);
        } catch (UserInputException e) {
            throw new UserInputException("Не найден");
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneOffset.UTC);
        System.out.println("Booking #" + b.getId());
        System.out.println("instrument id: " + b.getInstrumentId());
        System.out.println("start: " + dtf.format(b.getStartAt()));
        System.out.println("end:   " + dtf.format(b.getEndAt()));
        System.out.println("status: " + b.getStatus());
    }

    public void bookReschedule(long bookingId, String startStr, String endStr) throws UserInputException {
        Booking old;
        try {
            old = getById(bookingId);
        } catch (UserInputException e) {
            throw new UserInputException("Не найден");
        }
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Instant start;
        Instant end;
        try {
            start = LocalDateTime.parse(startStr, f).toInstant(ZoneOffset.UTC);
            end = LocalDateTime.parse(endStr, f).toInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException e) {
            throw new UserInputException("Формат даты неверный (YYYY-MM-DD HH:MM)");
        }
        if (!end.isAfter(start)) {
            throw new UserInputException("Конец раньше начала");
        }
        long instrumentId = old.getInstrumentId();
        for (Booking b : getAll()) {
            if (b.getId() == bookingId) continue;
            if (b.getInstrumentId() != instrumentId) continue;
            if (b.getStatus() == BookingStatus.CANCELLED) continue;
            boolean overlap = b.getStartAt().isBefore(end) && start.isBefore(b.getEndAt());
            if (overlap) {
                throw new UserInputException("Конфликт с другой бронью");
            }
        }
//        bookingCollection.remove(old);
        Instant now = Instant.now();
        Booking updated = new Booking(old.getId(), old.getInstrumentId(), start, end, old.getStatus(), old.getOwnerUsername(), old.getCreatedAt(), now);
//        bookingCollection.add(updated);
//        byId.put(updated.getId(), updated);
        try {
            bookingBas.update(updated);
        } catch (DatabaseException e) {
            throw new UserInputException("Ошибка базы данных при переносе брони.");
        }
        System.out.println("OK rescheduled");
    }

    public boolean hasBookingOverlap(long instrumentId, Instant start, Instant end) {
        for (Booking b : getAll()) {
            if (b.getInstrumentId() != instrumentId) continue;
            if (b.getStatus() != BookingStatus.ACTIVE) continue;
            if (start.isBefore(b.getEndAt()) && b.getStartAt().isBefore(end)) {
                return true;
            }
        }
        return false;
    }

    public void replaceAll(Set<Booking> newData) {
//        bookingCollection.clear();
//        byId.clear();
//        for (Booking b : newData) {
//            bookingCollection.add(b);
//            byId.put(b.getId(), b);
//        }
    }
}
