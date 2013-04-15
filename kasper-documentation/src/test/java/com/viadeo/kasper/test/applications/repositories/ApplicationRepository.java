package com.viadeo.kasper.test.applications.repositories;

import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.ddd.annotation.XKasperRepository;
import com.viadeo.kasper.test.applications.entities.Application;

@XKasperRepository(description = ApplicationRepository.DESCRIPTION)
public class ApplicationRepository implements IRepository<Application> {

	public static final String DESCRIPTION = "The applications repository";
	
	@Override
	public void add(final Application arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Application load(final Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Application load(final Object arg0, final Long arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
