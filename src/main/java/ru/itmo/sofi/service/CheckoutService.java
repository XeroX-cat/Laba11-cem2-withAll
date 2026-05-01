package ru.itmo.sofi.service;

import ru.itmo.sofi.essence.checkout.Checkout;
import ru.itmo.sofi.essence.checkout.ReturnCondition;
import ru.itmo.sofi.essence.instrument.Instrument;
import ru.itmo.sofi.essence.instrument.InstrumentStatus;
import ru.itmo.sofi.essence.instrument.InstrumentType;
import ru.itmo.sofi.exception.UserInputException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class CheckoutService {
    private final Set<Checkout> checkoutCollection = new HashSet<>();
    private final Map<Long, Checkout> byId = new HashMap<>();
    private final InstrumentService instrumentService;
    private final BookingService bookingService;

    public CheckoutService(InstrumentService instrumentService, BookingService bookingService) {
        this.instrumentService = instrumentService;
        this.bookingService = bookingService;
    }

    private long getCheckoutNextId() {
        return System.currentTimeMillis() + checkoutCollection.size();
    }

    public Checkout add(long instrumentId, String username, String comment, Instant takenAt, Instant returnedAt, ReturnCondition returnCondition, String ownerUsername, Instant createdAt) throws UserInputException {
        instrumentService.getById(instrumentId);
        long id = getCheckoutNextId();
        Instant now = Instant.now();
        Checkout checkout = new Checkout(id, instrumentId, username, comment, now, returnedAt, returnCondition, ownerUsername, now);
        if (byId.containsKey(id)) {
            throw new UserInputException(checkout.toString() + "уже существует.");
        }
        checkoutCollection.add(checkout);
        byId.put(id, checkout);
        return checkout;
    }

    public Checkout getById(long id) throws UserInputException {
        Checkout checkout = byId.get(id);
        if (checkout == null) {
            throw new UserInputException("Инструмента с id " + id + " не существует.");
        }
        return checkout;
    }

    public Set<Checkout> getAll() {
        return new HashSet<>(checkoutCollection); // копия
    }

    public Checkout update(long id, long instrumentId, String username, String comment, Instant takenAt, Instant returnedAt, ReturnCondition returnCondition, String ownerUsername) {
        Checkout old = getById(id);
        checkoutCollection.remove(old);
        Instant now = Instant.now();
        Checkout updated = new Checkout(old.getId(), instrumentId, username, comment, takenAt, returnedAt, returnCondition, ownerUsername, now);
        checkoutCollection.add(updated);
        byId.put(id, updated);
        return updated;
    }

    public void remove(long id) throws UserInputException{
        Checkout checkout = byId.remove(id);
        if (checkout == null) {
            throw new UserInputException("Инструмента с id " + id + " не существует.");
        }
        checkoutCollection.remove(checkout);
    }

    public boolean existsActiveCheckout(long instrumentId) {
        for (Checkout c : checkoutCollection) {
            if (c.getInstrumentId() == instrumentId && c.getReturnedAt() == null) {
                return true;
            }
        }
        return false;
    }

    public void checkoutTake(long instrumentId, String username, String comment) throws UserInputException {
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
        Long id = getCheckoutNextId();
        Instant now = Instant.now();
        Checkout checkout = new Checkout(id, instrumentId, user, com, now, null, null, username, now);
        checkoutCollection.add(checkout);
        byId.put(id, checkout);
        System.out.println("OK checkout_id=" + id);
    }

    public void checkoutReturn(long checkoutId, String condStr) throws UserInputException {
        Checkout checkout = byId.get(checkoutId);
        if (checkout == null) {
            System.out.println("Checkout не найден");
            return;
        }
        if (checkout.getReturnCondition() != null) {
            System.out.println("Уже возвращён");
            return;
        }
        condStr = condStr.trim().toUpperCase();
        ReturnCondition condition;
        try {
            condition = ReturnCondition.valueOf(condStr);
        } catch (IllegalArgumentException e) {
            throw new UserInputException ("неверный статус");
        }
        checkoutCollection.remove(checkout);
        Checkout updated = new Checkout(checkout.getId(), checkout.getInstrumentId(), checkout.getUsername(), checkout.getComment(), checkout.getTakenAt(), checkout.getReturnedAt(), condition, checkout.getOwnerUsername(), checkout.getCreatedAt());
        checkoutCollection.add(updated);
        byId.put(updated.getId(), updated);
        System.out.println("OK returned");
    }

    public void checkoutList(boolean openOnly) {
        List<Checkout> list = new ArrayList<>();
        for (Checkout c : checkoutCollection) {
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
        Checkout c = byId.get(checkoutId);
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
        checkoutCollection.clear();
        byId.clear();

        for (Checkout c : newData) {
            checkoutCollection.add(c);
            byId.put(c.getId(), c);
        }
    }
}
