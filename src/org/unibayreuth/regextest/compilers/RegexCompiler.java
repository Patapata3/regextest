package org.unibayreuth.regextest.compilers;

import org.unibayreuth.regextest.automata.Automaton;
import org.unibayreuth.regextest.automata.nondeterministic.NondeterministicAutomaton;

public interface RegexCompiler<T extends NondeterministicAutomaton<?>> {
    T compile(String regex);
}
