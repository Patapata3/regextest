package org.unibayreuth.regextest;

import org.unibayreuth.regextest.automata.deterministic.DFAutomaton;
import org.unibayreuth.regextest.automata.nondeterministic.NCFAutomaton;
import org.unibayreuth.regextest.automata.nondeterministic.NFAutomaton;
import org.unibayreuth.regextest.compilers.NCFARegexCompiler;
import org.unibayreuth.regextest.compilers.NFARegexCompiler;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter regex: ");
            String regex = scanner.nextLine();
            System.out.println("Enter your input: ");
            String input = scanner.nextLine();

            NFARegexCompiler compiler = new NFARegexCompiler();
            DFAutomaton automaton = compiler.compile(regex).determine();

            System.out.println(automaton.match(input));
        }
    }
}
