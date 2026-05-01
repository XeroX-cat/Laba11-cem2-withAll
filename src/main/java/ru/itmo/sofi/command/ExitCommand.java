package ru.itmo.sofi.command;

public class ExitCommand extends AbstractCommand {
    private final static String NAME = "exit";

    public static String getName() {
        return NAME;
    }

    @Override
    public void execute(String[] args) {
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
