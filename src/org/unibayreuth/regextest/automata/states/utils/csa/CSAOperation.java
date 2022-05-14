package org.unibayreuth.regextest.automata.states.utils.csa;

import org.unibayreuth.regextest.automata.states.utils.ncfa.CFACounter;

import java.util.Set;

public class CSAOperation {
    private final CFACounter counter;
    private final Set<CSAOpType> operation;

    public CSAOperation(CFACounter counter, Set<CSAOpType> operation) {
        this.counter = counter;
        this.operation = operation;
    }

    public CFACounter getCounter() {
        return counter;
    }

    public Set<CSAOpType> getOperation() {
        return operation;
    }
}
