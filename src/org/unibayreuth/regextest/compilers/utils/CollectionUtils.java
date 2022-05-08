package org.unibayreuth.regextest.compilers.utils;

import java.util.Collection;

public class CollectionUtils {
    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
