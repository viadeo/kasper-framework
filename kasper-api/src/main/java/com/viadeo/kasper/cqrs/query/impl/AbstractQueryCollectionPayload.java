// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.impl;

import com.google.common.base.Objects;
import com.viadeo.kasper.cqrs.query.CollectionQueryPayload;

import java.util.Collection;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractQueryCollectionPayload<RES> implements CollectionQueryPayload<RES> {
    
	private Collection<RES> innerCollection;

    // ------------------------------------------------------------------------

    protected AbstractQueryCollectionPayload() {
        /* Jackson */
    }

	protected AbstractQueryCollectionPayload(final Collection<RES> innerCollection) {
		this.innerCollection = checkNotNull(innerCollection);
	}

    // ------------------------------------------------------------------------

	@Override
	public Iterator<RES> iterator() {
		return this.innerCollection.iterator();
	}

    // ------------------------------------------------------------------------

	@Override
	public int getCount() {
		return this.innerCollection.size();
	}

    @Override
	public Collection<RES> getList() {
		return this.innerCollection;
	}

    public void setList(final Collection<RES> innerCollection) {
        this.innerCollection = innerCollection;
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if ((null == o) || (getClass() != o.getClass())) {
            return false;
        }

        final AbstractQueryCollectionPayload<?> that = (AbstractQueryCollectionPayload<?>) o;
        return Objects.equal(this.innerCollection, that.innerCollection);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(innerCollection);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("innerCollection", innerCollection)
                .toString();
    }

}
