package ru.itmo.sofi.command;

import ru.itmo.sofi.exception.UserInputException;
import ru.itmo.sofi.service.CheckoutService;

import java.util.Scanner;

public class CheckoutTake extends AbstractCommand {
    private final static String NAME = "checkout_take";
    private final CheckoutService checkoutService;
    private final Scanner scanner;

    public CheckoutTake(CheckoutService checkoutService, Scanner scanner) {
        this.checkoutService = checkoutService;
        this.scanner = scanner;
    }

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
            System.out.print("Кто берёт: ");
            String username = scanner.nextLine();
            System.out.print("Комментарий (можно пустой): ");
            String comment = scanner.nextLine();
            checkoutService.checkoutTake(instrumentId, username, comment, "SYSTEM");
        } catch (NumberFormatException e) {
            throw new UserInputException("id должен быть числом.");
        }
    }

    @Override
    public String gethelp() {
        return "checkout_take - выдать прибор";
    }
}
