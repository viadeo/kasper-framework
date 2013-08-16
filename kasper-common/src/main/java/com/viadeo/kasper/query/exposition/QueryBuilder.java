// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Map.Entry;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Low level class allowing to build a query. Suppose you want to have the following parameters into a query : name =
 * foo nicknames = bar, foo bar, toto It would be done using the QueryBuilder like that:
 * <p/>
 * 
 * <pre>
 * QueryBuilder builder = new QueryBuilder();
 * 
 * builder.begin(&quot;nicknames&quot;)
 *          .add(&quot;bar&quot;, &quot;foo bar&quot;, &quot;toto&quot;)
 *        .end()
 *        .addSingle(&quot;name&quot;, &quot;foo&quot;);
 * </pre>
 * <p/>
 * The builder can then be used to create a query string and combine it with an URI pointing to some resource.
 * <p/>
 * 
 * <pre>
 * // will equal to http://www.google.com/somepath?bar=foo%20bar&amp;bar=too&amp;name=foo
 * URI resourceWithQueryString = builder.build(new URI(&quot;http://www.google.com/somepath&quot;));
 * </pre>
 * <p/>
 * It will also make sure that the query is consistent by forbidding you to overwrite a pair of key/value(s) that has
 * been written. The query builder also keeps the order the key/values have been added to.
 */
public class QueryBuilder {

    // use a linkedhashmap to keep insertion order, nice to have when building the query.
    static class MapOfLists extends LinkedHashMap<String, List<String>> {
        private static final long serialVersionUID = -9221826712691674905L;

        void putSingle(final String key, final String value) {
            getAndPutIfAbsent(key).add(value);
        }

        void add(final String key, final String value) {
            getAndPutIfAbsent(key).add(value);
        }

        String first(final String key) {
            final List<String> values = get(key);
            if ((null != values) && (values.size() > 0)) {
                return values.get(0);
            }
            return null;
        }

        List<String> getAndPutIfAbsent(final String key) {
            List<String> values = get(key);
            if (null == values) {
                values = new ArrayList<String>();
                put(key, values);
            }
            return values;
        }
    }

    private final MapOfLists map = new MapOfLists();
    private final Deque<String> names = new ArrayDeque<String>();
    private String actualName;

    // ------------------------------------------------------------------------

    /**
     * Start writing values for a new name. It does not matter if you then write only one, multiple or no value at all.
     * If you want to write only a single value then just use {@link #addSingle(String, String)}
     * 
     * @throws IllegalStateException if this name already exists.
     */
    public QueryBuilder begin(final String name) {
        checkNotNull(name);
        if (has(name)) {
            throwDuplicate(name);
        }
        if (null != actualName) {
            names.push(actualName);
        }
        actualName = name;
        return this;
    }

    /**
     * Finishes writing the values linked to the name of the previous call to begin(name).
     * 
     * @throws IllegalStateException if begin was not called before end.
     */
    public QueryBuilder end() {
        if (null == actualName) {
            throwFirstCallBeginWithPropertyName();
        }
        if (!names.isEmpty()) {
            actualName = names.pop();
        } else {
            actualName = null;
        }
        return this;
    }

    // ------------------------------------------------------------------------

    /**
     * Writes a single pair name/value.
     * 
     * @throws IllegalStateException if this name already exists.
     */
    public QueryBuilder addSingle(final String name, final String value) {
        checkNotNull(name);
        checkNotNull(value);

        if (has(name)) {
            throwDuplicate(name);
        }
        map.putSingle(name, value);

        return this;
    }

    /**
     * Writes a single pair name/value.
     * 
     * @throws IllegalStateException if this name already exists.
     */
    public QueryBuilder addSingle(final String name, final Number value) {
        checkNotNull(name);
        checkNotNull(value);

        if (has(name)) {
            throwDuplicate(name);
        }
        map.putSingle(name, value.toString());

        return this;
    }

    public QueryBuilder singleNull() {
        // lets just forbid nulls for the moment by removing them
        map.remove(actualName);
        return this;
    }

    /**
     * Writes a single pair name/value.
     * 
     * @throws IllegalStateException if this name already exists.
     */
    public QueryBuilder addSingle(final String name, final Boolean value) {
        checkNotNull(name);
        checkNotNull(value);

        if (has(name)) {
            throwDuplicate(name);
        }
        map.putSingle(name, value.toString());

        return this;
    }

    /**
     * Writes a single pair name/value.
     * 
     * @throws IllegalStateException if this name already exists.
     */
    public QueryBuilder add(final Number value) {
        checkNotNull(value);

        if (null == actualName) {
            throwFirstCallBeginWithPropertyName();
        }
        map.add(actualName, value.toString());

        return this;
    }

    /**
     * Add a value for current key.
     * 
     * @throws IllegalStateException if begin(name) was not called.
     */
    public QueryBuilder add(final Boolean value) {
        checkNotNull(value);

        if (null == actualName) {
            throwFirstCallBeginWithPropertyName();
        }
        map.add(actualName, value.toString());

        return this;
    }

    /**
     * Add a value for current key.
     * 
     * @throws IllegalStateException if begin(name) was not called.
     */
    public QueryBuilder add(final String value) {
        checkNotNull(value);

        if (null == actualName) {
            throwFirstCallBeginWithPropertyName();
        }
        map.add(actualName, value);

        return this;
    }

    /**
     * Add a list of values for current key.
     * 
     * @throws IllegalStateException if begin(name) was not called.
     */
    public QueryBuilder add(final String... values) {
        checkNotNull(values);

        if (null == actualName) {
            throwFirstCallBeginWithPropertyName();
        }
        for (final String value : values) {
            map.add(actualName, value);
        }

        return this;
    }

    // ------------------------------------------------------------------------

    /**
     * @return true if at least one value is linked to this name.
     */
    public boolean has(final String name) {
        return map.containsKey(name);
    }

    /**
     * @return true if exactly one value is linked to this name.
     */
    public boolean hasSingle(final String name) {
        return (null != map.get(name)) && (map.get(name).size() == 1);
    }

    /**
     * @return the first value linked to this name.
     * @throws NoSuchElementException if there is no value mapped to this key.
     */
    public String first(final String name) {
        if (!map.containsKey(name)) {
            throw new NoSuchElementException();
        }
        return map.first(name);
    }

    /**
     * @return all the values linked to this name.
     * @throws NoSuchElementException if there is no value mapped to this key.
     */
    public List<String> values(final String name) {
        if (!map.containsKey(name)) {
            throw new NoSuchElementException();
        }
        return map.get(name);
    }

    // ------------------------------------------------------------------------
    /**
     * Builds an URI using the specified path and actual query string. This uri is compliant with RFC 3986, meaning that
     * query params will be escaped correctly. This uri can be further used to make requests.
     * 
     * @param path to use to locate the resource, the query params will be appended correctly.
     * @return a uri pointing to the specified path and with actual query as query string.
     */
    public URI build(URI path) {
        final StringBuilder sb = new StringBuilder();

        for (final Iterator<Entry<String, List<String>>> it = map.entrySet().iterator(); it.hasNext();) {
            final Entry<String, List<String>> entry = it.next();
            for (final Iterator<String> valueIt = entry.getValue().iterator(); valueIt.hasNext();) {
                sb.append(entry.getKey()).append('=').append(valueIt.next());
                if (valueIt.hasNext()) {
                    sb.append('&');
                }
            }
            if (it.hasNext()) {
                sb.append('&');
            }
        }

        try {
            return new URI(path.getScheme(), path.getUserInfo(), path.getHost(), path.getPort(), path.getPath(),
                           sb.toString(), path.getFragment());
        } catch (final URISyntaxException e) {
            throw new KasperQueryAdapterException("Could not create query.", e);
        }
    }

    public Map<String, List<String>> build() {
        final HashMap<String, List<String>> copyMap = new HashMap<String, List<String>>();
        for (final Map.Entry<String, List<String>> e : map.entrySet()) {
            copyMap.put(e.getKey(), new ArrayList<String>(e.getValue()));
        }
        return copyMap;
    }

    // ------------------------------------------------------------------------

    private void throwDuplicate(final String name) {
        throw new IllegalStateException("Duplicate key " + name);
    }

    private void throwFirstCallBeginWithPropertyName() {
        throw new IllegalStateException("First call begin with the name of the property.");
    }

}
