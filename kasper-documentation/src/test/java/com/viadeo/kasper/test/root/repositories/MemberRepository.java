package com.viadeo.kasper.test.root.repositories;

import com.viadeo.kasper.ddd.Repository;
import com.viadeo.kasper.ddd.annotation.XKasperRepository;
import com.viadeo.kasper.test.root.entities.Member;

@XKasperRepository
public class MemberRepository implements Repository<Member> {

	@Override
	public void add(Member arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Member load(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Member load(Object arg0, Long arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
