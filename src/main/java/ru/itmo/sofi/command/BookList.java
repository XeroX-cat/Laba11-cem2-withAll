package ru.itmo.sofi.command;

import ru.itmo.sofi.essence.booking.Booking;
import ru.itmo.sofi.exception.UserInputException;
import ru.itmo.sofi.service.BookingService;

public class BookList extends AbstractCommand {
    private final static String NAME = "book_list";
    private final BookingService bookingService;

    public BookList(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public static String getName() {
        return NAME;
    }

    @Override
    public void execute(String[] args) throws UserInputException {
        try {
            if (args.length < 2) {
                throw new UserInputException("Нужно указать id инструмента.");
            }
            long instrumentId = Long.parseLong(args[1]);
            String from = null;

            if (args.length == 4 && "--from".equals(args[2])) {
                from = args[3];
            } else if (args.length != 2 && args.length != 4) {
                throw new UserInputException("Формат book_list <instrument_id>");
            }

            bookingService.bookList(instrumentId, from);
        } catch (NumberFormatException e) {
            throw new UserInputException("id должен быть числом");
        }
    }

    @Override
    public String gethelp() {
        return "book_list - показать все записи";
    }
}
