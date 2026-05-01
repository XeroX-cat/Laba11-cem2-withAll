package ru.itmo.sofi.command;

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
    public void execute(String[] args) {
        for (String name : commands.keySet().stream().sorted().toList()) {
            System.out.println(commands.get(name).gethelp());
        }
    }

    @Override
    public String gethelp() {
        return "help - список команд";
    }
}
