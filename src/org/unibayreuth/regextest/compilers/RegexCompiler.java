package org.unibayreuth.regextest.compilers;

import org.unibayreuth.regextest.automata.Automaton;

public interface RegexCompiler<T extends Automaton> {
    String getType();
    T compile(String regex);
}
