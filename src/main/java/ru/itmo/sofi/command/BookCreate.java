package ru.itmo.sofi.command;

import ru.itmo.sofi.exception.UserInputException;
import ru.itmo.sofi.service.BookingService;

import java.util.Scanner;

public class BookCreate extends AbstractCommand {
    private final BookingService bookingService;
    private final Scanner scanner;

    public BookCreate(BookingService bookingService, Scanner scanner) {
        this.bookingService = bookingService;
        this.scanner = scanner;
    }

    private final static String NAME = "book_create";

    public static String getName() {
        return NAME;
    }

    @Override
    public void execute(String[] args) throws UserInputException {
        try {
            long instrumentId;
            if (args.length >= 2) {
                instrumentId = Long.parseLong(args[1]);
            } else {
                System.out.print("instrument_id: ");
                instrumentId = Long.parseLong(scanner.nextLine().trim());
            }
            System.out.print("Начало (YYYY-MM-DD HH:MM): ");
            String start = scanner.nextLine().trim();

            System.out.print("Конец (YYYY-MM-DD HH:MM): ");
            String end = scanner.nextLine().trim();
            bookingService.bookCreate(instrumentId, start, end);
        } catch (NumberFormatException e) {
            throw new UserInputException("id должен быть числом.");
        }
    }

    @Override
    public String gethelp() {
        return "book_create - создать запись";
    }
}
