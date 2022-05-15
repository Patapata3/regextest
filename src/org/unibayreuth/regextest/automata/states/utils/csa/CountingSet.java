package org.unibayreuth.regextest.automata.states.utils.csa;

import com.google.common.base.Preconditions;
import org.unibayreuth.regextest.automata.states.utils.ncfa.CFACounter;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Set;

public class CountingSet {
    private CFACounter counter;
    private Deque<Integer> counterQueue;
    private int offset;

    public CountingSet(CFACounter counter) {
        this.counter = counter;
        counterQueue = new DistinctArrayDeque<>();
        counterQueue.addLast(0);
        offset = 0;
    }

    public CountingSet(CountingSet originalSet) {
        counter = originalSet.counter;
        counterQueue = new DistinctArrayDeque<>(originalSet.counterQueue);
        offset = originalSet.offset;
    }

    public boolean canIncr() {
        return getMin() < counter.getMax();
    }

    public boolean canExit() {
        return getMax() >= counter.getMin();
    }

    public void apply(Set<CSAOpType> operations) {
        boolean keepThis = operations.contains(CSAOpType.INCR) || operations.contains(CSAOpType.NOOP);

        if (operations.contains(CSAOpType.INCR) && operations.contains(CSAOpType.NOOP)) {
            CountingSet originalSet = new CountingSet(this);
            incr();
            merge(originalSet);
        } else if(operations.contains(CSAOpType.INCR)) {
            incr();
        }

        if (operations.contains(CSAOpType.RST1)) {
            addOrReplace(1, keepThis);
            keepThis = true;
        }

        if (operations.contains(CSAOpType.RST)) {
            addOrReplace(0, keepThis);
        }
    }

    private void addOrReplace(int e, boolean keepThis) {
        if (keepThis) {
            add(e);
        } else {
            counterQueue = new DistinctArrayDeque<>(Collections.singleton(0));
            offset = e;
        }
    }

    private void incr() {
        offset++;
        if (getMax() > counter.getMax()) {
            counterQueue.removeFirst();
        }
    }

    private void add(int e) {
        Preconditions.checkArgument(e < 2 && e >= 0, "you can only add 0 or 1 to the counting set");
        Preconditions.checkArgument(e <= getMin(), "you can only add a new smallest element");

        counterQueue.addLast(offset - e);
    }

    private int getMin() {
        return offset - counterQueue.getLast();
    }

    private int getMax() {
        return offset - counterQueue.getFirst();
    }

    private void merge (CountingSet other) {
        Deque<Integer> merger = new DistinctArrayDeque<>();

        while (!counterQueue.isEmpty() || !other.counterQueue.isEmpty()) {
            if (counterQueue.isEmpty()) {
                merger.addLast(offset - other.popMax());
            } else if (other.counterQueue.isEmpty()) {
                merger.addLast(offset - popMax());
            } else {
                merger.addLast(offset - (getMax() > other.getMax() ? popMax() : other.popMax()));
            }
        }
        counterQueue = merger;
    }

    private int popMax() {
        int max = getMax();
        counterQueue.removeFirst();
        return max;
    }
}
