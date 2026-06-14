package ru.itmo.sofi.command;

import ru.itmo.sofi.base.CollectionStorage;
import ru.itmo.sofi.base.StorageValidator;
import ru.itmo.sofi.essence.booking.Booking;
import ru.itmo.sofi.essence.checkout.Checkout;
import ru.itmo.sofi.essence.instrument.Instrument;
import ru.itmo.sofi.exception.StorageLoadException;
import ru.itmo.sofi.exception.StorageSaveException;
import ru.itmo.sofi.exception.UserInputException;
import ru.itmo.sofi.service.BookingService;
import ru.itmo.sofi.service.CheckoutService;
import ru.itmo.sofi.service.InstrumentService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class Load extends AbstractCommand {
    private final static String NAME = "load";
    private final InstrumentService instrumentService;
    private final BookingService bookingService;
    private final CheckoutService checkoutService;
    private final CollectionStorage<Instrument> instrumentStorage;
    private final CollectionStorage<Booking> bookingStorage;
    private final CollectionStorage<Checkout> checkoutStorage;

    public Load(InstrumentService instrumentService, BookingService bookingService, CheckoutService checkoutService, CollectionStorage<Instrument> instrumentStorage, CollectionStorage<Booking> bookingStorage, CollectionStorage<Checkout> checkoutStorage) {
        this.instrumentService = instrumentService;
        this.bookingService = bookingService;
        this.checkoutService = checkoutService;
        this.instrumentStorage = instrumentStorage;
        this.bookingStorage = bookingStorage;
        this.checkoutStorage = checkoutStorage;
    }

    @Override
    public void execute(String[] args) throws UserInputException, StorageLoadException, StorageSaveException {
        try {
            if (args.length < 2) {
                System.out.println("Укажите путь. Пример: load data");
                return;
            }
            String basePathStr = args[1];
            Path basePath = Path.of(basePathStr);
            Path instrumentPath = basePath.resolve("instruments.json");
            Path bookingPath = basePath.resolve("bookings.json");
            Path checkoutPath = basePath.resolve("checkouts.json");
            if (!Files.exists(instrumentPath)) {
                throw new StorageLoadException("Файл не существует: " + instrumentPath);
            }
            if (!Files.exists(bookingPath)) {
                throw new StorageLoadException("Файл не существует: " + bookingPath);
            }
            if (!Files.exists(checkoutPath)) {
                throw new StorageLoadException("Файл не существует: " + checkoutPath);
            }
            Set<Instrument> instruments = instrumentStorage.load(instrumentPath);
            Set<Booking> bookings = bookingStorage.load(bookingPath);
            Set<Checkout> checkouts = checkoutStorage.load(checkoutPath);
            StorageValidator.validate(instruments, bookings, checkouts);
            instrumentService.replaceAll(instruments);
            bookingService.replaceAll(bookings);
            checkoutService.replaceAll(checkouts);
            System.out.println("Данные загружены");
        } catch (StorageLoadException e) {
            throw e;
        } catch (Exception e) {
            throw new StorageLoadException("Проверьте наличие и корректность файлов.");
        }
    }

    public static String getName() {
        return NAME;
    }

    @Override
    public String gethelp() {
        return "load - загрузить файл";
    }
}
