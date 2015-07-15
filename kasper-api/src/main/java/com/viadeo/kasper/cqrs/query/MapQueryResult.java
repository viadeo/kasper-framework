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
    private static final long serialVersionUID = -3955605974595645008L;

    private final Map<String,T> map;

    // ------------------------------------------------------------------------

    protected MapQueryResult() {
        this(Maps.<String,T>newHashMap());
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
        return this.map.size();
	}

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

	public Map<String,T> getMap() {
        return ImmutableMap.copyOf(this.map);
	}

    /**
     * This method is clearly a weakness of immutability, we keep it only to ensure retro-compatibility of the api.
     * @param map the map to be set
     * @deprecated deprecated in order to keep retro-compatibility before to be deleted.
     */
    @Deprecated
    public void setMap(final Map<String,T> map) {
        checkNotNull(map);
        this.map.clear();
        this.map.putAll(Maps.newHashMap(map));
    }

    /**
     * @param map the map to be set
     * @param <P> the inferred type of <code>MapQueryResult</code>
     * @return the mutated <code>MapQueryResult</code> instance
     * @deprecated deprecated in order to keep retro-compatibility before to be deleted
     * @see #setMap(java.util.Map)
     */
    @Deprecated
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

        if ( ! map.equals(that.map)) {
            return false;
        }

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
