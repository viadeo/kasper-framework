package com.viadeo.kasper.context;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;

public final class Tags {

    private static final char TAGS_SEPARATOR = ',';
    private static final Splitter SPLITTER = Splitter.on(TAGS_SEPARATOR).omitEmptyStrings();
    private static final Joiner JOINER = Joiner.on(TAGS_SEPARATOR).skipNulls();

    public static Set<String> valueOf(String string) {
        checkNotNull(string);
        return ImmutableSet.copyOf(SPLITTER.split(string));
    }

    public static String toString(Set<String> tags) {
        checkNotNull(tags);
        return JOINER.join(tags);
    }

    private Tags() {
    }

}
