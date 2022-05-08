package org.unibayreuth.regextest.compilers.utils.nfa;

import java.util.Stack;

public class StackConfig {
    private Stack<NFAWrapper> subNfaStack;
    private Stack<Character> operatorStack;

    public StackConfig() {
        subNfaStack = new Stack<>();
        operatorStack = new Stack<>();
    }

    public StackConfig(Stack<NFAWrapper> subNfaStack, Stack<Character> operatorStack) {
        this.subNfaStack = subNfaStack;
        this.operatorStack = operatorStack;
    }

    public StackConfig(StackConfig config) {
        subNfaStack = config.getSubNfaStack();
        operatorStack = config.getOperatorStack();
    }

    public void pushNfa(NFAWrapper subNfa) {
        subNfaStack.push(subNfa);
    }

    public void pushOp(char c) {
        operatorStack.push(c);
    }

    public NFAWrapper popNfa() {
        return subNfaStack.pop();
    }

    public char popOp() {
        return operatorStack.pop();
    }

    public boolean isNextOp(char c) {
        return !operatorStack.empty() && operatorStack.peek() == c;
    }

    public Stack<NFAWrapper> getSubNfaStack() {
        return subNfaStack;
    }

    public Stack<Character> getOperatorStack() {
        return operatorStack;
    }
}
