// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

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

     class MapOfLists extends HashMap<String, List<String>> {
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
    			values = new ArrayList<>();
    			put(key, values);
    		}
    		return values;
		}
    }
     
    private final MapOfLists map = new MapOfLists();
    private final Deque<String> names = new ArrayDeque<>();
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

    public Map<String, List<String>> build() {
    	final HashMap<String, List<String>> copyMap = new HashMap<>();
    	for (final Map.Entry<String, List<String>> e : map.entrySet()) {
    		copyMap.put(e.getKey(), new ArrayList<>(e.getValue()));
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
