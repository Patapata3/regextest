package org.unibayreuth.regextest.commands;

import com.google.common.collect.Sets;
import org.unibayreuth.regextest.automata.deterministic.CSAutomaton;
import org.unibayreuth.regextest.automata.deterministic.DFAutomaton;
import org.unibayreuth.regextest.automata.nondeterministic.NCFAutomaton;
import org.unibayreuth.regextest.automata.nondeterministic.NFAutomaton;
import org.unibayreuth.regextest.automata.nondeterministic.NondeterministicAutomaton;
import org.unibayreuth.regextest.compilers.NCFARegexCompiler;
import org.unibayreuth.regextest.compilers.NFARegexCompiler;
import org.unibayreuth.regextest.compilers.RegexCompiler;
import org.unibayreuth.regextest.fastsquaring.RelationMatcher;

import java.util.Map;
import java.util.Set;

public class MatchCommand implements Command<Boolean> {
    public static final String NAME = "match";

    private final Map<String, RegexCompiler<? extends NondeterministicAutomaton<?>>> compilerMap = Map.of(
            NFAutomaton.TYPE, new NFARegexCompiler(),
            DFAutomaton.TYPE, new NFARegexCompiler(),
            NCFAutomaton.TYPE, new NCFARegexCompiler(),
            CSAutomaton.TYPE, new NCFARegexCompiler()
    );

    private final Set<String> deterministicTypes = Sets.newHashSet(DFAutomaton.TYPE, CSAutomaton.TYPE);

    @Override
    public Boolean execute(String[] args) {
        String algorithm = args[1];
        if (algorithm.equalsIgnoreCase(RelationMatcher.TYPE)) {
            return new RelationMatcher().match(args[2], args[3]);
        }

        NondeterministicAutomaton<?> compiledAutomaton = compilerMap.get(algorithm).compile(args[2]);
        return deterministicTypes.contains(algorithm) ? compiledAutomaton.determine().match(args[3]) : compiledAutomaton.match(args[3]);
    }
}
