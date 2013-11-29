// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class MapQueryResult<String,T> implements Iterable<Map.Entry<String,T>>, QueryResult  {

	private Map<String,T> map;

    // ------------------------------------------------------------------------

    protected MapQueryResult() {
        /* Jackson */
    }

	protected MapQueryResult(final Map<String,T> map) {
		this.map = checkNotNull(map);
	}

    // ------------------------------------------------------------------------

    public void put(final String key, T value) {
        if (null == map) {
            map = Maps.newHashMap();
        }
        map.put(key,value);
    }
    public void put(Map.Entry<String,T> entry) {
        put(entry.getKey(), entry.getValue());
    }

	@Override
	public Iterator<Map.Entry<String,T>> iterator() {
		return this.map.entrySet().iterator();
	}

    // ------------------------------------------------------------------------

	public int getCount() {
		return this.map.size();
	}

	public Map<String,T> getMap() {
		return this.map;
	}

    public void setMap(final Map<String,T> map) {
        this.map= checkNotNull(map);
    }



    // ------------------------------------------------------------------------

    @Override
    public boolean equals(final java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapQueryResult that = (MapQueryResult) o;

        if (!map.equals(that.map)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public java.lang.String toString() {
        return Objects.toStringHelper(this)
                .add("map", map)
                .toString();
    }

}
