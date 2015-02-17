// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Tags {

    private static final char TAGS_SEPARATOR = ',';
    private static final Splitter SPLITTER = Splitter.on(TAGS_SEPARATOR).omitEmptyStrings();
    private static final Joiner JOINER = Joiner.on(TAGS_SEPARATOR).skipNulls();

    // ------------------------------------------------------------------------

    private Tags() {
        /* Utility class */
    }

    // ------------------------------------------------------------------------

    public static Set<String> valueOf(final String string) {
        checkNotNull(string);
        return ImmutableSet.copyOf(SPLITTER.split(string));
    }

    public static String toString(final Set<String> tags) {
        checkNotNull(tags);
        return JOINER.join(tags);
    }

}
