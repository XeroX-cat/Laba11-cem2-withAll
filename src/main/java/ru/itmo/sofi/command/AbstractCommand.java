package ru.itmo.sofi.command;

import ru.itmo.sofi.exception.StorageLoadException;
import ru.itmo.sofi.exception.StorageSaveException;
import ru.itmo.sofi.exception.UserInputException;

public abstract class AbstractCommand {
    boolean inReqAdditionalInput;

    public abstract void execute(String[] args) throws UserInputException, StorageLoadException, StorageSaveException;

    public abstract String gethelp();

    public boolean isExit() {
        return false;
    }
}
