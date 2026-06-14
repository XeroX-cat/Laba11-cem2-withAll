package ru.itmo.sofi.command;

import ru.itmo.sofi.exception.StorageLoadException;
import ru.itmo.sofi.exception.StorageSaveException;
import ru.itmo.sofi.exception.UserInputException;
import ru.itmo.sofi.service.BookingService;

public class BookShow extends AbstractCommand {
    private final BookingService bookingService;

    public BookShow(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    private final static String NAME = "book_show";

    public static String getName() {
        return NAME;
    }

    @Override
    public void execute(String[] args) throws UserInputException, StorageLoadException, StorageSaveException {
        try {
            if (args.length != 2) {
                throw new UserInputException("Формат book_show <booking_id>.");
            }

            long bookingId = Long.parseLong(args[1]);
            bookingService.bookShow(bookingId);
        } catch (NumberFormatException e) {
            throw new UserInputException("id должен быть числом.");
        }
    }

    @Override
    public String gethelp() {
        return "book_show - посмотреть бронь";
    }
}
