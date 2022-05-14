package org.unibayreuth.regextest.automata.states;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DFAState {
    private final Set<NFAState> powerSet;
    private final Map<Character, DFAState> transMap = new HashMap<>();
    private final boolean accept;

    public DFAState(Set<NFAState> powerSet) {
        this.powerSet = powerSet;
        this.accept = powerSet.stream().anyMatch(NFAState::isAccept);
    }

    public Set<NFAState> getPowerSet() {
        return powerSet;
    }

    public void addTransition(char c, DFAState target) {
        transMap.put(c, target);
    }

    public DFAState move(char c) {
        return transMap.get(c);
    }

    public boolean hasTransition(char c) {
        return transMap.containsKey(c);
    }

    public boolean isAccept() {
        return accept;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DFAState state = (DFAState) o;
        return powerSet.equals(state.powerSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(powerSet);
    }
}
