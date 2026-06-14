package ru.itmo.sofi.service;

import ru.itmo.sofi.bstorage.InstrumentBas;
import ru.itmo.sofi.essence.instrument.Instrument;
import ru.itmo.sofi.essence.instrument.InstrumentStatus;
import ru.itmo.sofi.essence.instrument.InstrumentType;
import ru.itmo.sofi.exception.DatabaseException;
import ru.itmo.sofi.exception.UserInputException;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InstrumentService {
    private final InstrumentBas instrumentBas = new InstrumentBas();
//    private static final Set<Instrument> instrumentCollection = new HashSet<>();
//    private static final Map<Long, Instrument> byId = new HashMap<>();

//    private long getInstrumentNextId() {
//        return System.currentTimeMillis() + instrumentCollection.size();
//    }
    private long getInstrumentNextId() throws DatabaseException {
        return System.currentTimeMillis() + instrumentBas.count();
    }

    public Instrument add(String name, InstrumentType type, String inventoryNumber, String location, InstrumentStatus status, String ownerUsername) throws UserInputException {
        try {
            long id = getInstrumentNextId();
            Instant now = Instant.now();
            Instrument instrument = new Instrument(id, name, type, inventoryNumber, location, status, ownerUsername, now, now);
//            if (byId.containsKey(id)) {
//                throw new UserInputException(instrument.toString() + " уже существует.");
//            }
//            instrumentCollection.add(instrument);
//            byId.put(id, instrument);
            instrumentBas.save(instrument);
            return instrument;
        } catch (DatabaseException e) {
            throw new UserInputException("Ошибка базы данных при создании прибора.");
        }
    }

    public Instrument getById(long id) throws UserInputException {
        try {
//          Instrument instrument = byId.get(id);
            Instrument instrument = instrumentBas.findById(id);
            if (instrument == null) {
                throw new UserInputException("Инструмента с id " + id + " не существует.");
            }
            return instrument;
        } catch (DatabaseException e) {
            throw new UserInputException("Ошибка базы данных при поиске прибора.");
        }
    }

    public Set<Instrument> getAll() {
//        return new HashSet<>(instrumentCollection);
        try {
            return instrumentBas.findAll();
        } catch (DatabaseException e) {
            return new HashSet<>();
        }
    }

    public Instrument update(long id, String name, String inventoryNumber, String location, InstrumentStatus status, String ownerUsername) {
        Instrument old = getById(id);
//        instrumentCollection.remove(old);
        Instant now = Instant.now();
        Instrument updated = new Instrument(old.getId(), name, old.getType(), inventoryNumber, location, status, ownerUsername, old.getCreatedAt(), now);
//        instrumentCollection.add(updated);
//        byId.put(id, updated);
        try {
            instrumentBas.update(updated);
        } catch (DatabaseException e) {
            throw new UserInputException("Ошибка базы данных при обновлении прибора.");
        }
        return updated;
    }

    public void remove(long id) throws UserInputException {
//        Instrument instrument = byId.remove(id);
//        if (instrument == null) {
//            throw new UserInputException("Инструмента с id " + id + " не существует.");
//        }
//        instrumentCollection.remove(instrument);
//        byId.remove(id);
        try {
            Instrument instrument = instrumentBas.findById(id);
            if (instrument == null) {
                throw new UserInputException("Инструмента с id " + id + " не существует.");
            }
            instrumentBas.delete(id);
        } catch (DatabaseException e) {
            throw new UserInputException("Ошибка базы данных при удалении прибора.");
        }
    }

    public void replaceAll(Set<Instrument> newData) {
//        instrumentCollection.clear();
//        byId.clear();
//        for (Instrument i : newData) {
//            instrumentCollection.add(i);
//            byId.put(i.getId(), i);
//        }
    }
}
