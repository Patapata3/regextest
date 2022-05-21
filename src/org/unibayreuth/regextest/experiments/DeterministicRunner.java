package org.unibayreuth.regextest.experiments;

import org.unibayreuth.regextest.automata.Automaton;
import org.unibayreuth.regextest.automata.nondeterministic.NondeterministicAutomaton;
import org.unibayreuth.regextest.compilers.RegexCompiler;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.unibayreuth.regextest.experiments.utils.Operations.*;

public class DeterministicRunner extends ExperimentRunner {

    public DeterministicRunner(RegexCompiler<?> compiler, String regex, String input) {
        super(compiler, regex, input);
    }

    @Override
    public double run(String[] operations, int times) {
        if (operations.length == 2) {
            Set<String> operationSet = Arrays.stream(operations)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
            if (operationSet.contains(COMPILE)) {
                return measure(times, () -> compiler.compile(regex).determine().match(input));
            }
            NondeterministicAutomaton<?> automaton = compiler.compile(regex);
            return measure(times, () -> automaton.determine().match(input));
        }

        String operation = operations[0].toLowerCase();
        if (operation.equals(COMPILE)) {
            return measure(times, () -> compiler.compile(regex).determine());
        }

        if (operation.equals(DETERMINE)) {
            NondeterministicAutomaton<?> automaton = compiler.compile(regex);
            return measure(times, automaton::determine);
        }

        Automaton automaton = compiler.compile(regex).determine();
        return measure(times, () -> automaton.match(input));
    }
}
