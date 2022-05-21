package org.unibayreuth.regextest.automata.states.utils.nfa;

import org.unibayreuth.regextest.automata.states.NFAState;
import org.unibayreuth.regextest.compilers.utils.CompileUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class EpsilonStateConfig {
    private NFAState state;
    private NFAState processedChild;
    private Iterator<NFAState> epsilonIterator;
    private Set<NFAState> epsilonTransitions;
    private Set<NFAState> closure;
    private Set<NFAState> foundStates;

    public EpsilonStateConfig(NFAState state, Set<NFAState> foundStates) {
        this.state = state;
        this.foundStates = foundStates;
        epsilonIterator = state.getTransitions(null) == null ? Collections.emptyIterator() : state.getTransitions(null).iterator();
        epsilonTransitions = state.getTransitions(null) == null ? new HashSet<>() : state.getTransitions(null);
        closure = new HashSet<>(epsilonTransitions);
    }

    public NFAState getState() {
        return state;
    }

    public Iterator<NFAState> getEpsilonIterator() {
        return epsilonIterator;
    }

    public Set<NFAState> getFoundStates() {
        return foundStates;
    }

    public Set<NFAState> getEpsilonTransitions() {
        return epsilonTransitions;
    }

    public Set<NFAState> getClosure() {
        return closure;
    }

    public Set<NFAState> getChildFoundStates() {
        return CompileUtils.setUnion(closure, foundStates);
    }

    public NFAState getProcessedChild() {
        return processedChild;
    }

    public void setProcessedChild(NFAState processedChild) {
        this.processedChild = processedChild;
    }
}
