package org.unibayreuth.regextest.automata.states.utils.csa;

import java.util.ArrayDeque;
import java.util.Collection;

public class DistinctArrayDeque<T> extends ArrayDeque<T> {
    @Override
    public void addLast(T t) {
        if (isEmpty() || !getLast().equals(t)) {
            super.addLast(t);
        }
    }

    public DistinctArrayDeque() {
        super();
    }

    public DistinctArrayDeque(Collection<? extends T> c) {
        super(c);
    }
}
