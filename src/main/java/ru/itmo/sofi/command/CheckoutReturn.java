package ru.itmo.sofi.command;

import ru.itmo.sofi.exception.StorageLoadException;
import ru.itmo.sofi.exception.StorageSaveException;
import ru.itmo.sofi.exception.UserInputException;
import ru.itmo.sofi.service.CheckoutService;

import java.util.Scanner;

public class CheckoutReturn extends AbstractCommand {
    private final static String NAME = "checkout_return";
    private final CheckoutService checkoutService;
    private final Scanner scanner;

    public CheckoutReturn(CheckoutService checkoutService, Scanner scanner) {
        this.checkoutService = checkoutService;
        this.scanner = scanner;
    }

    public static String getName() {
        return NAME;
    }

    @Override
    public void execute(String[] args) throws UserInputException, StorageLoadException, StorageSaveException {
        try {
            long checkoutId;
            if (args.length >= 2) {
                checkoutId = Long.parseLong(args[1]);
            } else {
                System.out.print("checkout_id: ");
                checkoutId = Long.parseLong(scanner.nextLine().trim());
            }
            String cond;
            if (args.length >= 3) {
                cond = args[2];
            } else {
                System.out.print("Состояние (OK|DAMAGED): ");
                cond = scanner.nextLine().trim();
            }
            checkoutService.checkoutReturn(checkoutId, cond);
        } catch (NumberFormatException e) {
            throw new UserInputException("id должен быть числом.");
        }
    }

    @Override
    public String gethelp() {
        return "checkout_return - вернуть прибор";
    }
}
