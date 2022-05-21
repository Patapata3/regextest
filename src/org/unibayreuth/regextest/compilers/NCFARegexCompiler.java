package org.unibayreuth.regextest.compilers;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.unibayreuth.regextest.automata.nondeterministic.NCFAutomaton;
import org.unibayreuth.regextest.automata.states.NCFAState;
import org.unibayreuth.regextest.automata.states.utils.ncfa.CFACounter;
import org.unibayreuth.regextest.automata.states.utils.ncfa.NCFAOpType;
import org.unibayreuth.regextest.automata.states.utils.ncfa.NCFAOperation;
import org.unibayreuth.regextest.automata.states.utils.ncfa.NCFATransition;
import org.unibayreuth.regextest.compilers.utils.CollectionUtils;
import org.unibayreuth.regextest.compilers.utils.CompileUtils;
import org.unibayreuth.regextest.compilers.utils.ncfa.PartialDerivation;
import org.unibayreuth.regextest.compilers.utils.ncfa.RegexElement;
import org.unibayreuth.regextest.compilers.utils.ncfa.RegexElementType;
import org.unibayreuth.regextest.compilers.utils.ncfa.RegexTree;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

public class NCFARegexCompiler implements RegexCompiler<NCFAutomaton> {
    @Override
    public NCFAutomaton compile(String regex) {
        Preconditions.checkNotNull(regex, "Regex cannot be null!");
        Preconditions.checkArgument(!regex.isEmpty(), "Regex cannot be empty!");
        return compileAutomaton(CompileUtils.parseRegexTree(regex));
    }

    private NCFAutomaton compileAutomaton(RegexTree tree) {
        RegexElement root = tree.getRoot();
        Map<String, NCFAState> stateMap = new HashMap<>();
        NCFAState startState = new NCFAState(root.getRegex());
        stateMap.put(root.getRegex(), startState);
        startState.setAcceptConditions(calculateAcceptConditions(root.getChildren()));
        Map<String, CFACounter> counterMap = new HashMap<>();
        startState.setTransMap(calculateTransitions(Collections.singletonList(root), tree.getAlphabet(), stateMap, new HashMap<>(), counterMap));
        return new NCFAutomaton(startState, new HashSet<>(counterMap.values()));
    }

    private Set<NCFAOperation> calculateAcceptConditions(List<RegexElement> elementSequence) {
        Set<NCFAOperation> acceptConditions = new HashSet<>();
        for (RegexElement element : elementSequence) {
            if (element.isNullable()) {
                continue;
            }
            if (element.getType() != RegexElementType.COUNTER) {
                return null;
            }
            NCFAOperation newCondition = new NCFAOperation(NCFAOpType.EXIT,
                    new CFACounter(element.getRegex(), element.getMinCounter(), element.getMaxCounter()));
            if (!acceptConditions.add(newCondition)) {
                return null;
            }
        }
        return acceptConditions;
    }

    private Map<Character, Set<NCFATransition>> calculateTransitions(List<RegexElement> elementSequence, Set<Character> alphabet,
                                                                     Map<String, NCFAState> stateMap,
                                                                     Map<String, Set<PartialDerivation>> derivationCache,
                                                                     Map<String, CFACounter> counterMap) {
        Map<Character, Set<NCFATransition>> transMap = new HashMap<>();
        for (char c : alphabet) {
            Set<PartialDerivation> derivations = calculateDerivation(c, elementSequence, derivationCache, counterMap);
            if (!derivations.isEmpty()) {
                transMap.put(c, new HashSet<>());
            }
            for (PartialDerivation derivation : derivations) {
                String stateName = sequenceToString(derivation.getRemainder());
                if (stateMap.containsKey(stateName)) {
                    transMap.get(c).add(new NCFATransition(derivation.getOperations(), stateMap.get(stateName)));
                    continue;
                }
                NCFAState newState = new NCFAState(stateName);
                stateMap.put(stateName, newState);
                newState.setAcceptConditions(calculateAcceptConditions(derivation.getRemainder()));
                newState.setTransMap(calculateTransitions(derivation.getRemainder(), alphabet, stateMap, derivationCache, counterMap));
                transMap.get(c).add(new NCFATransition(derivation.getOperations(), newState));
            }
        }
        return transMap;
    }

