package org.unibayreuth.regextest.automata.states.utils.csa;

import org.unibayreuth.regextest.automata.states.utils.ncfa.CFACounter;

public class CSAGuard {
    private final CFACounter counter;
    private final boolean exit;
    private final boolean increment;

    public CSAGuard(CFACounter counter, boolean exit, boolean increment) {
        this.counter = counter;
        this.exit = exit;
        this.increment = increment;
    }

    public CFACounter getCounter() {
        return counter;
    }

    public boolean isExit() {
        return exit;
    }

    public boolean isIncrement() {
        return increment;
    }
}
