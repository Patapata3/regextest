package org.unibayreuth.regextest.automata.deterministic;

import org.unibayreuth.regextest.automata.Automaton;
import org.unibayreuth.regextest.automata.states.DFAState;

public class DFAutomaton implements Automaton {
    private DFAState startState;

    public DFAutomaton(DFAState startState) {
        this.startState = startState;
    }

    @Override
    public boolean match(String input) {
        DFAState activeState = startState;
        for (char c : input.toCharArray()) {
            if (!activeState.hasTransition(c)) {
                return false;
            }
            activeState = activeState.move(c);
        }
        return activeState.isAccept();
    }
}
