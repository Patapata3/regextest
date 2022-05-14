package org.unibayreuth.regextest.automata.states;

import org.unibayreuth.regextest.automata.states.utils.ncfa.NCFAOperation;
import org.unibayreuth.regextest.automata.states.utils.ncfa.NCFATransition;

import java.util.*;
import java.util.stream.Collectors;

public class NCFAState {
    private final String regexDerivative;
    private Map<Character, Set<NCFATransition>> transMap = new HashMap<>();
    private Set<NCFAOperation> acceptConditions;

    public NCFAState(String regexDerivative) {
        this.regexDerivative = regexDerivative;
    }

    public Set<NCFATransition> getAllTransitions() {
        return transMap.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public Map<Character, Set<NCFATransition>> getTransMap() {
        return transMap;
    }

    public void setTransMap(Map<Character, Set<NCFATransition>> transMap) {
        this.transMap = transMap;
    }

    public Set<NCFAOperation> getAcceptConditions() {
        return acceptConditions;
    }

    public void setAcceptConditions(Set<NCFAOperation> acceptConditions) {
        this.acceptConditions = acceptConditions;
    }

    public boolean hasTransitions(char c) {
        return transMap.containsKey(c);
    }

    public Set<NCFATransition> getTransitions(char c) {
        return transMap.get(c);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NCFAState ncfaState = (NCFAState) o;
        return regexDerivative.equals(ncfaState.regexDerivative);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regexDerivative);
    }
}
