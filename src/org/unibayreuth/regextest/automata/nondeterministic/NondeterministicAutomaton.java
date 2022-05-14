package org.unibayreuth.regextest.automata.nondeterministic;

import org.unibayreuth.regextest.automata.Automaton;

public interface NondeterministicAutomaton<T extends Automaton> extends Automaton {
    T determine();
}
