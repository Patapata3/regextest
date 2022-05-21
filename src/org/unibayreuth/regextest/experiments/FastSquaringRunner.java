package org.unibayreuth.regextest.experiments;

import org.unibayreuth.regextest.compilers.RegexCompiler;
import org.unibayreuth.regextest.fastsquaring.RelationMatcher;

public class FastSquaringRunner extends ExperimentRunner {
    public FastSquaringRunner(String regex, String input) {
        super(regex, input);
    }

    @Override
    public double run(String[] operations, int times) {
        RelationMatcher matcher = new RelationMatcher();
        return measure(times, () -> matcher.match(regex, input));
    }
}
