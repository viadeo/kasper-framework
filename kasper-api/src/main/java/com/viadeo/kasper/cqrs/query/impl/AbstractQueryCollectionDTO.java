// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.impl;

import com.google.common.base.Objects;
import com.viadeo.kasper.cqrs.query.QueryCollectionDTO;
import com.viadeo.kasper.cqrs.query.QueryDTO;

import java.util.Collection;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractQueryCollectionDTO<DTO extends QueryDTO> implements QueryCollectionDTO<DTO> {

	private static final long serialVersionUID = 5181041546682941845L;

	private Collection<DTO> innerCollection;

    // ------------------------------------------------------------------------

    protected AbstractQueryCollectionDTO() {
        /* Jackson */
    }

	protected AbstractQueryCollectionDTO(final Collection<DTO> innerCollection) {
		this.innerCollection = checkNotNull(innerCollection);
	}

    // ------------------------------------------------------------------------

	@Override
	public Iterator<DTO> iterator() {
		return this.innerCollection.iterator();
	}

    // ------------------------------------------------------------------------

	@Override
	public int getCount() {
		return this.innerCollection.size();
	}

    @Override
	public Collection<DTO> getList() {
		return this.innerCollection;
	}

    public void setList(final Collection<DTO> innerCollection) {
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

        final AbstractQueryCollectionDTO that = (AbstractQueryCollectionDTO) o;
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
