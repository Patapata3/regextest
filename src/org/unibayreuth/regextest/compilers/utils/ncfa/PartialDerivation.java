package org.unibayreuth.regextest.compilers.utils.ncfa;

import org.unibayreuth.regextest.automata.states.utils.NCFACounter;
import org.unibayreuth.regextest.automata.states.utils.NCFAOpType;
import org.unibayreuth.regextest.automata.states.utils.NCFAOperation;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;


public class PartialDerivation {
    private Set<NCFAOperation> operations;
    private List<RegexElement> remainder;

    public PartialDerivation(Set<NCFAOperation> operations, List<RegexElement> remainder) {
        this.operations = ofNullable(operations).orElse(new HashSet<>());
        this.remainder = remainder;
    }

    public Set<NCFAOperation> getOperations() {
        return operations;
    }

    public List<RegexElement> getRemainder() {
        return remainder;
    }

    public PartialDerivation compose(PartialDerivation other) {
        if (operations.isEmpty() || other.getOperations().isEmpty()) {
            List<RegexElement> newRemainder = new ArrayList<>(remainder);
            newRemainder.addAll(other.getRemainder());
            return new PartialDerivation(operations.isEmpty() ? other.getOperations() : operations, newRemainder);
        }

        Map<NCFACounter, NCFAOperation> otherOperationsMap = other.getOperations()
                .stream()
                .collect(Collectors.toMap(NCFAOperation::getCounter, operation -> operation));
        Set<NCFAOperation> unprocessedOperations = new HashSet<>(operations);
        unprocessedOperations.addAll(other.getOperations());
        Set<NCFAOperation> composedOperations = new HashSet<>();
        for (NCFAOperation operation : operations) {
            NCFAOperation otherOperation = otherOperationsMap.get(operation.getCounter());
            if (otherOperation != null) {
                NCFAOperation composedOperation = composeOperations(operation, otherOperation);
                if (composedOperation == null) {
                    return null;
                }
                composedOperations.add(composedOperation);
                unprocessedOperations.remove(operation);
                unprocessedOperations.remove(otherOperation);
            }
        }
        composedOperations.addAll(unprocessedOperations);

        List<RegexElement> newRemainder = new ArrayList<>(remainder);
        newRemainder.addAll(other.getRemainder());
        return new PartialDerivation(composedOperations, newRemainder);
    }

    private NCFAOperation composeOperations(NCFAOperation operation, NCFAOperation otherOperation) {
        if (operation.getOperation() == NCFAOpType.EXIT && otherOperation.getOperation() == NCFAOpType.INCREMENT) {
            return new NCFAOperation(NCFAOpType.EXIT1, operation.getCounter());
        }
        if (operation.getOperation() == NCFAOpType.EXIT && otherOperation.getOperation() == NCFAOpType.EXIT) {
            return operation.getCounter().getMin() == 0 ? new NCFAOperation(NCFAOpType.EXIT, operation.getCounter()) : null;
        }
        return null;
    }
}
