package org.unibayreuth.regextest.automata.nondeterministic;

import org.unibayreuth.regextest.automata.deterministic.CSAutomaton;
import org.unibayreuth.regextest.automata.states.CSAState;
import org.unibayreuth.regextest.automata.states.NCFAState;
import org.unibayreuth.regextest.automata.states.utils.csa.*;
import org.unibayreuth.regextest.automata.states.utils.ncfa.*;

import java.util.*;
import java.util.stream.Collectors;

public class NCFAutomaton implements NondeterministicAutomaton<CSAutomaton> {
    private final NCFAState startState;
    private final Set<CFACounter> counters;

    public NCFAutomaton(NCFAState startState, Set<CFACounter> counters) {
        this.startState = startState;
        this.counters = counters;
    }

    @Override
    public boolean match(String input) {
        Set<NCFARunState> activeStates = new HashSet<>();
        Map<CFACounter, Integer> initialValues = counters.stream()
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

    @Override
    public CSAutomaton determine() {
        Map<CFACounter, Set<NCFAState>> scopeMap = calculateScope();
        Map<Set<NCFAState>, CSAState> foundStates = new HashMap<>();

        CSAState start = new CSAState(Collections.singleton(startState));
        Set<CSAState> newStates = Collections.singleton(start);
        while(!newStates.isEmpty()) {
            Set<CSAState> processedStates = newStates;
            newStates = new HashSet<>();
            for (CSAState state : processedStates) {
                newStates.addAll(calculateTransitions(state, scopeMap, foundStates));
            }
        }

        return null;
    }

    private Map<CFACounter, Set<NCFAState>> calculateScope() {
        Map<CFACounter, Set<NCFAState>> scopeMap = counters.stream()
                .collect(Collectors.toMap(counter -> counter, states -> new HashSet<>()));

        Set<NCFAState> foundStates = new HashSet<>();
        foundStates.add(startState);
        Set<NCFAState> newStates = Collections.singleton(startState);
        while (!newStates.isEmpty()) {
            Set<NCFAState> processedStates = newStates;
            newStates = processedStates.stream()
                    .flatMap(state -> calculateStateScope(state, foundStates, scopeMap).stream())
                    .collect(Collectors.toSet());
        }

        return scopeMap;
    }

    private Set<NCFAState> calculateStateScope(NCFAState state, Set<NCFAState> foundStates, Map<CFACounter, Set<NCFAState>> scopeMap) {
        Set<NCFAState> newStates = new HashSet<>();

        for (Set<NCFAState> scope : scopeMap.values()) {
            scope.addAll(state.getAllTransitions()
                    .stream()
                    .map(NCFATransition::getTargetState)
                    .collect(Collectors.toSet()));
        }

        for (NCFATransition transition : state.getAllTransitions()) {
            NCFAState target = transition.getTargetState();
            if (foundStates.add(target)) {
                newStates.add(target);
            }

            scopeMap.forEach(((counter, scope) -> {
                if (transition.getOperations().contains(new NCFAOperation(NCFAOpType.INCREMENT, counter)) ||
                        transition.getOperations().contains(new NCFAOperation(NCFAOpType.EXIT1, counter)) ||
                        (scope.contains(state) && !transition.getOperations().contains(new NCFAOperation(NCFAOpType.EXIT, counter)))
                ) {
                    if (scopeMap.get(counter).add(target)) {
                        newStates.add(target);
                    }
                }
            }));
        }

        return newStates;
    }

    private Set<CSAState> calculateTransitions(CSAState source, Map<CFACounter, Set<NCFAState>> scopeMap, Map<Set<NCFAState>, CSAState> foundStates) {
        Set<CSAState> newStates = new HashSet<>();
        Set<Character> alphabet = source.getPowerSet()
                .stream()
                .flatMap(ncfaState -> ncfaState.getTransMap().keySet().stream())
                .collect(Collectors.toSet());

        for (char c : alphabet) {
            Set<SourceTransition> transitions = source.getPowerSet()
                    .stream()
                    .filter(ncfaState -> ncfaState.hasTransitions(c))
                    .flatMap(ncfaState -> ncfaState.getTransitions(c).stream()
                            .map(transition -> new SourceTransition(ncfaState, transition)))
                    .collect(Collectors.toSet());

            Set<CFACounter> relevantCounters = transitions.stream()
                    .flatMap(transition -> transition.getOperationMap().keySet().stream())
                    .collect(Collectors.toSet());

            if (relevantCounters.isEmpty()) {
                Set<NCFAState> powerSet = transitions.stream()
                        .map(NCFATransition::getTargetState)
                        .collect(Collectors.toSet());
                CSAState target;
                if (!foundStates.containsKey(powerSet)) {
                    target = new CSAState(powerSet);
                    newStates.add(target);
                } else {
                    target = foundStates.get(powerSet);
                }

                CSATransition csaTransition = new CSATransition(target, new HashSet<>(), new HashSet<>());
                source.addTransition(c, csaTransition);
                continue;
            }

            Set<SourceTransition> baseTransitions = transitions.stream()
                    .filter(transition -> transition.getOperations().isEmpty())
                    .collect(Collectors.toSet());

            transitions.removeAll(baseTransitions);

            List<CSAGuard> guardList = relevantCounters.stream()
                    .map(counter -> new CSAGuard(counter, false, true))
                    .collect(Collectors.toList());

            for (int i = 0; i < Math.pow(3, relevantCounters.size()); i++) {

            }
        }
    }

    private Set<CSAOperation> calculateOperations(Map<CFACounter, Set<NCFAState>> scopeMap, Set<SourceTransition> transitions) {
        Set<CSAOperation> operations = new HashSet<>();

        for (CFACounter counter : scopeMap.keySet()) {
            Set<CSAOpType> counterOperations = transitions.stream()
                    .filter(sourceTransition -> scopeMap.get(counter).contains(sourceTransition.getTargetState()))
                    .map(sourceTransition -> getOperation(counter, sourceTransition, scopeMap.get(counter)))
                    .collect(Collectors.toSet());
            operations.add(new CSAOperation(counter, counterOperations));
        }
        return operations;
    }

    private CSAOpType getOperation(CFACounter counter, SourceTransition transition, Set<NCFAState> counterScope) {
        NCFAOperation counterOperation = transition.getOperationMap().get(counter);

        if (counterOperation != null) {
            return getOperation(counterScope.contains(transition.getSource()), counterOperation.getOperation());
        }
        return counterScope.contains(transition.getSource()) ? CSAOpType.NOOP : CSAOpType.RST;
    }

    private CSAOpType getOperation(boolean sourceInScope, NCFAOpType opType) {
        if (opType == NCFAOpType.EXIT) {
            return CSAOpType.RST;
        }
        if (opType == NCFAOpType.EXIT1) {
            return CSAOpType.RST1;
        }

        return sourceInScope ? CSAOpType.INCR : CSAOpType.RST1;
    }

    private void incrementGuards(List<CSAGuard> guardList) {
        for (int i = guardList.size() - 1; i >= 0; i--) {
            CSAGuard previousGuard = guardList.set(i, incrementGuard(guardList.get(i)));
            if (!previousGuard.isExit() || !previousGuard.isIncrement()) {
                return;
            }
        }
    }

    private CSAGuard incrementGuard(CSAGuard guard) {
        if (!guard.isIncrement()) {
            return new CSAGuard(guard.getCounter(), guard.isExit(), true);
        }
        if (!guard.isExit()) {
            return new CSAGuard(guard.getCounter(), true, false);
        }
        return new CSAGuard(guard.getCounter(), false, true);
    }
}
