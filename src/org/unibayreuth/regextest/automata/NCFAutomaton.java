package org.unibayreuth.regextest.automata;

import org.unibayreuth.regextest.automata.states.NCFAState;
import org.unibayreuth.regextest.automata.states.utils.NCFACounter;

import java.util.Set;

public class NCFAutomaton implements Automaton {
    private NCFAState startState;
    private Set<NCFACounter> counters;

    public NCFAutomaton(NCFAState startState, Set<NCFACounter> counters) {
        this.startState = startState;
        this.counters = counters;
    }

    @Override
    public boolean match(String input) {
        return false;
    }
}
