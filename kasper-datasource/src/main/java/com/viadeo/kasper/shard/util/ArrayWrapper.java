// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.shard.util;

import java.util.Collection;

public class ArrayWrapper {

    private final Collection<?> items;

    // ------------------------------------------------------------------------

    public ArrayWrapper(final Collection<?> c) {
        items = c;
    }

    // ------------------------------------------------------------------------

    public String getList() {
        final StringBuilder s = new StringBuilder();

        if (null != items) {
            for (final Object id : items) {
                s.append(id.toString()).append(",");
            }
            if (s.length() > 0) {
                s.deleteCharAt(s.length()-1);
            }
        }

        return s.toString();
    }

}
