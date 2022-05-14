package org.unibayreuth.regextest.automata.nondeterministic;

import com.google.common.collect.Sets;
import org.unibayreuth.regextest.automata.deterministic.DFAutomaton;
import org.unibayreuth.regextest.automata.states.DFAState;
import org.unibayreuth.regextest.automata.states.NFAState;

import java.util.*;
import java.util.stream.Collectors;

public class NFAutomaton implements NondeterministicAutomaton<DFAutomaton> {
    private final Set<NFAState> startStates;

    public NFAutomaton(Set<NFAState> startStates) {
        this.startStates = startStates;
    }

    @Override
    public boolean match(String input) {
        Map<NFAState, Set<NFAState>> closureCache = new HashMap<>();
        Set<NFAState> activeStates = epsilonClosure(startStates, closureCache);

        for (char c : input.toCharArray()) {
            activeStates = activeStates.stream()
                    .filter(state -> state.hasTransition(c))
                    .flatMap(state -> state.getTransitions(c).stream())
                    .collect(Collectors.toSet());
            activeStates = epsilonClosure(activeStates, closureCache);

            if (activeStates.isEmpty()) {
                return false;
            }
        }

        return activeStates.stream()
                .anyMatch(NFAState::isAccept);
    }

    @Override
    public DFAutomaton determine() {
        Map<NFAState, Set<NFAState>> closureCache = new HashMap<>();
        Map<Set<NFAState>, DFAState> foundStatesMap = new HashMap<>();

        DFAState startState = new DFAState(epsilonClosure(startStates, closureCache));
        foundStatesMap.put(startState.getPowerSet(), startState);

        Set<DFAState> newStates = Collections.singleton(startState);
        while (!newStates.isEmpty()) {
            Set<DFAState> processedStates = newStates;
            newStates = new HashSet<>();
            for (DFAState state : processedStates) {
                newStates.addAll(calculateTransitions(state, closureCache, foundStatesMap));
            }
        }

        return new DFAutomaton(startState);
    }

    private Set<NFAState> epsilonClosure(Set<NFAState> states, Map<NFAState, Set<NFAState>> closureCache) {
        Set<NFAState> foundStates = new HashSet<>(states);
        for (NFAState state : states) {
            if (!closureCache.containsKey(state)) {
                closureCache.put(state, epsilonClosure(state, foundStates, closureCache));
            }
            foundStates.addAll(closureCache.get(state));
        }
        return foundStates;
    }

    private Set<NFAState> epsilonClosure(NFAState state, Set<NFAState> foundStates, Map<NFAState, Set<NFAState>> closureCache) {
        Set<NFAState> epsilonTransitions = state.getTransitions(null);
        if (epsilonTransitions == null || epsilonTransitions.isEmpty()) {
            return new HashSet<>();
        }

        Set<NFAState> newStates = new HashSet<>(epsilonTransitions);
        for (NFAState epsilonState : epsilonTransitions) {
            if (!foundStates.contains(epsilonState)) {
                if (!closureCache.containsKey(epsilonState)) {
                    closureCache.put(epsilonState, epsilonClosure(epsilonState, Sets.union(newStates, foundStates), closureCache));
                }
                newStates.addAll(closureCache.get(epsilonState));
            }
        }
        return newStates;
    }

    private Set<DFAState> calculateTransitions(DFAState state, Map<NFAState, Set<NFAState>> closureCache, Map<Set<NFAState>, DFAState> foundStatesMap) {
        Set<DFAState> newStates = new HashSet<>();
        Map<Character, Set<NFAState>> powerStateMap = new HashMap<>();

        for (NFAState nfaState : state.getPowerSet()) {
            nfaState.getAllTransitions().entrySet()
                    .stream()
                    .filter(entry -> entry.getKey() != null)
                    .forEach(entry -> {
                        char c = entry.getKey();
                        if (!powerStateMap.containsKey(c)) {
                            powerStateMap.put(c, new HashSet<>());
                        }
                        powerStateMap.get(c).addAll(epsilonClosure(entry.getValue(), closureCache));
                    });
        }

        powerStateMap.forEach((character, powerSet) -> {
            DFAState target;
            if (!foundStatesMap.containsKey(powerSet)) {
                target = new DFAState(powerSet);
                foundStatesMap.put(powerSet, target);
                newStates.add(target);
            } else {
                target = foundStatesMap.get(powerSet);
            }
            state.addTransition(character, target);
        });

        return newStates;
    }
}
