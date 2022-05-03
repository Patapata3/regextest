package org.unibayreuth.regextest.compilers;

import com.google.common.collect.Sets;
import org.unibayreuth.regextest.automata.NFAutomaton;
import org.unibayreuth.regextest.automata.states.NFAState;
import org.unibayreuth.regextest.compilers.utils.NFAWrapper;
import org.unibayreuth.regextest.compilers.utils.StackConfig;

import java.util.*;
import java.util.function.Function;

public class NFARegexCompiler implements RegexCompiler<NFAutomaton>{
    private Map<Character, Function<StackConfig, StackConfig>> infixOperations = new HashMap<>(Map.of(
            '.', this::processConcat,
            '|', this::processUnion
    ));
    private Map<Character, Function<NFAWrapper, NFAWrapper>> postfixOperations = new HashMap<>(Map.of(
            '*', this::kleeneStar,
            '+', this::kleenePlus,
            '?', this::optional
    ));


    @Override
    public String getType() {
        return "nfa";
    }

    @Override
    public NFAutomaton compile(String regex) {
        StackConfig stackConfig = new StackConfig();
        boolean isEscape = false;
        boolean isConcat = false;
        int bracketsCount = 0;

        for (char c : regex.toCharArray()) {
            if (c == '\\' && !isEscape) {
                isEscape = true;
                continue;
            }

            if (!isEscape && (c == '|')) {
                stackConfig.pushOp(c);
                isConcat = false;
            } else if (!isEscape && (c == '(')) {
                if (isConcat) {
                    stackConfig.pushOp('.');
                }

                stackConfig.pushOp(c);
                isConcat = false;
                bracketsCount++;
            } else if (!isEscape && postfixOperations.containsKey(c)) {
                stackConfig.pushNfa(postfixOperations.get(c).apply(stackConfig.popNfa()));
            } else if (! isEscape && c == ')') {
                stackConfig = calculateBrackets(stackConfig, bracketsCount);
                bracketsCount--;
                stackConfig.popOp();
            } else {
                stackConfig.pushNfa(createAtomicNfa(c));
                if (isConcat) {
                    stackConfig.pushOp('.');
                }
                isConcat = true;
            }
            isEscape = false;
        }

        if (bracketsCount != 0) {
            throw new IllegalArgumentException("Invalid regex: more beginning parentheses than end parentheses");
        }

        while (!stackConfig.getOperatorStack().empty()) {
            if (stackConfig.getOperatorStack().empty()) {
                throw new IllegalArgumentException("Invalid regex: imbalance in operands and operators");
            }
            char nextOp = stackConfig.popOp();
            if (infixOperations.containsKey(nextOp)) {
                stackConfig = infixOperations.get(nextOp).apply(stackConfig);
            }
        }

        NFAWrapper finalNfa = stackConfig.popNfa();
        finalNfa.getFinish().setAccept(true);

        return finalNfa.getNfa();
    }

    private StackConfig calculateBrackets(StackConfig stackConfig, int bracketsCount) {
        if (bracketsCount == 0) {
            throw new IllegalArgumentException("Invalid regex: more end parentheses than beginning parentheses");
        }
        while (!stackConfig.getOperatorStack().empty() && stackConfig.getOperatorStack().peek() != '(') {
            stackConfig = infixOperations.get(stackConfig.getOperatorStack().pop()).apply(stackConfig);
        }

        return stackConfig;
    }

    private NFAWrapper createAtomicNfa(char c) {
        NFAState endState = new NFAState();
        NFAState startState = new NFAState();
        startState.addTransition(c, endState);

        return new NFAWrapper(startState, endState);
    }

    private StackConfig processConcat(StackConfig config) {
        StackConfig newConfig = new StackConfig(config);
        NFAWrapper nfa2 = newConfig.popNfa();
        NFAWrapper nfa1 = newConfig.popNfa();
        newConfig.pushNfa(concat(nfa1, nfa2));
        return newConfig;
    }

    private NFAWrapper concat(NFAWrapper nfa1, NFAWrapper nfa2) {
        nfa1.getFinish().setTransMap(nfa2.getStart().getAllTransitions());
        return new NFAWrapper(nfa1.getStart(), nfa2.getFinish());
    }

    private StackConfig processUnion(StackConfig config) {
        StackConfig newConfig = new StackConfig(config);
        NFAWrapper nfa2 = newConfig.popNfa();
        NFAWrapper nfa1;
        if (!newConfig.getOperatorStack().empty() && newConfig.isNextOp('.')) {
            Stack<NFAWrapper> concatStack = new Stack<>();
            concatStack.push(newConfig.popNfa());

            while (newConfig.isNextOp('.')) {
                concatStack.push(newConfig.popNfa());
                newConfig.popOp();
            }

            nfa1 = concat(concatStack.pop(), concatStack.pop());
            while (!concatStack.empty()) {
                concat(nfa1, concatStack.pop());
            }
        } else {
            nfa1 = newConfig.popNfa();
        }

        newConfig.pushNfa(union(nfa1, nfa2));
        return newConfig;
    }

    private NFAWrapper union(NFAWrapper nfa1, NFAWrapper nfa2) {
        NFAState newStart = new NFAState();
        NFAState newFinish = new NFAState();
        newStart.addTransitions(null, Sets.newHashSet(nfa1.getStart(), nfa2.getStart()));
        nfa1.getFinish().addTransition(null, newFinish);
        nfa2.getFinish().addTransition(null, newFinish);
        return new NFAWrapper(newStart, newFinish);
    }

    private NFAWrapper kleeneStar(NFAWrapper nfa) {
        NFAWrapper newNFA = kleenePlus(nfa);
        newNFA.getStart().addTransition(null, newNFA.getFinish());
        return newNFA;
    }

    private NFAWrapper kleenePlus(NFAWrapper nfa) {
        NFAState newStart = new NFAState();
        NFAState newFinish = new NFAState();
        nfa.getFinish().addTransition(null, nfa.getStart());
        newStart.addTransition(null, nfa.getStart());
        nfa.getFinish().addTransition(null, newFinish);
        return new NFAWrapper(newStart, newFinish);
    }

    private NFAWrapper optional(NFAWrapper nfa) {
        nfa.getStart().addTransition(null, nfa.getFinish());
        return nfa;
    }

}
