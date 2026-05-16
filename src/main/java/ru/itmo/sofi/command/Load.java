package ru.itmo.sofi.command;

import ru.itmo.sofi.base.CollectionStorage;
import ru.itmo.sofi.base.StorageValidator;
import ru.itmo.sofi.essence.booking.Booking;
import ru.itmo.sofi.essence.checkout.Checkout;
import ru.itmo.sofi.essence.instrument.Instrument;
import ru.itmo.sofi.exception.StorageException;
import ru.itmo.sofi.service.BookingService;
import ru.itmo.sofi.service.CheckoutService;
import ru.itmo.sofi.service.InstrumentService;

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
    public void execute(String[] args) throws StorageException {
        try {
            Thread.sleep(5000);
            if (args.length < 2) {
                System.out.println("Укажите путь. Пример: load data");
                return;
            }
            String basePathStr = args[1];
            Path basePath = Path.of(basePathStr);
            Path instrumentPath = basePath.resolve("instruments.json");
            Path bookingPath = basePath.resolve("bookings.json");
            Path checkoutPath = basePath.resolve("checkouts.json");
            Set<Instrument> instruments = instrumentStorage.load(instrumentPath);
            Set<Booking> bookings = bookingStorage.load(bookingPath);
            Set<Checkout> checkouts = checkoutStorage.load(checkoutPath);
            StorageValidator.validate(instruments, bookings, checkouts);
            instrumentService.replaceAll(instruments);
            bookingService.replaceAll(bookings);
            checkoutService.replaceAll(checkouts);
            System.out.println("Данные загружены");
        } catch (InterruptedException e) {
            throw new StorageException("Операция прервана");
        } catch (Exception e) {
            throw new StorageException ("Ошибка загрузки: " + e.getMessage());
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
