// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition.query;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;

import java.util.*;

public class QueryParser implements Iterable<QueryParser> {

    private static class Scope {
        private String actualName;
        private Deque<String> actualValues;

        public Scope(final String actualName, final Deque<String> actualValues) {
            this.actualName = actualName;
            this.actualValues = actualValues;
        }
    }

    private final Deque<Scope> ctx = new ArrayDeque<Scope>();
    private final Multimap<String, String> queryMap;
    private String actualValue;

    // ------------------------------------------------------------------------

    public QueryParser() {
        this.queryMap = LinkedHashMultimap.create();
    }

    public QueryParser(final SetMultimap<String, String> queryMap) {
        this.queryMap = LinkedHashMultimap.create(queryMap);
    }

    // ------------------------------------------------------------------------

    public boolean exists(final String key) {
        return queryMap.containsKey(key);
    }

    public QueryParser begin(final String key) {
        final Collection<String> values = queryMap.get(key);

        if (values == null) {
            throw new NoSuchElementException("No value found for key[" + key + "].");
        }

        if (1 == values.size()) {
            actualValue = values.iterator().next();
        }

        ctx.push(new Scope(key, new LinkedList<String>(values)));
        queryMap.removeAll(key);

        return this;
    }

    public QueryParser end() {
        if (ctx.isEmpty()) {
            throw new IllegalStateException("Invalid call of end without calling begin.");
        }
        ctx.pop();
        return this;
    }

    public Set<String> names() {
        return  ImmutableSet.copyOf(queryMap.keySet());
    }

    public String name() {
        checkContextNotEmpty();
        return ctx.peek().actualName;
    }

    public String value() {
        checkContextNotEmpty();
        return actualValue;
    }

    public int intValue() {
        checkContextNotEmpty();
        return Integer.valueOf(actualValue);
    }

    public double doubleValue() {
        checkContextNotEmpty();
        return Double.valueOf(actualValue);
    }

    public long longValue() {
        checkContextNotEmpty();
        return Long.valueOf(actualValue);
    }

    public boolean booleanValue() {
        checkContextNotEmpty();
        return Boolean.valueOf(actualValue);
    }

    public boolean hasNext() {
        if (!ctx.isEmpty()) {
            return !ctx.peek().actualValues.isEmpty();
        } else {
            return !queryMap.isEmpty();
        }
    }

    public QueryParser next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        if (!ctx.isEmpty()) {
            actualValue = ctx.peek().actualValues.pop();
        }
        /* // -- else { --
         * no need to handle here iteration over key/values, as the access
         * order does not matter
         * // -- } --
         */

        return this;
    }

    private void checkContextNotEmpty() {
        if (ctx.isEmpty()) {
            throw new IllegalStateException("Call begin before trying to get a value.");
        }
    }

    @Override
    public Iterator<QueryParser> iterator() {
        return new Iterator<QueryParser>() {
            @Override
            public boolean hasNext() {
                return QueryParser.this.hasNext();
            }

            @Override
            public QueryParser next() {
                return QueryParser.this.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

}
