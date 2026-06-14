package ru.itmo.sofi.command;

import ru.itmo.sofi.exception.StorageLoadException;
import ru.itmo.sofi.exception.StorageSaveException;
import ru.itmo.sofi.exception.UserInputException;
import ru.itmo.sofi.service.CheckoutService;

public class CheckoutShow extends AbstractCommand {
    private final static String NAME = "checkout_show";
    private final CheckoutService checkoutService;

    public CheckoutShow(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    public static String getName() {
        return NAME;
    }

    @Override
    public void execute(String[] args) throws UserInputException, StorageLoadException, StorageSaveException {
        try {
            if (args.length != 2) {
                throw new UserInputException("Формат checkout_show <checkout_id>.");
            }
            long checkoutId = Long.parseLong(args[1]);
            checkoutService.checkoutShow(checkoutId);
        } catch (NumberFormatException e) {
            throw new UserInputException("id должен быть числом.");
        }
    }

    @Override
    public String gethelp() {
        return "checkout_show - показ выдачи";
    }
}
