package com.viadeo.kasper.test.root;

import com.viadeo.kasper.ddd.IAggregateRoot;
import com.viadeo.kasper.ddd.IEntity;
import com.viadeo.kasper.ddd.IInternalDomain;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;

import java.util.Set;

@XKasperDomain(prefix=Facebook.PREFIX, label=Facebook.NAME, description=Facebook.DESCRIPTION)
public class Facebook implements IInternalDomain {

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
	public Set<? extends IEntity> getDomainEntities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E extends IAggregateRoot> IRepository<E> getEntityRepository(E entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E extends IAggregateRoot> IRepository<E> getEntityRepository(Class<E> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

}
