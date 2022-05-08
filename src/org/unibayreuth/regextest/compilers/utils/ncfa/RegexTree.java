package org.unibayreuth.regextest.compilers.utils.ncfa;

import java.util.Set;

public class RegexTree {
    private RegexElement root;
    private Set<Character> alphabet;

    public RegexTree(RegexElement root, Set<Character> alphabet) {
        this.root = root;
        this.alphabet = alphabet;
    }

    public RegexElement getRoot() {
        return root;
    }

    public Set<Character> getAlphabet() {
        return alphabet;
    }
}
