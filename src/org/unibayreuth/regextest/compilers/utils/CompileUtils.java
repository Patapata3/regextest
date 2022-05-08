package org.unibayreuth.regextest.compilers.utils;

public class CompileUtils {
    public static Counter parseCounter(String counterString) {
        String[] counterBorders = counterString.split(",");
        if (counterBorders.length < 1 || counterBorders.length > 2) {
            throw new IllegalArgumentException("Invalid regex: too many or to few counter parameters");
        }

        int minCounter, maxCounter;
        try {
            minCounter = Integer.parseInt(counterBorders[0].trim());
            maxCounter = counterBorders.length == 2 ? Integer.parseInt(counterBorders[1].trim()) : minCounter;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid regex: incorrect format of the counter", e);
        }

        if (maxCounter < minCounter) {
            throw new IllegalArgumentException("Invalid regex: max counter value exceeds min counter value");
        }
        if (maxCounter == 0) {
            throw new IllegalArgumentException("Invalid regex: max counter value must be bigger than zero");
        }

        return new Counter(minCounter, maxCounter);
    }
}
