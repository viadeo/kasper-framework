// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.impl;

import com.google.common.base.Objects;
import com.viadeo.kasper.cqrs.query.QueryCollectionResult;
import com.viadeo.kasper.cqrs.query.QueryResult;

import java.util.Collection;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractQueryCollectionResult<RES extends QueryResult> implements QueryCollectionResult<RES> {

	private static final long serialVersionUID = 5181041546682941845L;

	private Collection<RES> innerCollection;

    // ------------------------------------------------------------------------

    protected AbstractQueryCollectionResult() {
        /* Jackson */
    }

	protected AbstractQueryCollectionResult(final Collection<RES> innerCollection) {
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final AbstractQueryCollectionResult that = (AbstractQueryCollectionResult) o;
        return Objects.equal(this.innerCollection, that.innerCollection);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(
                innerCollection
        );
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("innerCollection", innerCollection)
                .toString();
    }

}
