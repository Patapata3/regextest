package org.unibayreuth.regextest.commands;

import org.unibayreuth.regextest.automata.deterministic.CSAutomaton;
import org.unibayreuth.regextest.automata.deterministic.DFAutomaton;
import org.unibayreuth.regextest.automata.nondeterministic.NCFAutomaton;
import org.unibayreuth.regextest.automata.nondeterministic.NFAutomaton;
import org.unibayreuth.regextest.compilers.NCFARegexCompiler;
import org.unibayreuth.regextest.compilers.NFARegexCompiler;
import org.unibayreuth.regextest.experiments.DeterministicRunner;
import org.unibayreuth.regextest.experiments.ExperimentRunner;
import org.unibayreuth.regextest.experiments.FastSquaringRunner;
import org.unibayreuth.regextest.experiments.NonDeterministicRunner;
import org.unibayreuth.regextest.fastsquaring.RelationMatcher;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.function.BiFunction;

public class EvalCommand implements Command<String> {
    public static final String NAME = "eval";

    private final Map<String, BiFunction<String, String, ? extends ExperimentRunner>> runnerMap = Map.of(
            NCFAutomaton.TYPE, (regex, input) -> new NonDeterministicRunner(new NCFARegexCompiler(), regex, input),
            NFAutomaton.TYPE, (regex, input) -> new NonDeterministicRunner(new NFARegexCompiler(), regex, input),
            DFAutomaton.TYPE, (regex, input) -> new DeterministicRunner(new NFARegexCompiler(), regex, input),
            CSAutomaton.TYPE, (regex, input) -> new DeterministicRunner(new NCFARegexCompiler(), regex, input),
            RelationMatcher.TYPE, FastSquaringRunner::new
    );

    public String execute(String[] args) {
        String automatonType = args[1].toLowerCase();
        int times = Integer.parseInt(args[args.length - 1]);
        String regex = args[2];
        String input = args[3];
        String[] operations = args.length > 4 ? Arrays.copyOfRange(args, 4, args.length - 1) : new String[]{};
        double result = runnerMap.get(automatonType).apply(regex, input).run(operations, times);
        DecimalFormat format = new DecimalFormat("#.#####");
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format.format(result) + " ms";
    }
}
