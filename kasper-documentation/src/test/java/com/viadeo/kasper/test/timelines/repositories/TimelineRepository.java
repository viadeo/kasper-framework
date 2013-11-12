package com.viadeo.kasper.test.timelines.repositories;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.ddd.annotation.XKasperRepository;
import com.viadeo.kasper.test.timelines.entities.Timeline;

@XKasperRepository
public class TimelineRepository implements IRepository<Timeline> {

	@Override
	public void add(Timeline arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Timeline load(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Timeline load(Object arg0, Long arg1) {
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
    public Timeline get(KasperID aggregateIdentifier, Long expectedVersion) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Timeline get(KasperID aggregateIdentifier) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


}
