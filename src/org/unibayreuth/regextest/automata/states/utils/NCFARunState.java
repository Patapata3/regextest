package org.unibayreuth.regextest.automata.states.utils;

import org.unibayreuth.regextest.automata.states.NCFAState;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class NCFARunState {
    private NCFAState state;
    private Map<NCFACounter, Integer> counterValues;

    private Map<NCFAOpType, Consumer<NCFACounter>> opMap = Map.of(
            NCFAOpType.INCREMENT, this::increment,
            NCFAOpType.EXIT, this::exit,
            NCFAOpType.EXIT1, this::exit1
    );

    public NCFARunState(NCFAState state, Map<NCFACounter, Integer> counterValues) {
        this.state = state;
        this.counterValues = counterValues;
    }

    public Set<NCFARunState> move(char c) {
        if (!state.hasTransitions(c)) {
            return new HashSet<>();
        }
        Set<NCFAOperation> availableOperations = getAvailableOperations();

        return state.getTransitions(c).stream()
                .filter(transition -> availableOperations.containsAll(transition.getOperations()))
                .map(this::move)
                .collect(Collectors.toSet());
    }

    public boolean isAccept() {
        if (state.getAcceptConditions() == null) {
            return false;
        }

        Set<NCFAOperation> availableOperations = getAvailableOperations();
        return availableOperations.containsAll(state.getAcceptConditions());
    }

    private NCFARunState move(NCFATransition transition) {
        NCFARunState newState = new NCFARunState(transition.getTargetState(), new HashMap<>(counterValues));
        newState.apply(transition.getOperations());
        return newState;
    }

    private Set<NCFAOperation> getAvailableOperations() {
        return counterValues.entrySet().stream()
                .map(this::getCounterState)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private void apply(Set<NCFAOperation> operations) {
        operations.forEach(operation -> opMap.get(operation.getOperation()).accept(operation.getCounter()));
    }

    private void increment(NCFACounter counter) {
        counterValues.put(counter, counterValues.get(counter) + 1);
    }

    private void exit(NCFACounter counter) {
        counterValues.put(counter, 0);
    }

    private void exit1(NCFACounter counter) {
        counterValues.put(counter, 1);
    }

    private Set<NCFAOperation> getCounterState(Map.Entry<NCFACounter, Integer> counterValue) {
        NCFACounter counter = counterValue.getKey();
        int value = counterValue.getValue();

        Set<NCFAOperation> availableOperations = new HashSet<>();
        if (value < counter.getMax()) {
            availableOperations.add(new NCFAOperation(NCFAOpType.INCREMENT, counter));
        }
        if (value >= counter.getMin()) {
            availableOperations.add(new NCFAOperation(NCFAOpType.EXIT, counter));
            availableOperations.add(new NCFAOperation(NCFAOpType.EXIT1, counter));
        }

        return availableOperations;
    }
}
