/*
 * Copyright 2013 Viadeo.com
 */

package com.viadeo.kasper.client;

import java.util.List;
import java.util.NoSuchElementException;
import javax.ws.rs.core.MultivaluedMap;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class QueryBuilder {
    private final MultivaluedMap<String, String> map = new MultivaluedMapImpl();
    private String actualName;

    public QueryBuilder begin(String name) {
        if (has(name)) throw new IllegalStateException("Duplicate key " + name);
        actualName = name;
        return this;
    }

    public QueryBuilder end() {
        actualName = null;
        return this;
    }

    public QueryBuilder addSingle(String name, String value) {
        if (has(name)) throw new IllegalStateException("Duplicate key " + name);
        map.putSingle(name, value);
        return this;
    }
    
    public QueryBuilder addSingle(String name, Number value) {
        if (has(name)) throw new IllegalStateException("Duplicate key " + name);
        map.putSingle(name, value.toString());
        return this;
    }
    
    public QueryBuilder addSingle(String name, Boolean value) {
        if (has(name)) throw new IllegalStateException("Duplicate key " + name);
        map.putSingle(name, value.toString());
        return this;
    }
    
    public QueryBuilder add(Number value) {
        if (actualName == null) {
            throw new IllegalStateException("First call begin with the name of the property.");
        }
        map.add(actualName, value.toString());
        return this;
    }
    
    public QueryBuilder add(Boolean value) {
        if (actualName == null) {
            throw new IllegalStateException("First call begin with the name of the property.");
        }
        map.add(actualName, value.toString());
        return this;
    }

    public QueryBuilder add(String value) {
        if (actualName == null) {
            throw new IllegalStateException("First call begin with the name of the property.");
        }
        map.add(actualName, value);
        return this;
    }

    public QueryBuilder add(String... values) {
        if (actualName == null) {
            throw new IllegalStateException("First call begin with the name of the property.");
        }
        for (String value : values) {
            map.add(actualName, value);
        }
        return this;
    }

    public boolean has(String name) {
        return map.containsKey(name);
    }
    
    public boolean hasSingle(String name) {
        return map.get(name) != null && map.get(name).size() == 1;
    }
    
    public String first(String name) {
        if (!map.containsKey(name)) throw new NoSuchElementException();
        return map.getFirst(name);
    }
    
    public List<String> values(String name) {
        if (!map.containsKey(name)) throw new NoSuchElementException();
        return map.get(name);
    }
    
    MultivaluedMap<String, String> build() {
        return new MultivaluedMapImpl(map);
    }
}
