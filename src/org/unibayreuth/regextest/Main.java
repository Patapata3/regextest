package org.unibayreuth.regextest;

import org.unibayreuth.regextest.fastsquaring.RelationMatcher;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter regex: ");
            String regex = scanner.nextLine();
            System.out.println("Enter your input: ");
            String input = scanner.nextLine();

//            NCFARegexCompiler compiler = new NCFARegexCompiler();
//            CSAutomaton automaton = compiler.compile(regex).determine();

            System.out.println(new RelationMatcher().match(regex, input));
        }
    }
}
