package org.unibayreuth.regextest.automata.states;

import org.unibayreuth.regextest.automata.states.utils.csa.CSATransition;
import org.unibayreuth.regextest.automata.states.utils.ncfa.CFACounter;
import org.unibayreuth.regextest.automata.states.utils.ncfa.NCFAOperation;

import java.util.*;
import java.util.stream.Collectors;

public class CSAState {
    private final Set<NCFAState> powerSet;
    private Map<Character, Set<CSATransition>> transMap = new HashMap<>();
    private Set<Set<CFACounter>> acceptConditions;

    public CSAState(Set<NCFAState> powerSet) {
        this.powerSet = powerSet;
        acceptConditions = powerSet.stream()
                .filter(state -> state.getAcceptConditions() != null)
                .map(state -> state.getAcceptConditions().stream()
                        .map(NCFAOperation::getCounter)
                        .collect(Collectors.toSet()))
                .collect(Collectors.toSet());
    }

    public Set<NCFAState> getPowerSet() {
        return powerSet;
    }

    public void addTransition(char c, CSATransition transition) {
        transMap.putIfAbsent(c, new HashSet<>());
        transMap.get(c).add(transition);
    }

    public Map<Character, Set<CSATransition>> getTransMap() {
        return transMap;
    }

    public void setTransMap(Map<Character, Set<CSATransition>> transMap) {
        this.transMap = transMap;
    }

    public Set<Set<CFACounter>> getAcceptConditions() {
        return acceptConditions;
    }

    public void setAcceptConditions(Set<Set<CFACounter>> acceptConditions) {
        this.acceptConditions = acceptConditions;
    }
}
