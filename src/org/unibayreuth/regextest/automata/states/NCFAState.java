package org.unibayreuth.regextest.automata.states;

import org.unibayreuth.regextest.automata.states.utils.NCFAOperation;
import org.unibayreuth.regextest.automata.states.utils.NCFATransition;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NCFAState {
    private String regexDerivative;
    private Map<Character, Set<NCFATransition>> transMap = new HashMap<>();
    private Set<NCFAOperation> acceptConditions;

    public NCFAState(String regexDerivative) {
        this.regexDerivative = regexDerivative;
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
}
