// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class CollectionQueryResult<RES extends QueryResult> implements Iterable<RES>, QueryResult  {

    private static final long serialVersionUID = -3864225412367930495L;

    /** Generic parameter position for Data Transfer Object */
    int PARAMETER_RESULT_POSITION = 0;

	private final List<RES> list;

    // ------------------------------------------------------------------------

    protected CollectionQueryResult() {
        this(Lists.<RES>newArrayList());
    }

	protected CollectionQueryResult(final Collection<RES> list) {
		this.list = Lists.newArrayList(checkNotNull(list));
	}

    // ------------------------------------------------------------------------

	@Override
	public Iterator<RES> iterator() {
		return this.list.iterator();
	}

    // ------------------------------------------------------------------------

	public int getCount() {
		return this.list.size();
	}

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

	public Collection<RES> getList() {
        return ImmutableList.copyOf(this.list);
	}

    /**
     * This method is clearly a weakness of immutability, we keep it only to ensure retro-compatibility of the api.
     * @param list the list to be set
     * @deprecated deprecated in order to keep retro-compatibility before to be deleted.
     */
    @Deprecated
    public void setList(final Collection<RES> list) {
        checkNotNull(list);
        this.list.clear();
        this.list.addAll(Lists.newArrayList(list));
    }

    /**
     * This method is clearly a weakness of immutability, we keep it only to ensure retro-compatibility of the api.
     * @param iterator the iterator to be set
     * @deprecated in order to keep retro-compatibility before to be deleted.
     */
    @Deprecated
    public void setListAsIterator(final Iterator<RES> iterator) {
        checkNotNull(iterator);
        this.list.clear();
        this.list.addAll(Lists.newArrayList(iterator));
    }

    /**
     * @deprecated in order to keep retro-compatibility before to be deleted.
     * @see #setList(java.util.Collection)
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public <P extends CollectionQueryResult> P withList(final Collection<RES> list) {
        this.setList(checkNotNull(list));
        return (P) this;
    }

    /**
     * @deprecated in order to keep retro-compatibility before to be deleted.
     * @see #setListAsIterator(java.util.Iterator)
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public <P extends CollectionQueryResult> P withListAsIterator(final Iterator<RES> iterator) {
        this.setListAsIterator(checkNotNull(iterator));
        return (P) this;
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(final Object o) {
        if (this == checkNotNull(o)) {
            return true;
        }
        if ((null == o) || (getClass() != o.getClass())) {
            return false;
        }

        final CollectionQueryResult that = (CollectionQueryResult) o;

        if ((null == this.list) && (null != that.list)) {
            return false;
        }

        if ((null == that.list) && (null != this.list)) {
            return false;
        }

        if (null == that.list) {
            return true;
        }

        if (this.list.size() != that.list.size()) {
            return false;
        }

        final Iterator it1 = this.list.iterator();
        final Iterator it2 = that.list.iterator();

        while (it1.hasNext()) {
            if ( ! it1.next().equals(it2.next())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(list);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("list", list)
                .add("count", getCount())
                .toString();
    }

}
