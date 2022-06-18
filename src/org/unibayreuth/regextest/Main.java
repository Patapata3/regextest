package org.unibayreuth.regextest;

import org.unibayreuth.regextest.commands.CommandExecutor;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CommandExecutor executor = new CommandExecutor();
//        System.out.println("Enter String length:");
//        int length = Integer.parseInt(scanner.nextLine());
//
//        Random random = new Random();
//        String input = random.ints('a', 'b' + 1)
//                .limit(length)
//                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
//                .toString();

        while (true) {
            System.out.println("Enter your command:");
            String[] commandArgs = scanner.nextLine().split(" ");
            //commandArgs[3] = input;
            executor.execute(commandArgs);
        }
    }
}
