package org.unibayreuth.regextest.automata.deterministic;

import org.unibayreuth.regextest.automata.Automaton;
import org.unibayreuth.regextest.automata.states.CSAState;

public class CSAutomaton implements Automaton {
    private CSAState startState;

    public CSAutomaton(CSAState startState) {
        this.startState = startState;
    }

    @Override
    public boolean match(String input) {
        return false;
    }
}
