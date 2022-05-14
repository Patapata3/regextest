package org.unibayreuth.regextest.automata.states.utils.ncfa;

import org.unibayreuth.regextest.automata.states.NCFAState;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class NCFATransition {
    private Set<NCFAOperation> operations;
    private NCFAState targetState;

    public NCFATransition(Set<NCFAOperation> operations, NCFAState targetState) {
        this.operations = Optional.ofNullable(operations).orElse(new HashSet<>());
        this.targetState = targetState;
    }

    public Set<NCFAOperation> getOperations() {
        return operations;
    }

    public void setOperations(Set<NCFAOperation> operations) {
        this.operations = operations;
    }

    public NCFAState getTargetState() {
        return targetState;
    }

    public void setTargetState(NCFAState targetState) {
        this.targetState = targetState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NCFATransition that = (NCFATransition) o;
        return operations.equals(that.operations) &&
                targetState.equals(that.targetState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operations, targetState);
    }
}
