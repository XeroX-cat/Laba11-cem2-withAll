package ru.itmo.sofi.command;

import ru.itmo.sofi.exception.StorageLoadException;
import ru.itmo.sofi.exception.StorageSaveException;
import ru.itmo.sofi.exception.UserInputException;

import java.util.Map;

public class HelpCommand extends AbstractCommand {
    private final Map<String, AbstractCommand> commands;

    public HelpCommand(Map<String, AbstractCommand> commands) {
        this.commands = commands;
    }

    private final static String NAME = "help";

    public static String getName() {
        return NAME;
    }

    @Override
    public void execute(String[] args) throws UserInputException, StorageLoadException, StorageSaveException {
        for (String name : commands.keySet().stream().sorted().toList()) {
            System.out.println(commands.get(name).gethelp());
        }
    }

    @Override
    public String gethelp() {
        return "help - список команд";
    }
}
