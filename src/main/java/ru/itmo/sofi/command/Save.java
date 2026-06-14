package ru.itmo.sofi.command;

import ru.itmo.sofi.base.CollectionStorage;
import ru.itmo.sofi.essence.booking.Booking;
import ru.itmo.sofi.essence.checkout.Checkout;
import ru.itmo.sofi.essence.instrument.Instrument;
import ru.itmo.sofi.exception.StorageLoadException;
import ru.itmo.sofi.exception.StorageSaveException;
import ru.itmo.sofi.exception.UserInputException;
import ru.itmo.sofi.service.BookingService;
import ru.itmo.sofi.service.CheckoutService;
import ru.itmo.sofi.service.InstrumentService;

import java.nio.file.Path;
import java.util.Set;

public class Save extends AbstractCommand {
    private final static String NAME = "save";
    private final InstrumentService instrumentService;
    private final BookingService bookingService;
    private final CheckoutService checkoutService;
    private final CollectionStorage<Instrument> instrumentStorage;
    private final CollectionStorage<Booking> bookingStorage;
    private final CollectionStorage<Checkout> checkoutStorage;

    public Save(InstrumentService instrumentService, BookingService bookingService, CheckoutService checkoutService, CollectionStorage<Instrument> instrumentStorage, CollectionStorage<Booking> bookingStorage, CollectionStorage<Checkout> checkoutStorage) {
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
                System.out.println("Укажите путь. Пример: save data");
                return;
            }
            String basePathStr = args[1];
            if (basePathStr.isEmpty()) {
                System.out.println("Укажите путь. Пример: save data");
                return;
            }
            Path basePath = Path.of(basePathStr);
            Path instrumentPath = basePath.resolve("instruments.json");
            Path bookingPath = basePath.resolve("bookings.json");
            Path checkoutPath = basePath.resolve("checkouts.json");
            Set<Instrument> instruments = instrumentService.getAll();
            Set<Booking> bookings = bookingService.getAll();
            Set<Checkout> checkouts = checkoutService.getAll();
            instrumentStorage.save(instruments, instrumentPath);
            bookingStorage.save(bookings, bookingPath);
            checkoutStorage.save(checkouts, checkoutPath);
            System.out.println("Данные сохранены в " + basePath.toAbsolutePath());
        } catch (Exception e) {
            throw new StorageSaveException("Проверьте корректность пути и доступность файлов.");
        }
    }

    public static String getName() {
        return NAME;
    }

    @Override
    public String gethelp() {
        return "save - сохранить файл";
    }
}
