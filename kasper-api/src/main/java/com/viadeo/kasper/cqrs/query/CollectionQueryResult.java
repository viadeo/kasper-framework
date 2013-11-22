// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class CollectionQueryResult<RES extends QueryResult> implements Iterable<RES>, QueryResult  {

    /** Generic parameter position for Data Transfer Object */
    int PARAMETER_RESULT_POSITION = 0;

	private Collection<RES> list;

    // ------------------------------------------------------------------------

    protected CollectionQueryResult() {
        /* Jackson */
    }

	protected CollectionQueryResult(final Collection<RES> list) {
		this.list = checkNotNull(list);
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
		return this.list;
	}

    public void setList(final Collection<RES> list) {
        if (null == this.list) {
            this.list = checkNotNull(list);
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
        this.setList(list);
        return (P) this;
    }

    @SuppressWarnings("unchecked")
    public <P extends CollectionQueryResult> P withListAsIterator(final Iterator<RES> iterator) {
        this.setListAsIterator(iterator);
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
        return Objects.equal(this.list, that.list);
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
