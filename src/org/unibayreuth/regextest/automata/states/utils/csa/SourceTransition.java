package org.unibayreuth.regextest.automata.states.utils.csa;

import org.unibayreuth.regextest.automata.states.NCFAState;
import org.unibayreuth.regextest.automata.states.utils.ncfa.CFACounter;
import org.unibayreuth.regextest.automata.states.utils.ncfa.NCFAOperation;
import org.unibayreuth.regextest.automata.states.utils.ncfa.NCFATransition;

import java.util.Map;
import java.util.stream.Collectors;

public class SourceTransition extends NCFATransition {
    private NCFAState source;
    private Map<CFACounter, NCFAOperation> operationMap;

    public SourceTransition(NCFAState source, NCFATransition transition) {
        super(transition.getOperations(), transition.getTargetState());
        this.source = source;
        operationMap = transition.getOperations().stream()
                .collect(Collectors.toMap(NCFAOperation::getCounter, operation -> operation));
    }

    public NCFAState getSource() {
        return source;
    }

    public Map<CFACounter, NCFAOperation> getOperationMap() {
        return operationMap;
    }
}
