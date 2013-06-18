// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.shard.util;

import com.google.common.base.Joiner;

import java.util.Collection;

public class Sql {

    public static String in(final Object... items) {
        return Joiner.on(",")
                .skipNulls()
                .join(items)
                .replaceAll(" ","");
    }

    public static String in(final Collection<?> items) {
        return Joiner.on(",")
                .skipNulls()
                .join(items)
                .replaceAll(" ","");
    }
}
