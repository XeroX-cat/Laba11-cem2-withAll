package ru.itmo.sofi.command;

import ru.itmo.sofi.exception.StorageLoadException;
import ru.itmo.sofi.exception.StorageSaveException;
import ru.itmo.sofi.exception.UserInputException;
import ru.itmo.sofi.service.BookingService;

public class BookCancel extends AbstractCommand {
    private final BookingService bookingService;
    private final static String NAME = "book_cancel";

    public BookCancel(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public static String getName() {
        return NAME;
    }

    @Override
    public void execute(String[] args) throws UserInputException, StorageLoadException, StorageSaveException {
        if (args.length != 2) {
            throw new UserInputException("формат book_cancel <booking_id>.");
        }
        long bookingId;
        try {
            bookingId = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            throw new UserInputException("id должен быть числом.");
        }
        bookingService.bookCancel(bookingId);
    }

    @Override
    public String gethelp() {
        return "book_cancel - отменить бронь";
    }
}
