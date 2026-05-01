package ru.itmo.sofi.command;

import ru.itmo.sofi.essence.instrument.InstrumentType;
import ru.itmo.sofi.service.CheckoutService;

import java.util.Scanner;

public class InstAvailable extends AbstractCommand {
    private final static String NAME = "inst_available";
    private final CheckoutService checkoutService;
    private final Scanner scanner;

    public InstAvailable(CheckoutService checkoutService, Scanner scanner) {
        this.checkoutService = checkoutService;
        this.scanner = scanner;
    }

    public static String getName() {
        return NAME;
    }

    @Override
    public void execute(String[] args) {
        String type;
        String start;
        String end;
        if (args.length >= 2) {
            type = args[1];
        } else {
            System.out.println("Тип прибора:");
            for (InstrumentType t : InstrumentType.values()) {
                System.out.println(" - " + t);
            }
            System.out.print("> ");
            type = scanner.nextLine().trim();
        }
        System.out.print("Начало (YYYY-MM-DD HH:MM): ");
        start = scanner.nextLine().trim();
        System.out.print("Конец (YYYY-MM-DD HH:MM): ");
        end = scanner.nextLine().trim();
        checkoutService.instAvailable(type, start, end);
    }

    @Override
    public String gethelp() {
        return "inst_available - показ доступного на данное время";
    }
}
