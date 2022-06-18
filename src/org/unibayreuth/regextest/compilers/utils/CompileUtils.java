package org.unibayreuth.regextest.compilers.utils;

import org.unibayreuth.regextest.compilers.utils.ncfa.RegexElement;
import org.unibayreuth.regextest.compilers.utils.ncfa.RegexElementType;
import org.unibayreuth.regextest.compilers.utils.ncfa.RegexTree;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class CompileUtils {
    private static final Map<Character, RegexElementType> postfixOpMap = Map.of(
            '*', RegexElementType.STAR,
            '+', RegexElementType.PLUS,
            '?', RegexElementType.OPTIONAL);

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

    public static <T> Set<T> setUnion(Set<T> first, Set<T> second) {
        Set<T> result = new HashSet<>(first);
        result.addAll(second);
        return result;
    }

    public static RegexTree parseRegexTree(String regex) {
        RegexElement root = new RegexElement();
        Stack<RegexElement> elementStack = new Stack<>();
        Set<Character> alphabet = new HashSet<>();
        elementStack.push(root);
        boolean isEscape = false;
        boolean isCounter = false;
        int bracketsCount = 0;
        StringBuilder counterString = new StringBuilder();

        for (char c : regex.toCharArray()) {
            if (c == '\\' && !isEscape) {
                isEscape = true;
                continue;
            }

            if (!isEscape && !isCounter) {
                switch (c) {
                    case '|':
                        if (elementStack.peek().isReady()) {
                            RegexElement newChild = elementStack.pop();
                            elementStack.peek().addChild(newChild);
                        }
                        elementStack.peek().setAlternative(true);
                        elementStack.peek().newAlternative();
                        elementStack.peek().addSymbol(c);
                        break;
                    case '(':
                        if (elementStack.peek().isReady()) {
                            RegexElement newChild = elementStack.pop();
                            elementStack.peek().addChild(newChild);
                        }

                        RegexElement child = new RegexElement();
                        elementStack.push(child);
                        elementStack.peek().addSymbol(c);
                        bracketsCount++;
                        break;
                    case ')':
                        if (bracketsCount == 0) {
                            throw new IllegalArgumentException("Invalid regex: too many closing brackets");
                        }
                        RegexElement newChild = elementStack.pop();
                        elementStack.peek().addChild(newChild);
                        elementStack.peek().setReady(true);
                        elementStack.peek().addSymbol(c);
                        bracketsCount--;
                        break;
                    case '*':
                    case '+':
                    case '?':
                        elementStack.push(wrapPostfixOp(postfixOpMap.get(c), elementStack.pop()));
                        elementStack.peek().addSymbol(c);
                        break;
                    case '{':
                        isCounter = true;
                        break;
                    default:
                        handleSingleton(elementStack, c);
                        alphabet.add(c);
                }
            } else if (isCounter && c == '}') {
                handleCounter(elementStack, counterString.toString());
                counterString.setLength(0);
                isCounter = false;
            } else if (isCounter) {
                counterString.append(c);
            } else {
                handleSingleton(elementStack, c);
                isEscape = false;
                alphabet.add(c);
            }
        }

        if (bracketsCount != 0) {
            throw new IllegalArgumentException("Invalid regex: too many opening brackets");
        }
        RegexElement lastChild = elementStack.pop();
        root.addChild(lastChild);
        return new RegexTree(root, alphabet);
    }

    private static void handleSingleton(Stack<RegexElement> elementStack, char c) {
        if (elementStack.peek().isReady()) {
            RegexElement newChild = elementStack.pop();
            elementStack.peek().addChild(newChild);
        }
        elementStack.push(RegexElement.singleton(c));
    }

    private static RegexElement wrapPostfixOp(RegexElementType opType, RegexElement elementToWrap) {
        RegexElement wrapperElement = new RegexElement();
        wrapperElement.addChild(elementToWrap);
        wrapperElement.setType(opType);
        wrapperElement.setReady(true);
        return wrapperElement;
    }

    private static void handleCounter(Stack<RegexElement> elementStack, String counterString) {
        if (elementStack.peek().getType() == RegexElementType.STAR) {
            return;
        }

        org.unibayreuth.regextest.compilers.utils.Counter counter = CompileUtils.parseCounter(counterString);
        if (elementStack.peek().getType() == RegexElementType.OPTIONAL) {
            elementStack.peek().setType(RegexElementType.COUNTER);
            elementStack.peek().setMinCounter(0);
            elementStack.peek().setMaxCounter(counter.getMax());

            String regex = elementStack.peek().getRegex();
            regex = regex.substring(0, regex.length() - 1) + String.format("{%d;%d}", 0, counter.getMax());
            elementStack.peek().setRegex(regex);
            return;
        }
        elementStack.push(wrapCounter(elementStack.pop(), counter.getMin(), counter.getMax()));
    }

    private static RegexElement wrapCounter(RegexElement elementToWrap, int min, int max) {
        RegexElement counterElement = wrapPostfixOp(RegexElementType.COUNTER, elementToWrap);
        if (elementToWrap.isNullable()) {
            min = 0;
        }
        counterElement.setMinCounter(min);
        counterElement.setMaxCounter(max);
        counterElement.setRegex(counterElement.getRegex() + String.format("{%d;%d}", min, max));
        return counterElement;
    }
}
