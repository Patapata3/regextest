package org.unibayreuth.regextest.experiments;

import org.unibayreuth.regextest.compilers.RegexCompiler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class ExperimentRunner {
    protected RegexCompiler<?> compiler;
    protected String regex;
    protected String input;

    public ExperimentRunner(String regex, String input) {
        this.regex = regex;
        this.input = input;
    }

    public ExperimentRunner(RegexCompiler<?> compiler, String regex, String input) {
        this.compiler = compiler;
        this.regex = regex;
        this.input = input;
    }

    public abstract double run(String[] operations, int times);

    protected double measure(int times, Runnable procedure) {
        List<Double> measures = new ArrayList<>();
        for (int i = 0; i < 100 + times; i++) {
            long startTime = System.nanoTime();
            procedure.run();
            long finishTime = System.nanoTime();
            double resultMs = ((double) (finishTime - startTime)) / 1000000;
            System.out.printf("%sMeasure %d - %.5f%n", i <= 100 ? "Warm-up " : "", i, resultMs);
            if (i >= 100) {
                measures.add(resultMs);
            }
        }

        measures.sort(Double::compareTo);
        double sum = measures.subList(2, measures.size() - 2)
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        return sum / (measures.size() - 4);
    }
}
