package com.viadeo.kasper.test.timelines.repositories;

import com.viadeo.kasper.ddd.Repository;
import com.viadeo.kasper.ddd.annotation.XKasperRepository;
import com.viadeo.kasper.test.timelines.entities.Timeline;

@XKasperRepository
public class TimelineRepository implements Repository<Timeline> {

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


}
