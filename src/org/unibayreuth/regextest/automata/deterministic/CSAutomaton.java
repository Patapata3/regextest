package org.unibayreuth.regextest.automata.deterministic;

import org.unibayreuth.regextest.automata.Automaton;
import org.unibayreuth.regextest.automata.states.CSAState;
import org.unibayreuth.regextest.automata.states.utils.csa.CSAGuard;
import org.unibayreuth.regextest.automata.states.utils.csa.CSAOperation;
import org.unibayreuth.regextest.automata.states.utils.csa.CSATransition;
import org.unibayreuth.regextest.automata.states.utils.csa.CountingSet;
import org.unibayreuth.regextest.automata.states.utils.ncfa.CFACounter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CSAutomaton implements Automaton {
    private CSAState startState;
    private Set<CFACounter> counters;

    public CSAutomaton(CSAState startState, Set<CFACounter> counters) {
        this.startState = startState;
        this.counters = counters;
    }

    @Override
    public boolean match(String input) {
        Map<CFACounter, CountingSet> countingSets = counters.stream()
                .collect(Collectors.toMap(key -> key, CountingSet::new));

        CSAState activeState = startState;
        for (char c : input.toCharArray()) {
            if (!activeState.hasTransition(c)) {
                return false;
            }
            activeState = move(c, activeState, countingSets);
            if (activeState == null) {
                return false;
            }
        }

        return activeState.getAcceptConditions()
                .stream()
                .anyMatch(conditionSet -> conditionSet.stream()
                        .allMatch(counter -> countingSets.get(counter).canExit()));
    }

    private CSAState move(char c, CSAState source, Map<CFACounter, CountingSet> countingSets) {
        CSATransition activeTransition = source.getTransitions(c)
                .stream()
                .filter(transition -> guardsSatisfied(transition.getGuards(), countingSets))
                .findFirst()
                .orElse(null);

        if (activeTransition == null) {
            return null;
        }

        Set<CFACounter> affectedCounters = new HashSet<>();

        for (CSAOperation operation : activeTransition.getOperations()) {
            CFACounter affectedCounter = operation.getCounter();
            affectedCounters.add(affectedCounter);
            countingSets.get(affectedCounter).apply(operation.getOperation());
        }

        countingSets.keySet()
                .stream()
                .filter(counter -> !affectedCounters.contains(counter))
                .forEach(counter -> {
                    countingSets.put(counter, new CountingSet(counter));
                });

        return activeTransition.getTarget();
    }

    private boolean guardsSatisfied(Set<CSAGuard> guards, Map<CFACounter, CountingSet> countingSets) {
        return guards.stream()
                .allMatch(guard -> guard.isExit() == countingSets.get(guard.getCounter()).canExit() &&
                                    guard.isIncrement() == countingSets.get(guard.getCounter()).canIncr());
    }
}
