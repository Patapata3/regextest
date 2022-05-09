package org.unibayreuth.regextest.automata;

import org.unibayreuth.regextest.automata.states.NCFAState;
import org.unibayreuth.regextest.automata.states.utils.NCFACounter;
import org.unibayreuth.regextest.automata.states.utils.NCFARunState;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NCFAutomaton implements Automaton {
    private NCFAState startState;
    private Set<NCFACounter> counters;

    public NCFAutomaton(NCFAState startState, Set<NCFACounter> counters) {
        this.startState = startState;
        this.counters = counters;
    }

    @Override
    public boolean match(String input) {
        Set<NCFARunState> activeStates = new HashSet<>();
        Map<NCFACounter, Integer> initialValues = counters.stream()
                .collect(Collectors.toMap(counter -> counter, value -> 0));
        activeStates.add(new NCFARunState(startState, initialValues));

        for (char c : input.toCharArray()) {
            activeStates = activeStates.stream()
                    .flatMap(stateConfig -> stateConfig.move(c).stream())
                    .collect(Collectors.toSet());

            if (activeStates.isEmpty()) {
                return false;
            }
        }


        return activeStates.stream()
                .anyMatch(NCFARunState::isAccept);
    }
}
