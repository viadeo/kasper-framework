// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Iterator;

import com.viadeo.kasper.cqrs.query.IQueryCollectionDTO;
import com.viadeo.kasper.cqrs.query.IQueryDTO;

public abstract class AbstractQueryCollectionDTO<DTO extends IQueryDTO> implements IQueryCollectionDTO<DTO> {

	private static final long serialVersionUID = 5181041546682941845L;

	private final Collection<DTO> innerCollection;

	protected AbstractQueryCollectionDTO(Collection<DTO> innerCollection) {
		this.innerCollection = checkNotNull(innerCollection);
	}

	@Override
	public Iterator<DTO> iterator() {
		return this.innerCollection.iterator();
	}

	@Override
	public int getSize() {
		return this.innerCollection.size();
	}

	protected Collection<DTO> list() {
		return this.innerCollection;
	}

}
