package ru.itmo.sofi.command;

import ru.itmo.sofi.exception.StorageException;
import ru.itmo.sofi.exception.UserInputException;

public abstract class AbstractCommand {
    boolean inReqAdditionalInput;

    public abstract void execute(String[] args) throws UserInputException, StorageException;

    public abstract String gethelp();

    public boolean isExit() {
        return false;
    }
}
