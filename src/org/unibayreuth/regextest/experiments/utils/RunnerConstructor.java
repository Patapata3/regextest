package org.unibayreuth.regextest.experiments.utils;

import org.unibayreuth.regextest.experiments.ExperimentRunner;

@FunctionalInterface
public interface RunnerConstructor {
    ExperimentRunner apply(String regex, String input);
}
