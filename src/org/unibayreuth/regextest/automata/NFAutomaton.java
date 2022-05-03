package org.unibayreuth.regextest.automata;

import com.google.common.collect.Sets;
import org.unibayreuth.regextest.automata.states.NFAState;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class NFAutomaton implements Automaton {
    private Set<NFAState> startStates;

    public NFAutomaton(Set<NFAState> startStates) {
        this.startStates = startStates;
    }

    @Override
    public boolean match(String input) {
        Set<NFAState> activeStates = epsilonClosure(startStates);

        for (char c : input.toCharArray()) {
            activeStates = activeStates.stream()
                    .filter(state -> state.hasTransition(c))
                    .flatMap(state -> state.getTransitions(c).stream())
                    .collect(Collectors.toSet());
            activeStates = epsilonClosure(activeStates);

            if (activeStates.isEmpty()) {
                return false;
            }
        }

        return activeStates.stream()
                .anyMatch(NFAState::isAccept);
    }

    private Set<NFAState> epsilonClosure(Set<NFAState> states) {
        Set<NFAState> foundStates = new HashSet<>(states);
        for (NFAState state : states) {
            foundStates.addAll(epsilonClosure(state, foundStates));
        }
        return foundStates;
    }

    private Set<NFAState> epsilonClosure(NFAState state, Set<NFAState> foundStates) {
        Set<NFAState> epsilonTransitions = state.getTransitions(null);
        if (epsilonTransitions == null || epsilonTransitions.isEmpty()) {
            return new HashSet<>();
        }

        Set<NFAState> newStates = new HashSet<>(epsilonTransitions);
        for (NFAState epsilonState : epsilonTransitions) {
            if (!foundStates.contains(epsilonState)) {
                newStates.addAll(epsilonClosure(epsilonState, Sets.union(newStates, foundStates)));
            }
        }
        return newStates;
    }
}
