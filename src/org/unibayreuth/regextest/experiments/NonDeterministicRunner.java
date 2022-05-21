package org.unibayreuth.regextest.experiments;

import org.unibayreuth.regextest.automata.Automaton;
import org.unibayreuth.regextest.compilers.RegexCompiler;

import java.util.Arrays;

import static org.unibayreuth.regextest.experiments.utils.Operations.*;

public class NonDeterministicRunner extends ExperimentRunner {
    public NonDeterministicRunner(RegexCompiler<?> compiler, String regex, String input) {
        super(compiler, regex, input);
    }

    @Override
    public double run(String[] operations, int times) {
        if (operations.length == 2) {
            return measure(times, () -> compiler.compile(regex).match(input));
        }
        String operation = operations[0].toLowerCase();
        if (operation.equals(COMPILE)) {
            return measure(times, () -> compiler.compile(regex));
        }
        Automaton automaton = compiler.compile(regex);
        return measure(times, () -> automaton.match(input));
    }
}
