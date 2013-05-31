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
        private String _actualName;
        private LinkedList<String> _actualValues;

        public Scope(final String _actualName, final LinkedList<String> _actualValues) {
            this._actualName = _actualName;
            this._actualValues = _actualValues;
        }
    }

    private final Deque<Scope> _ctx = new ArrayDeque<>();
    private final Map<String, List<String>> queryMap;
    private String _actualValue;

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
            _actualValue = values.get(0);
        }
        _ctx.push(new Scope(key, new LinkedList<>(values)));
        queryMap.remove(key);

        return this;
    }

    public QueryParser end() {
        if (_ctx.isEmpty()) {
            throw new IllegalStateException("Invalid call of end without calling begin.");
        }
        _ctx.pop();
        return this;
    }

    public Set<String> names() {
        return queryMap.keySet();
    }

    public String name() {
        _checkContextNotEmpty();
        return _ctx.peek()._actualName;
    }

    public String value() {
        _checkContextNotEmpty();
        return _actualValue;
    }

    public int intValue() {
        _checkContextNotEmpty();
        return Integer.valueOf(_actualValue);
    }

    public double doubleValue() {
        _checkContextNotEmpty();
        return Double.valueOf(_actualValue);
    }

    public long longValue() {
        _checkContextNotEmpty();
        return Long.valueOf(_actualValue);
    }

    public boolean booleanValue() {
        _checkContextNotEmpty();
        return Boolean.valueOf(_actualValue);
    }

    public boolean hasNext() {
        if (!_ctx.isEmpty()) {
            return !_ctx.peek()._actualValues.isEmpty();
        } else {
            return !queryMap.isEmpty();
        }
    }

    public QueryParser next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        if (!_ctx.isEmpty()) {
            _actualValue = _ctx.peek()._actualValues.pop();
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
        if (_ctx.isEmpty()) {
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
