package org.unibayreuth.regextest;

import org.unibayreuth.regextest.commands.ExperimentCommandExecutor;
import org.unibayreuth.regextest.compilers.NFARegexCompiler;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExperimentCommandExecutor executor = new ExperimentCommandExecutor();
        while (true) {
//            System.out.println("Enter regex: ");
//            String regex = scanner.nextLine();
//            System.out.println("Enter your input: ");
//            String input = scanner.nextLine();
//            System.out.println(new NFARegexCompiler().compile(regex).match(input));
            System.out.println("Enter your command:");
            String[] commandArgs = scanner.nextLine().split(" ");

//            NCFARegexCompiler compiler = new NCFARegexCompiler();
//            CSAutomaton automaton = compiler.compile(regex).determine();

            System.out.println(executor.execute(commandArgs) + "ms");
        }
    }
}
