package org.unibayreuth.regextest.automata.states.utils.csa;

import org.unibayreuth.regextest.automata.states.CSAState;

import java.util.Set;

public class CSATransition {
    private CSAState target;
    private Set<CSAGuard> guards;
    private Set<CSAOperation> operations;

    public CSATransition(CSAState target, Set<CSAGuard> guards, Set<CSAOperation> operations) {
        this.target = target;
        this.guards = guards;
        this.operations = operations;
    }

    public CSAState getTarget() {
        return target;
    }

    public void setTarget(CSAState target) {
        this.target = target;
    }

    public Set<CSAGuard> getGuards() {
        return guards;
    }

    public void setGuards(Set<CSAGuard> guards) {
        this.guards = guards;
    }

    public Set<CSAOperation> getOperations() {
        return operations;
    }

    public void setOperations(Set<CSAOperation> operations) {
        this.operations = operations;
    }
}
