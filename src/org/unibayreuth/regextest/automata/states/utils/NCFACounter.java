package org.unibayreuth.regextest.automata.states.utils;

import java.util.Objects;

public class NCFACounter {
    private String name;
    private int min;
    private int max;

    public NCFACounter(String name, int min, int max) {
        this.name = name;
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NCFACounter that = (NCFACounter) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public String getName() {
        return name;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}
