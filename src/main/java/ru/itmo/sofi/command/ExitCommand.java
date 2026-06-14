package ru.itmo.sofi.command;

import ru.itmo.sofi.exception.StorageLoadException;
import ru.itmo.sofi.exception.StorageSaveException;
import ru.itmo.sofi.exception.UserInputException;

public class ExitCommand extends AbstractCommand {
    private final static String NAME = "exit";

    public static String getName() {
        return NAME;
    }

    @Override
    public void execute(String[] args) throws UserInputException, StorageLoadException, StorageSaveException {
    }

    @Override
    public boolean isExit() {
        return true;
    }

    @Override
    public String gethelp() {
        return "exit - завершить программу";
    }
}
