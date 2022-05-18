package org.unibayreuth.regextest.fastsquaring;

import com.google.common.collect.Sets;
import org.unibayreuth.regextest.compilers.utils.CompileUtils;
import org.unibayreuth.regextest.compilers.utils.ncfa.RegexElement;
import org.unibayreuth.regextest.compilers.utils.ncfa.RegexElementType;
import org.unibayreuth.regextest.compilers.utils.ncfa.RegexTree;
import org.unibayreuth.regextest.fastsquaring.pojo.SubstringTuple;

import java.util.*;

public class RelationMatcher {
    private final Set<RegexElementType> kleeneElements = Sets.newHashSet(RegexElementType.STAR, RegexElementType.STAR);


    public boolean match(String regex, String input) {
        RegexTree parseTree = CompileUtils.parseRegexTree(regex);
        if (input.isEmpty() && parseTree.getRoot().isNullable()) {
            return true;
        }

        Map<String, Set<SubstringTuple>> relationCache = getSingletonRelations(parseTree.getAlphabet(), input);
        if (relationCache == null) {
            return false;
        }

        Set<SubstringTuple> rootRelation = constructRelation(input, parseTree.getRoot(), relationCache);
        return rootRelation.contains(new SubstringTuple(0, input.length() - 1));
    }

    private Map<String, Set<SubstringTuple>> getSingletonRelations(Set<Character> alphabet, String input) {
        Map<String, Set<SubstringTuple>> singletonRelations = new HashMap<>();
        Set<Character> alphabetRemainder = new HashSet<>(alphabet);

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (alphabet.contains(c)) {
                singletonRelations.putIfAbsent(String.valueOf(c), new HashSet<>());
                singletonRelations.get(String.valueOf(c)).add(new SubstringTuple(i, i));
                alphabetRemainder.remove(c);
            }
            else return null;
        }
        alphabetRemainder.forEach(symbol -> singletonRelations.put(String.valueOf(symbol), new HashSet<>()));

        return singletonRelations;
    }

    private Set<SubstringTuple> constructRelation(String input, RegexElement element, Map<String, Set<SubstringTuple>> relationCache) {
        String elementRegex = element.getRegex();
        if (relationCache.containsKey(elementRegex)) {
            return relationCache.get(elementRegex);
        }

        if (element.getType() == RegexElementType.OPTIONAL) {
            Set<SubstringTuple> resultRelation = constructRelation(input, element.getChildren().get(0), relationCache);
            relationCache.put(elementRegex, resultRelation);
            return resultRelation;
        }

        if (kleeneElements.contains(element.getType())) {
            Set<SubstringTuple> childRelation = constructRelation(input, element.getChildren().get(0), relationCache);
            if (childRelation.isEmpty()) {
                return childRelation;
            }

            Set<SubstringTuple> resultRelation = cycleConcat(childRelation, 2, input.length(), false);
            relationCache.put(elementRegex, resultRelation);
            return resultRelation;
        }

        if (element.getType() == RegexElementType.COUNTER) {
            Set<SubstringTuple> childRelation = constructRelation(input, element.getChildren().get(0), relationCache);
            if (childRelation.isEmpty()) {
                return childRelation;
            }

            Set<SubstringTuple> resultRelation;
            if (element.isNullable()) {
                resultRelation = cycleConcat(childRelation, 2, Math.min(input.length(), element.getMaxCounter()), false);
            } else {
                resultRelation = cycleConcat(childRelation, 2, Math.min(input.length(), element.getMinCounter()), true);
                resultRelation = cycleConcat(resultRelation, childRelation, element.getMinCounter() + 1, Math.min(input.length(), element.getMaxCounter()));
            }
            relationCache.put(elementRegex, resultRelation);
            return resultRelation;
        }

        Set<SubstringTuple> resultRelation = concatChildren(input, element.getChildren(), relationCache);
        if (element.isAlternative()) {
            Set<SubstringTuple> alternativeRelation = concatChildren(input, element.getAlternatives(), relationCache);
            resultRelation.addAll(alternativeRelation);
        }
        relationCache.put(elementRegex, resultRelation);

        return resultRelation;
    }

    private Set<SubstringTuple> cycleConcat(Set<SubstringTuple> toConcat, int fromIdx, int toIdx, boolean elementRequired) {
        return elementRequired ? fastSquare(toConcat, fromIdx, toIdx) :
                cycleConcat(toConcat, toConcat, fromIdx, toIdx);
    }

    private Set<SubstringTuple> cycleConcat(Set<SubstringTuple> baseRelation, Set<SubstringTuple> toConcat, int fromIdx, int toIdx) {
        Set<SubstringTuple> concatResult = new HashSet<>(baseRelation);
        for (int i = fromIdx; i <= toIdx; i++) {
            Set<SubstringTuple> joinRelation = join(concatResult, toConcat);
            concatResult = Sets.union(concatResult, joinRelation);
        }

        return concatResult;
    }

    private Set<SubstringTuple> fastSquare(Set<SubstringTuple> toConcat, int fromIdx, int toIdx) {
        if (fromIdx < toIdx) {
            return toConcat;
        }

        Map<Integer, Set<SubstringTuple>> calculatedPowers = new HashMap<>();
        calculatedPowers.put(1, toConcat);
        int power = fromIdx - toIdx + 2;

        int calculatedPower;
        for (calculatedPower = 2; calculatedPower <= power; calculatedPower *= 2) {
            toConcat = join(toConcat, toConcat);
            calculatedPowers.putIfAbsent(calculatedPower, toConcat);
        }
        power -= calculatedPower / 2;

        while(power > 0) {
            int square = nearestSquare(power);
            toConcat = join(toConcat, calculatedPowers.get(square));
            power -= square;
        }

        return toConcat;
    }

    private Set<SubstringTuple> join(Set<SubstringTuple> first, Set<SubstringTuple> second) {
        if (first.isEmpty() || second.isEmpty()) {
            return new HashSet<>();
        }

        Set<SubstringTuple> joinRelation = new HashSet<>();
        for (SubstringTuple firstTuple : first) {
            for (SubstringTuple secondTuple : second) {
                if (secondTuple.getStart() - firstTuple.getFinish() == 1) {
                    joinRelation.add(new SubstringTuple(firstTuple.getStart(), secondTuple.getFinish()));
                }
            }
        }

        return joinRelation;
    }

    private int nearestSquare(int num) {
        int result = 1;
        for (int shift = 16; shift > 0; shift = shift / 2) {
            if (num >= result << shift) {
                result <<= shift;
            }
        }
        return result;
    }

    private Set<SubstringTuple> concatChildren(String input, List<RegexElement> children, Map<String, Set<SubstringTuple>> relationCache) {
        if (children.isEmpty()) {
            return new HashSet<>();
        }

        boolean concatNullable = true;
        Set<SubstringTuple> concatRelation = new HashSet<>();
        for (RegexElement child : children) {
            Set<SubstringTuple> prevConcat = concatRelation;
            Set<SubstringTuple> childRelation = constructRelation(input, child, relationCache);
            concatRelation = join(concatRelation, childRelation);

            if (concatNullable) {
                concatRelation.addAll(childRelation);
            }
            if (child.isNullable()) {
                concatRelation.addAll(prevConcat);
            }
            concatNullable = concatNullable && child.isNullable();
        }

        return concatRelation;
    }
}
