package ru.itmo.sofi.command;

import ru.itmo.sofi.exception.StorageLoadException;
import ru.itmo.sofi.exception.StorageSaveException;
import ru.itmo.sofi.exception.UserInputException;
import ru.itmo.sofi.service.BookingService;

import java.util.Scanner;

public class BookReschedule extends AbstractCommand {
    private final BookingService bookingService;
    private final Scanner scanner;

    public BookReschedule(BookingService bookingService, Scanner scanner) {
        this.bookingService = bookingService;
        this.scanner = scanner;
    }

    private final static String NAME = "book_reschedule";

    public static String getName() {
        return NAME;
    }

    @Override
    public void execute(String[] args) throws UserInputException, StorageLoadException, StorageSaveException {
        try {
            long bookingId;
            if (args.length >= 2) {
                bookingId = Long.parseLong(args[1]);
            } else {
                System.out.print("booking_id: ");
                bookingId = Long.parseLong(scanner.nextLine().trim());
            }
            String start;
            String end;
            if (args.length >= 4) {
                start = args[2] + (args.length >= 5 ? " " + args[3] : "");
                if (args.length >= 6) {
                    end = args[4] + " " + args[5];
                } else {
                    System.out.print("Конец (YYYY-MM-DD HH:MM): ");
                    end = scanner.nextLine().trim();
                }
            } else {
                System.out.print("Начало (YYYY-MM-DD HH:MM): ");
                start = scanner.nextLine().trim();
                System.out.print("Конец (YYYY-MM-DD HH:MM): ");
                end = scanner.nextLine().trim();
            }
            bookingService.bookReschedule(bookingId, start, end);
        } catch (NumberFormatException e) {
            throw new UserInputException("id должен быть числом.");
        }
    }

    @Override
    public String gethelp() {
        return "book_reschedule - перенести бронь";
    }
}
