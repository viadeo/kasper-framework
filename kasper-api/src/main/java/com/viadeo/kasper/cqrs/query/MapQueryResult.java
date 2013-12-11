// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class MapQueryResult<T extends QueryResult> implements Iterable<Map.Entry<String,T>>, QueryResult  {

	private Map<String,T> map;

    // ------------------------------------------------------------------------

    protected MapQueryResult() {
        /* Jackson */
    }

	protected MapQueryResult(final Map<String,T> map) {
		this.map = Maps.newHashMap(checkNotNull(map));
	}

    // ------------------------------------------------------------------------

    @Override
    public Iterator<Map.Entry<String, T>> iterator() {
		return this.map.entrySet().iterator();
	}

    // ------------------------------------------------------------------------

	public int getCount() {
        if (null == map) {
            return 0;
        }
        return this.map.size();
	}
    public boolean isEmpty() {
        if (null == map) {
            return true;
        }
        return this.map.isEmpty();
    }

	public Map<String,T> getMap() {
        final ImmutableMap.Builder<String,T> builder = new ImmutableMap.Builder<String,T>();
        if (null != map) {
            builder.putAll(this.map);
        }
        return builder.build();
	}

    public void setMap(final Map<String,T> map) {
        if (null == this.map) {
            this.map = Maps.newHashMap(checkNotNull(map));
        } else {
            throw new UnsupportedOperationException("MapQueryResult is immutable");
        }
        this.map= checkNotNull(map);
    }

    @SuppressWarnings("unchecked")
    public <P extends MapQueryResult> P withMap(final Map<String,T> map) {
        this.setMap(checkNotNull(map));
        return (P) this;
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(final java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final MapQueryResult that = (MapQueryResult) o;

        if (!map.equals(that.map)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("map", map)
                .toString();
    }

}