    private Set<PartialDerivation> calculateDerivation(char c, List<RegexElement> elementSequence, Map<String, Set<PartialDerivation>> derivationCache,
                                                       Map<String, CFACounter> counterMap) {
        if (elementSequence == null || elementSequence.isEmpty()) {
            return new HashSet<>();
        }
        String cacheKey = String.format("%s - %s", c, sequenceToString(elementSequence));
        if (derivationCache.containsKey(cacheKey)) {
            return derivationCache.get(cacheKey);
        }

        RegexElement leadingElement = elementSequence.get(0);
        List<RegexElement> sequenceTail = elementSequence.size() > 1 ? elementSequence.subList(1, elementSequence.size()) : new ArrayList<>();
        Set<PartialDerivation> derivations;
        switch (leadingElement.getType()) {
            case SINGLETON:
                derivations = String.valueOf(c).equals(leadingElement.getRegex()) ?
                        Collections.singleton(new PartialDerivation(null, sequenceTail)) : new HashSet<>();
                break;
            case STAR:
                Set<PartialDerivation> composition = composeDerivations(calculateDerivation(c, leadingElement.getChildren(), derivationCache, counterMap),
                        Collections.singleton(new PartialDerivation(null, elementSequence)));
                Set<PartialDerivation> remaining = calculateDerivation(c, sequenceTail, derivationCache, counterMap);
                derivations = CompileUtils.setUnion(composition, remaining);
                break;
            case PLUS:
                RegexElement starElement = new RegexElement();
                starElement.setType(RegexElementType.STAR);
                starElement.setChildren(leadingElement.getChildren());
                starElement.setRegex(leadingElement.getRegex().substring(0, leadingElement.getRegex().length() - 1) + "*");

                List<RegexElement> plusSequence = new ArrayList<>(leadingElement.getChildren());
                plusSequence.add(starElement);
                plusSequence.addAll(sequenceTail);
                derivations = calculateDerivation(c, plusSequence, derivationCache, counterMap);
                break;
            case COUNTER:
                CFACounter counter = ofNullable(counterMap.get(leadingElement.getRegex()))
                        .orElse(new CFACounter(leadingElement.getRegex(), leadingElement.getMinCounter(), leadingElement.getMaxCounter()));
                counterMap.put(leadingElement.getRegex(), counter);

                Set<PartialDerivation> incrDerivations = composeDerivations(calculateDerivation(c, leadingElement.getChildren(), derivationCache, counterMap),
                        Collections.singleton(new PartialDerivation(Collections.singleton(new NCFAOperation(NCFAOpType.INCREMENT, counter)), elementSequence)));
                Set<PartialDerivation> exitDerivations = composeDerivations(Collections.singleton(new PartialDerivation(Collections.singleton(new NCFAOperation(NCFAOpType.EXIT, counter)), new ArrayList<>())),
                        calculateDerivation(c, sequenceTail, derivationCache, counterMap));

                derivations = CompileUtils.setUnion(incrDerivations, exitDerivations);
                break;
            default:
                List<RegexElement> childSequence = new ArrayList<>(leadingElement.getChildren());
                childSequence.addAll(sequenceTail);
                derivations = calculateDerivation(c, childSequence, derivationCache, counterMap);
                if (leadingElement.isAlternative()) {
                    List<RegexElement> alternativeSequence = new ArrayList<>(leadingElement.getAlternatives());
                    alternativeSequence.addAll(sequenceTail);
                    derivations = CompileUtils.setUnion(derivations, calculateDerivation(c, alternativeSequence, derivationCache, counterMap));
                }
                if (leadingElement.getType() == RegexElementType.OPTIONAL) {
                    derivations = CompileUtils.setUnion(derivations, calculateDerivation(c, sequenceTail, derivationCache, counterMap));
                }
        }

        derivationCache.put(cacheKey, derivations);
        return derivations;
    }

    private Set<PartialDerivation> composeDerivations(Set<PartialDerivation> first, Set<PartialDerivation> second) {
        if (CollectionUtils.isNullOrEmpty(first) || CollectionUtils.isNullOrEmpty(second)) {
            return new HashSet<>();
        }

        Set<PartialDerivation> composedDerivations = new HashSet<>();
        for (PartialDerivation firstDerivation : first) {
            for (PartialDerivation secondDerivation : second) {
                PartialDerivation composed = firstDerivation.compose(secondDerivation);
                if (composed != null) {
                    composedDerivations.add(composed);
                }
            }
        }
        return composedDerivations;
    }

    private String sequenceToString(List<RegexElement> elementSequence) {
        return elementSequence.stream()
                .map(RegexElement::getRegex)
                .collect(Collectors.joining());
    }
}
