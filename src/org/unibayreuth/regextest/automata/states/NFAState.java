package org.unibayreuth.regextest.automata.states;

import java.util.*;

public class NFAState {
    private Map<Character, Set<NFAState>> transMap = new HashMap<>();
    private boolean accept = false;

    public void addTransition(Character c, NFAState state) {
        if (!transMap.containsKey(c)) {
            transMap.put(c, new LinkedHashSet<>());
        }
        transMap.get(c).add(state);
    }

    public void addTransitions(Character c, Set<NFAState> states) {
        if (!transMap.containsKey(c)) {
            transMap.put(c, states);
            return;
        }
        transMap.get(c).addAll(states);
    }

    public void setTransMap(Map<Character, Set<NFAState>> transMap) {
        this.transMap = transMap;
    }

    public Map<Character, Set<NFAState>> getAllTransitions() {
        return transMap;
    }

    public Set<NFAState> getTransitions(Character c) {
        return transMap.get(c);
    }

    public boolean hasTransition(Character c) {
        return transMap.containsKey(c);
    }

    public boolean isAccept() {
        return accept;
    }

    public void setAccept(boolean accept) {
        this.accept = accept;
    }
}
