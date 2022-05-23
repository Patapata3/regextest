package org.unibayreuth.regextest;

import org.unibayreuth.regextest.commands.CommandExecutor;
import org.unibayreuth.regextest.commands.ExperimentCommand;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CommandExecutor executor = new CommandExecutor();

        while (true) {
            System.out.println("Enter your command:");
            String[] commandArgs = scanner.nextLine().split(" ");
            executor.execute(commandArgs);
        }
    }
}
