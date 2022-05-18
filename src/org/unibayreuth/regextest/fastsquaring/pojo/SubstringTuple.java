package org.unibayreuth.regextest.fastsquaring.pojo;

import java.util.Objects;

public class SubstringTuple {
    private final int start;
    private final int finish;

    public SubstringTuple(int start, int finish) {
        this.start = start;
        this.finish = finish;
    }

    public int getStart() {
        return start;
    }

    public int getFinish() {
        return finish;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubstringTuple that = (SubstringTuple) o;
        return start == that.start &&
                finish == that.finish;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, finish);
    }
}