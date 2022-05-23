package org.unibayreuth.regextest.commands;

import java.util.Map;

public class CommandExecutor {
    private Map<String, Command<?>> commandMap = Map.of(
            MatchCommand.NAME, new MatchCommand(),
            ExperimentCommand.NAME, new ExperimentCommand()
    );

    public void execute(String[] args) {
        if (args.length == 0) {
            System.out.println("Command cannot be empty!");
            return;
        }

        String command = args[0];

        if (!commandMap.containsKey(command)) {
            System.out.println("Command not found!");
            return;
        }

        try{
            Object result = commandMap.get(command).execute(args);
            System.out.println(result.toString());
        } catch (Exception e) {
            System.out.printf("Error occurred during execution: %s%n", e.getMessage());
        }
    }
}
