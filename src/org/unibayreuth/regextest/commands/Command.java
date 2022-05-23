package org.unibayreuth.regextest.commands;

public interface Command<T> {
    T execute(String[] args);
}
