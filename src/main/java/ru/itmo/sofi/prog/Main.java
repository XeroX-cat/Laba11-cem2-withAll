package ru.itmo.sofi.prog;

import com.fasterxml.jackson.core.type.TypeReference;
import ru.itmo.sofi.base.CollectionStorage;
import ru.itmo.sofi.base.JsonCollectionStorage;
import ru.itmo.sofi.command.*;
import ru.itmo.sofi.essence.booking.*;
import ru.itmo.sofi.essence.checkout.Checkout;
import ru.itmo.sofi.essence.instrument.*;
import ru.itmo.sofi.exception.StorageException;
import ru.itmo.sofi.exception.UserInputException;
import ru.itmo.sofi.service.*;

import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
//        Instrument in1 = instrumentService.add("pH-meter, 2010", InstrumentType.PH_METER, "", "Grafckuy", InstrumentStatus.ACTIVE, "SYSTEM");
//        Booking book1 = bookingService.add(in1.getId(), Instant.parse("2026-01-08T10:00:00Z"), Instant.parse("2026-05-17T12:00:00Z"), BookingStatus.ACTIVE, "Kira");
//        Instrument in1 = instrumentService.add("pH-meter, 2010", InstrumentType.PH_METER, "", "Grafckuy", InstrumentStatus.ACTIVE, "SYSTEM");
//        Booking book1 = bookingService.add(in1.getId(), Instant.parse("2026-01-08T10:00:00Z"), Instant.parse("2026-05-17T12:00:00Z"), BookingStatus.ACTIVE, "Kira");
//        System.out.println("Все приборы:");
//        for (Instrument o : instrumentService.getAll()) {
//            System.out.println(o);
//        }
//        System.out.println("Все записи:");
//        for (Booking r : bookingService.getAll()) {
//            System.out.println(r);
//        }
//        Set<Instrument> instruments = Set.of();
//        Set<Booking> bookings = Set.of();
//        Set<Checkout> checkouts = Set.of();
//        Set<Instrument> instruments = new HashSet<>();
//        Set<Booking> bookings = new HashSet<>();
//        Set<Checkout> checkouts = new HashSet<>();
        CollectionStorage<Instrument> instrumentStorage = new JsonCollectionStorage<>(new TypeReference<Set<Instrument>>() {
        });

        CollectionStorage<Booking> bookingStorage = new JsonCollectionStorage<>(new TypeReference<Set<Booking>>() {
        });

        CollectionStorage<Checkout> checkoutStorage = new JsonCollectionStorage<>(new TypeReference<Set<Checkout>>() {
        });
//        Path instrumentPath = Path.of("data/instruments.json");
//        Path bookingPath = Path.of("data/bookiЧngs.json");
//        Path checkoutPath = Path.of("data/checkouts.json");
//        Set<Instrument> instruments = instrumentStorage.load(instrumentPath);
//        Set<Booking> bookings = bookingStorage.load(bookingPath);
//        Set<Checkout> checkouts = checkoutStorage.load(checkoutPath);
//        instrumentStorage.save(instruments, instrumentPath);
//        bookingStorage.save(bookings, bookingPath);
//        checkoutStorage.save(checkouts, checkoutPath);
//        InstrumentService instrumentService = new InstrumentService(instruments);
//        BookingService bookingService = new BookingService(bookings, instrumentService);
//        CheckoutService checkoutService = new CheckoutService(checkouts, instrumentService);
        Scanner scanner = new Scanner(System.in);
        InstrumentService instrumentService = new InstrumentService();
        BookingService bookingService = new BookingService(instrumentService);
        CheckoutService checkoutService = new CheckoutService(instrumentService, bookingService);
        HashMap<String, AbstractCommand> commandHashMap = new HashMap<>();
        commandHashMap.put(ExitCommand.getName(), new ExitCommand());
        commandHashMap.put(HelpCommand.getName(), new HelpCommand(commandHashMap));
        commandHashMap.put(BookCreate.getName(), new BookCreate(bookingService, scanner));
        commandHashMap.put(BookList.getName(), new BookList(bookingService));
        commandHashMap.put(BookCancel.getName(), new BookCancel(bookingService));
        commandHashMap.put(CheckoutTake.getName(), new CheckoutTake(checkoutService, scanner));
        commandHashMap.put(CheckoutReturn.getName(), new CheckoutReturn(checkoutService, scanner));
        commandHashMap.put(CheckoutList.getName(), new CheckoutList(checkoutService));
        commandHashMap.put(InstAvailable.getName(), new InstAvailable(checkoutService, scanner));
        commandHashMap.put(BookShow.getName(), new BookShow(bookingService));
        commandHashMap.put(CheckoutShow.getName(), new CheckoutShow(checkoutService));
        commandHashMap.put(BookReschedule.getName(), new BookReschedule(bookingService, scanner));
        commandHashMap.put(Save.getName(), new Save(instrumentService, bookingService, checkoutService, instrumentStorage, bookingStorage, checkoutStorage));
        commandHashMap.put(Load.getName(), new Load( instrumentService, bookingService, checkoutService, instrumentStorage, bookingStorage, checkoutStorage));
        instrumentService.add(
                "pH метр",
                InstrumentType.PH_METER,
                "123",
                "Лаборатория",
                InstrumentStatus.ACTIVE,
                "SYSTEM"
        );
        for (Instrument o : instrumentService.getAll()) {
            System.out.println(o);
        }
        Boolean isRunning = true;
        while (isRunning) {
            try {
                System.out.print("> ");
                String line = scanner.nextLine().trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\s+");
                String commandName = parts[0];

                AbstractCommand command = commandHashMap.get(commandName);

                if (command == null) {
                    System.out.println("Такой команды не существует. Попробуйте другую.");
                    continue;
                }

                if (command.isExit()) {
                    isRunning = false;
                    System.out.println("Программа завершена.");
                    continue;
                }

                command.execute(parts);
            } catch (UserInputException | StorageException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}