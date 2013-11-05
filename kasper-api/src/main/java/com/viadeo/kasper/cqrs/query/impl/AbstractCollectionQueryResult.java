// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.impl;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.viadeo.kasper.cqrs.query.CollectionQueryResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractCollectionQueryResult<RES> implements CollectionQueryResult<RES> {
    
	private Collection<RES> list;

    // ------------------------------------------------------------------------

    protected AbstractCollectionQueryResult() {
        /* Jackson */
    }

	protected AbstractCollectionQueryResult(final Collection<RES> list) {
		this.list = checkNotNull(list);
	}

    // ------------------------------------------------------------------------

    public void add(final RES element) {
        if (null == list) {
            list = Lists.newArrayList();
        }
        list.add(element);
    }

	@Override
	public Iterator<RES> iterator() {
		return this.list.iterator();
	}

    // ------------------------------------------------------------------------

	@Override
	public int getCount() {
		return this.list.size();
	}

    @Override
	public Collection<RES> getList() {
		return this.list;
	}

    public void setList(final Collection<RES> list) {
        this.list = checkNotNull(list);
    }

    public void setListAsIterator(final Iterator<RES> iterator) {
        checkNotNull(iterator);
        this.list = new ArrayList<RES>();
        while (iterator.hasNext()) {
            this.list.add(iterator.next());
        }
    }

    @SuppressWarnings("unchecked")
    public <P extends AbstractCollectionQueryResult> P withList(final Collection<RES> list) {
        this.setList(list);
        return (P) this;
    }

    @SuppressWarnings("unchecked")
    public <P extends AbstractCollectionQueryResult> P withListAsIterator(final Iterator<RES> iterator) {
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

        final AbstractCollectionQueryResult that = (AbstractCollectionQueryResult) o;
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
