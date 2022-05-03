package org.unibayreuth.regextest.compilers.utils;

import com.google.common.collect.Sets;
import org.unibayreuth.regextest.automata.NFAutomaton;
import org.unibayreuth.regextest.automata.states.NFAState;

public class NFAWrapper {
    private NFAutomaton nfa;
    private NFAState start;
    private NFAState finish;

    public NFAWrapper(NFAState start, NFAState finish) {
        this.start = start;
        this.finish = finish;
        nfa = new NFAutomaton(Sets.newHashSet(start));
    }

    public NFAutomaton getNfa() {
        return nfa;
    }

    public NFAState getStart() {
        return start;
    }

    public NFAState getFinish() {
        return finish;
    }
}