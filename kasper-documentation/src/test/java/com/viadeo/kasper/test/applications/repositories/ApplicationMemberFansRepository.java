package com.viadeo.kasper.test.applications.repositories;

import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.ddd.annotation.XKasperRepository;
import com.viadeo.kasper.test.applications.entities.Member_fanOf_Application;

@XKasperRepository
public class ApplicationMemberFansRepository implements IRepository<Member_fanOf_Application> {

	@Override
	public void add(Member_fanOf_Application arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Member_fanOf_Application load(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Member_fanOf_Application load(Object arg0, Long arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
}
