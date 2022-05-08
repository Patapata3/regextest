package org.unibayreuth.regextest.compilers.utils.ncfa;

import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.List;

public class RegexElement {
    private RegexElementType type;
    private String regex = "";
    private List<RegexElement> children = new ArrayList<>();
    private List<RegexElement> alternatives = new ArrayList<>();
    private Integer maxCounter;
    private Integer minCounter;
    private boolean ready = false;
    private boolean isAlternative = false;

    public RegexElement() {
    }

    public static RegexElement singleton(char c) {
        RegexElement singletonElement = new RegexElement();
        singletonElement.setRegex(String.valueOf(c));
        singletonElement.setType(RegexElementType.SINGLETON);
        singletonElement.setReady(true);
        return singletonElement;
    }

    public void addChild(RegexElement childElement) {
        if (isAlternative) {
            alternatives.add(childElement);
        } else {
            children.add(childElement);
        }
        regex += childElement.getRegex();
    }

    public RegexElementType getType() {
        return type;
    }

    public void setType(RegexElementType type) {
        this.type = type;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public List<RegexElement> getChildren() {
        return children;
    }

    public void setChildren(List<RegexElement> children) {
        this.children = children;
    }

    public List<RegexElement> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(List<RegexElement> alternatives) {
        this.alternatives = alternatives;
    }

    public Integer getMaxCounter() {
        return maxCounter;
    }

    public void setMaxCounter(Integer maxCounter) {
        this.maxCounter = maxCounter;
    }

    public Integer getMinCounter() {
        return minCounter;
    }

    public void setMinCounter(Integer minCounter) {
        this.minCounter = minCounter;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isAlternative() {
        return isAlternative;
    }

    public void setAlternative(boolean alternative) {
        isAlternative = alternative;
    }

    public boolean isNullable() {
        return regex.isEmpty() || Sets.newHashSet(RegexElementType.OPTIONAL, RegexElementType.STAR).contains(type)
                || (type == RegexElementType.COUNTER && minCounter == 0)
                || children.stream().allMatch(RegexElement::isNullable)
                || alternatives.stream().allMatch(RegexElement::isNullable);
    }

    public void addSymbol(char c) {
        regex += c;
    }


}
