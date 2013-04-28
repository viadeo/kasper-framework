// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client.lib;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;
import javax.ws.rs.core.MultivaluedMap;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Low level class allowing to build a query.
 * Suppose you want to have the following parameters into a query :
 *  name = foo
 *  nicknames = bar, foo bar, toto
 *  
 *  It would be done using the QueryBuilder like that:
 *  <pre>
 *      QueryBuilder builder = new QueryBuilder();
 *      
 *      builder.begin("nicknames")
 *                 .add("bar", "foo bar", "toto")
 *              .end()
 *              .addSingle("name", "foo");
 *  </pre>
 *  
 *  It will also make sure that the query is consistent by forbidding you to overwrite a pair of key/value(s) that has been written.
 */
public class QueryBuilder {

    private final MultivaluedMap<String, String> map = new MultivaluedMapImpl();
    private final Deque<String> names = new ArrayDeque<String>();
    private String actualName;

    // ------------------------------------------------------------------------

    /**
     * Start writing values for a new name.
     * It does not matter if you then write only one, multiple or no value at all.
     * 
     * If you want to write only a single value then just use {@link #addSingle(String, String)}
     * 
     * @throws IllegalStateException if this name already exists.
     */
    public QueryBuilder begin(final String name) {
        if (has(name)) {
            throwDuplicate(name);
        }
        if (actualName != null)
            names.push(actualName);
        actualName = name;
        return this;
    }

    /**
     * Finishes writing the values linked to the name of the previous call to begin(name).
     * @throws IllegalStateException if begin was not called before end.
     */
    public QueryBuilder end() {
        if (actualName == null)
            throwFirstCallBeginWithPropertyName();
        if (!names.isEmpty())
            actualName = names.pop();
        else
            actualName = null;
        return this;
    }

    // ------------------------------------------------------------------------

    /**
     * Writes a single pair name/value.
     * @throws IllegalStateException if this name already exists.
     */
    public QueryBuilder addSingle(final String name, final String value) {
        if (has(name)) {
            throwDuplicate(name);
        }
        map.putSingle(name, value);
        return this;
    }

    /**
     * Writes a single pair name/value.
     * @throws IllegalStateException if this name already exists.
     */
    public QueryBuilder addSingle(final String name, final Number value) {
        if (has(name)) {
            throwDuplicate(name);
        }
        map.putSingle(name, value.toString());
        return this;
    }

    /**
     * Writes a single pair name/value.
     * @throws IllegalStateException if this name already exists.
     */
    public QueryBuilder addSingle(final String name, final Boolean value) {
        if (has(name)) {
            throwDuplicate(name);
        }
        map.putSingle(name, value.toString());
        return this;
    }

    /**
     * Writes a single pair name/value.
     * @throws IllegalStateException if this name already exists.
     */
    public QueryBuilder add(final Number value) {
        if (null == actualName) {
            throwFirstCallBeginWithPropertyName();
        }
        map.add(actualName, value.toString());
        return this;
    }

    /**
     * Add a value for current key.
     * @throws IllegalStateException if begin(name) was not called.
     */
    public QueryBuilder add(final Boolean value) {
        if (null == actualName) {
            throwFirstCallBeginWithPropertyName();
        }
        map.add(actualName, value.toString());
        return this;
    }

    /**
     * Add a value for current key.
     * @throws IllegalStateException if begin(name) was not called.
     */
    public QueryBuilder add(final String value) {
        if (null == actualName) {
            throwFirstCallBeginWithPropertyName();
        }
        map.add(actualName, value);
        return this;
    }

    /**
     * Add a list of values for current key.
     * @throws IllegalStateException if begin(name) was not called.
     */
    public QueryBuilder add(final String... values) {
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
        return map.getFirst(name);
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

    public MultivaluedMap<String, String> build() {
        return new MultivaluedMapImpl(map);
    }

    // ------------------------------------------------------------------------

    private void throwDuplicate(final String name) {
        throw new IllegalStateException("Duplicate key " + name);
    }

    private void throwFirstCallBeginWithPropertyName() {
        throw new IllegalStateException("First call begin with the name of the property.");
    }

}
