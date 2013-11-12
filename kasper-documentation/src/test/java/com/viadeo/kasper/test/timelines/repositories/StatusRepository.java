package com.viadeo.kasper.test.timelines.repositories;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.ddd.annotation.XKasperRepository;
import com.viadeo.kasper.test.timelines.entities.Status;

@XKasperRepository
public class StatusRepository implements IRepository<Status> {

	@Override
	public void add(Status arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Status load(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Status load(Object arg0, Long arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

    @Override
    public boolean has(KasperID id) {
        return false;
    }

    @Override
    public Status get(KasperID aggregateIdentifier, Long expectedVersion) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Status get(KasperID aggregateIdentifier) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
