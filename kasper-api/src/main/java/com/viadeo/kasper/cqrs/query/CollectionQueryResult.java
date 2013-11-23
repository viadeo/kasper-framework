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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class CollectionQueryResult<RES extends QueryResult> implements Iterable<RES>, QueryResult  {

    /** Generic parameter position for Data Transfer Object */
    int PARAMETER_RESULT_POSITION = 0;

	private ArrayList<RES> list;

    // ------------------------------------------------------------------------

    protected CollectionQueryResult() {
        /* Jackson */
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
        if (null == list) {
            return 0;
        }
		return this.list.size();
	}

    public boolean isEmpty() {
        if (null == list) {
            return true;
        }
        return this.list.isEmpty();
    }

	public Collection<RES> getList() {
		final ImmutableList.Builder<RES> builder = new ImmutableList.Builder<RES>();
        if (null != list) {
            builder.addAll(this.list);
        }
        return builder.build();
	}

    public void setList(final Collection<RES> list) {
        if (null == this.list) {
            this.list = Lists.newArrayList(checkNotNull(list));
        } else {
            throw new UnsupportedOperationException("CollectionQueryResult is immutable");
        }
    }

    public void setListAsIterator(final Iterator<RES> iterator) {
        checkNotNull(iterator);
        if (null == this.list) {
            this.list = new ArrayList<RES>();
            while (iterator.hasNext()) {
                this.list.add(iterator.next());
            }
        } else {
            throw new UnsupportedOperationException("CollectionQueryResult is immutable");
        }
    }

    @SuppressWarnings("unchecked")
    public <P extends CollectionQueryResult> P withList(final Collection<RES> list) {
        this.setList(checkNotNull(list));
        return (P) this;
    }

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
                .toString();
    }

}
