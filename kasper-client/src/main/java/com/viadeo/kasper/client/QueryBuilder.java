// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client;

import java.util.List;
import java.util.NoSuchElementException;
import javax.ws.rs.core.MultivaluedMap;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class QueryBuilder {

    private final MultivaluedMap<String, String> map = new MultivaluedMapImpl();
    private String actualName;

    // ------------------------------------------------------------------------
    
    public QueryBuilder begin(final String name) {
        if (has(name)) {
        	throwDuplicate(name);
        }
        actualName = name;
        return this;
    }

    public QueryBuilder end() {
        actualName = null;
        return this;
    }

    // ------------------------------------------------------------------------
    
    public QueryBuilder addSingle(final String name, final String value) {
        if (has(name)) {
        	throwDuplicate(name);
        }
        map.putSingle(name, value);
        return this;
    }
    
    public QueryBuilder addSingle(final String name, final Number value) {
        if (has(name)) {
        	throwDuplicate(name);
        }
        map.putSingle(name, value.toString());
        return this;
    }
    
    public QueryBuilder addSingle(final String name, final Boolean value) {
        if (has(name)) {
        	throwDuplicate(name);
        }
        map.putSingle(name, value.toString());
        return this;
    }
    
    public QueryBuilder add(final Number value) {
        if (null == actualName) {
        	throwFirstCallBeginWithPropertyName();
        }
        map.add(actualName, value.toString());
        return this;
    }
    
    public QueryBuilder add(final Boolean value) {
        if (null == actualName) {
        	throwFirstCallBeginWithPropertyName();
        }
        map.add(actualName, value.toString());
        return this;
    }

    public QueryBuilder add(final String value) {
        if (null == actualName) {
        	throwFirstCallBeginWithPropertyName();
        }
        map.add(actualName, value);
        return this;
    }

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
    
    public boolean has(final String name) {
        return map.containsKey(name);
    }
    
    public boolean hasSingle(final String name) {
        return (null != map.get(name)) && (map.get(name).size() == 1);
    }
    
    public String first(final String name) {
        if (!map.containsKey(name)) {
            throw new NoSuchElementException();
        }
        return map.getFirst(name);
    }
    
    public List<String> values(final String name) {
        if (!map.containsKey(name)) {
            throw new NoSuchElementException();
        }
        return map.get(name);
    }
    
    // ------------------------------------------------------------------------
    
    MultivaluedMap<String, String> build() {
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
