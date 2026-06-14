package ru.itmo.sofi.service;

import ru.itmo.sofi.bstorage.BookingBas;
import ru.itmo.sofi.bstorage.CheckoutBas;
import ru.itmo.sofi.essence.booking.Booking;
import ru.itmo.sofi.essence.checkout.Checkout;
import ru.itmo.sofi.essence.checkout.ReturnCondition;
import ru.itmo.sofi.essence.instrument.Instrument;
import ru.itmo.sofi.essence.instrument.InstrumentStatus;
import ru.itmo.sofi.essence.instrument.InstrumentType;
import ru.itmo.sofi.exception.DatabaseException;
import ru.itmo.sofi.exception.UserInputException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class CheckoutService {
    //    private static final Set<Checkout> checkoutCollection = new HashSet<>();
//    private static final Map<Long, Checkout> byId = new HashMap<>();
    private final InstrumentService instrumentService;
    private final BookingService bookingService;
    private final CheckoutBas checkoutBas = new CheckoutBas();

    public CheckoutService(InstrumentService instrumentService, BookingService bookingService) {
        this.instrumentService = instrumentService;
        this.bookingService = bookingService;
    }

    //    private long getCheckoutNextId() {
//        return System.currentTimeMillis() + checkoutCollection.size();
//    }
    private long getCheckoutNextId() throws DatabaseException {
        return System.currentTimeMillis() + checkoutBas.count();
    }

    public Checkout add(long instrumentId, String username, String comment, Instant takenAt, Instant returnedAt, ReturnCondition returnCondition, String ownerUsername, Instant createdAt) throws UserInputException {
        try {
            instrumentService.getById(instrumentId);
            long id = getCheckoutNextId();
            Instant now = Instant.now();
            Checkout checkout = new Checkout(id, instrumentId, username, comment, now, returnedAt, returnCondition, ownerUsername, now);
//            if (byId.containsKey(id)) {
//                throw new UserInputException(checkout.toString() + "уже существует.");
//            }
//            checkoutCollection.add(checkout);
//            byId.put(id, checkout);
            checkoutBas.save(checkout);
            return checkout;
        } catch (DatabaseException e) {
            throw new UserInputException("Ошибка базы данных при создании выдачи.");
        }
    }

    public Checkout getById(long id) throws UserInputException {
        try {
//            Checkout checkout = byId.get(id);
            Checkout checkout = checkoutBas.findById(id);
            if (checkout == null) {
                throw new UserInputException("Выдачи с id " + id + " не существует.");
            }
            return checkout;
        } catch (DatabaseException e) {
            throw new UserInputException("Ошибка базы данных при поиске выдачи.");
        }
    }

    public Set<Checkout> getAll() {
//        return new HashSet<>(checkoutCollection);
        try {
            return checkoutBas.findAll();
        } catch (DatabaseException e) {
            return new HashSet<>();
        }
    }

    public Checkout update(long id, long instrumentId, String username, String comment, Instant takenAt, ReturnCondition returnCondition, String ownerUsername, Instant createdAt) {
        Checkout old = getById(id);
//        checkoutCollection.remove(old);
        Instant now = Instant.now();
        Checkout updated = new Checkout(old.getId(), instrumentId, username, comment, takenAt, now, returnCondition, ownerUsername, createdAt);
//        checkoutCollection.add(updated);
//        byId.put(id, updated);
        try {
            checkoutBas.update(updated);
        } catch (DatabaseException e) {
            throw new UserInputException("Ошибка базы данных при обновлении выдачи.");
        }
        return updated;
    }

    public void remove(long id) throws UserInputException {
        try {
            Checkout checkout = checkoutBas.findById(id);
            if (checkout == null) {
                throw new UserInputException("Выдача с id " + id + " не существует.");
            }
            Instant now = Instant.now();
            if (checkout.getReturnedAt() == null) {
                throw new UserInputException("Нельзя удалить выдачу, пока прибор не возвращён.");
            }
            checkoutBas.delete(id);
        } catch (DatabaseException e) {
            throw new UserInputException("Ошибка базы данных при удалении выдачи.");
        }
//        Checkout checkout = byId.get(id);
//        if (checkout == null) {
//            throw new UserInputException("Выдача с id " + id + " не существует.");
//        }
//        if (checkout.getReturnedAt() == null) {
//            throw new UserInputException("Нельзя удалить выдачу, пока прибор не возвращён.");
//        }
//        checkoutCollection.remove(checkout);
//        byId.remove(id);
    }

    public boolean existsActiveCheckout(long instrumentId) {
        for (Checkout c : getAll()) {
            if (c.getInstrumentId() == instrumentId && c.getReturnedAt() == null) {
                return true;
            }
        }
        return false;
    }

    public void checkoutTake(long instrumentId, String username, String comment, String ownerUsername) throws UserInputException {
        Instrument instrument;
        instrument = instrumentService.getById(instrumentId);
        if (instrument.getStatus() == InstrumentStatus.OUT_OF_SERVICE) {
            throw new UserInputException("Прибор OUT_OF_SERVICE");
        }
        if (existsActiveCheckout(instrumentId)) {
            throw new UserInputException("Прибор уже выдан");
        }
        String user = username.trim();
        String com = comment.trim();
        try {
            Long id = getCheckoutNextId();
            Instant now = Instant.now();
            Checkout checkout = new Checkout(id, instrumentId, user, com, now, null, null, ownerUsername, now);
            checkoutBas.save(checkout);
            System.out.println("OK checkout_id=" + id);
        } catch (DatabaseException e) {
            throw new UserInputException("Ошибка базы данных при создании выдачи.");
        }
    }

    public void checkoutReturn(long checkoutId, String condStr) throws UserInputException {
        Checkout checkout = getById(checkoutId);
        if (checkout == null) {
            System.out.println("Checkout не найден");
            return;
        }
        if (checkout.getReturnCondition() != null) {
            throw new UserInputException("Эта выдача уже возвращена.");
        }
        condStr = condStr.trim().toUpperCase();
        ReturnCondition condition;
        try {
            condition = ReturnCondition.valueOf(condStr);
        } catch (IllegalArgumentException e) {
            throw new UserInputException("неверный статус");
        }
        update(checkout.getId(), checkout.getInstrumentId(), checkout.getUsername(), checkout.getComment(), checkout.getTakenAt(), condition, checkout.getOwnerUsername(), checkout.getCreatedAt());
        System.out.println("OK returned");
    }

    public void checkoutList(boolean openOnly) {
        List<Checkout> list = new ArrayList<>();
        for (Checkout c : getAll()) {
            if (openOnly && c.getReturnCondition() != null) {
                continue;
            }
            list.add(c);
        }
        list.sort(Comparator.comparing(Checkout::getCreatedAt));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneOffset.UTC);
        System.out.println("ID  Instrument  User      TakenAt");
        for (Checkout c : list) {
            System.out.printf("%-3d %-11d %-9s %s%n", c.getId(), c.getInstrumentId(), c.getOwnerUsername(), dtf.format(c.getCreatedAt()));
        }
    }

    public void instAvailable(String typeStr, String startStr, String endStr) throws UserInputException {
        InstrumentType type;
        try {
            type = InstrumentType.valueOf(typeStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UserInputException("неверный тип прибора");
        }
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Instant start;
        Instant end;
        try {
            LocalDateTime s = LocalDateTime.parse(startStr, f);
            LocalDateTime e = LocalDateTime.parse(endStr, f);
            start = s.toInstant(ZoneOffset.UTC);
            end = e.toInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException e) {
            throw new UserInputException("Формат даты неверный (--from YYYY-MM-DD)");
        }
        if (!end.isAfter(start)) {
            throw new UserInputException("Конец раньше начала");
        }
        List<Long> ids = new ArrayList<>();
        for (Instrument inst : instrumentService.getAll()) {
            if (inst.getType() != type) continue;
            if (inst.getStatus() == InstrumentStatus.OUT_OF_SERVICE) continue;
            if (isInstrumentTakenNow(inst.getId())) continue;
            if (bookingService.hasBookingOverlap(inst.getId(), start, end)) continue;
            ids.add(inst.getId());
        }
        String out = ids.stream().map(String::valueOf).collect(Collectors.joining(", "));
        System.out.println("Доступные инструменты: " + out);
    }

    public void checkoutShow(long checkoutId) throws UserInputException {
        Checkout c = getById(checkoutId);
        if (c == null) {
            throw new UserInputException("Не найден");
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneOffset.UTC);
        System.out.println("Checkout #" + c.getId());
        System.out.println("instrument_id: " + c.getInstrumentId());
        System.out.println("user: " + c.getUsername());
        System.out.println("takenAt: " + dtf.format(c.getCreatedAt()));
        if (c.getReturnCondition() == null) {
            System.out.println("returnedAt: -");
        } else {
            System.out.println("returnedAt: (returned)");
        }
    }

    private boolean isInstrumentTakenNow(Long instrumentId) {
        for (Checkout c : getAll()) {
            if (c.getInstrumentId() == instrumentId && c.getReturnCondition() == null) {
                return true;
            }
        }
        return false;
    }

    public void replaceAll(Set<Checkout> newData) {
//        checkoutCollection.clear();
//        byId.clear();
//
//        for (Checkout c : newData) {
//            checkoutCollection.add(c);
//            byId.put(c.getId(), c);
//        }
    }
}
