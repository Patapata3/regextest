package org.unibayreuth.regextest.automata.states.utils.ncfa;

import java.util.Objects;

public class NCFAOperation {
    private final NCFAOpType operation;
    private final CFACounter counter;

    public NCFAOperation(NCFAOpType operation, CFACounter counter) {
        this.operation = operation;
        this.counter = counter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NCFAOperation that = (NCFAOperation) o;
        return operation == that.operation &&
                counter.equals(that.counter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operation, counter);
    }

    public NCFAOpType getOperation() {
        return operation;
    }

    public CFACounter getCounter() {
        return counter;
    }
}
