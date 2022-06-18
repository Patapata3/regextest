package org.unibayreuth.regextest.compilers.utils.ncfa;

import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RegexElement {
    private RegexElementType type = RegexElementType.DEFAULT;
    private String regex = "";
    private List<RegexElement> children = new ArrayList<>();
    private List<List<RegexElement>> alternatives = new ArrayList<>();
    private Integer maxCounter;
    private Integer minCounter;
    private boolean ready = false;
    private boolean isAlternative = false;
    private boolean childrenNullable = false;
    private boolean alternativesNullable = false;

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
            if (alternatives.isEmpty()) {
                alternatives.add(new ArrayList<>());
            }
            List<RegexElement> lastAlternative = alternatives.get(alternatives.size() - 1);
            alternativesNullable = (alternativesNullable || lastAlternative.isEmpty()) && childElement.isNullable();
            lastAlternative.add(childElement);
        } else {
            childrenNullable = (childrenNullable || children.isEmpty()) && childElement.isNullable();
            children.add(childElement);
        }
        regex += childElement.getRegex();
    }

    public void newAlternative() {
        alternatives.add(new ArrayList<>());
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

    public List<List<RegexElement>> getAlternatives() {
        return alternatives;
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
                || childrenNullable || alternativesNullable;
    }

    public void addSymbol(char c) {
        regex += c;
    }


}
