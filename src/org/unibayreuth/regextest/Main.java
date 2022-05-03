package org.unibayreuth.regextest;

import org.unibayreuth.regextest.automata.NFAutomaton;
import org.unibayreuth.regextest.compilers.NFARegexCompiler;

import java.io.Console;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter regex: ");
        String regex = scanner.nextLine();
        System.out.println("Enter your input: ");
        String input = scanner.nextLine();

        NFARegexCompiler compiler = new NFARegexCompiler();
        NFAutomaton automaton = compiler.compile(regex);
        System.out.println(automaton.match(input));
    }
}
