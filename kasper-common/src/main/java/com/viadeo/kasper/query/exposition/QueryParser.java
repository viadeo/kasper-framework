// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition;

import java.util.*;

public class QueryParser implements Iterable<QueryParser> {

    private class Scope {
        private String actualName;
        private LinkedList<String> actualValues;

        public Scope(final String actualName, final LinkedList<String> actualValues) {
            this.actualName = actualName;
            this.actualValues = actualValues;
        }
    }

    private final Deque<Scope> ctx = new ArrayDeque<>();
    private final Map<String, List<String>> queryMap;
    private String actualValue;

    // ------------------------------------------------------------------------

    public QueryParser(final Map<String, List<String>> queryMap) {
        this.queryMap = new HashMap<>(queryMap);
    }

    // ------------------------------------------------------------------------

    public boolean exists(final String key) {
        return queryMap.containsKey(key);
    }

    public QueryParser begin(final String key) {
        final List<String> values = queryMap.get(key);

        if (values == null) {
            throw new NoSuchElementException("No value found for key[" + key + "].");
        }
        if (values.size() == 1) {
            actualValue = values.get(0);
        }
        ctx.push(new Scope(key, new LinkedList<>(values)));
        queryMap.remove(key);

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
        return queryMap.keySet();
    }

    public String name() {
        _checkContextNotEmpty();
        return ctx.peek().actualName;
    }

    public String value() {
        _checkContextNotEmpty();
        return actualValue;
    }

    public int intValue() {
        _checkContextNotEmpty();
        return Integer.valueOf(actualValue);
    }

    public double doubleValue() {
        _checkContextNotEmpty();
        return Double.valueOf(actualValue);
    }

    public long longValue() {
        _checkContextNotEmpty();
        return Long.valueOf(actualValue);
    }

    public boolean booleanValue() {
        _checkContextNotEmpty();
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
        // else {
        /*
         * no need to handle here iteration over key/values, as the access
         * order does not matter
         */
        // }

        return this;
    }

    private void _checkContextNotEmpty() {
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
