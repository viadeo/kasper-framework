package com.viadeo.kasper.test.root;

import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.Entity;
import com.viadeo.kasper.ddd.InternalDomain;
import com.viadeo.kasper.ddd.Repository;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;

import java.util.Set;

@XKasperDomain(prefix=Facebook.PREFIX, label=Facebook.NAME, description=Facebook.DESCRIPTION)
public class Facebook implements InternalDomain {

	public static final String PREFIX = "fb";
	public static final String NAME = "Facebook";
	public static final String DESCRIPTION = "Root Facebook domain";
	
	@Override
	public String getPrefix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<? extends Entity> getDomainEntities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E extends AggregateRoot> Repository<E> getEntityRepository(E entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E extends AggregateRoot> Repository<E> getEntityRepository(Class<E> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

}
