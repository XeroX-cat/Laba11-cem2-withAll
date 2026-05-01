package ru.itmo.sofi.command;

import ru.itmo.sofi.exception.UserInputException;
import ru.itmo.sofi.service.CheckoutService;

public class CheckoutList extends AbstractCommand {
    private final static String NAME = "checkout_list";
    private final CheckoutService checkoutService;

    public CheckoutList(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    public static String getName() {
        return NAME;
    }

    @Override
    public void execute(String[] args) throws UserInputException {
        boolean openOnly = args.length == 2 && "--open-only".equals(args[1]);

        if (args.length != 1 && !openOnly) {
            throw new UserInputException("Формат checkout_list [--open-only]");
        }
        checkoutService.checkoutList(openOnly);
    }

    @Override
    public String gethelp() {
        return "checkout_list - список выдачи";
    }
}
