package com.viadeo.kasper.test.root.repositories;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.ddd.annotation.XKasperRepository;
import com.viadeo.kasper.test.root.entities.Member;

@XKasperRepository
public class MemberRepository implements IRepository<Member> {

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

    @Override
    public boolean has(KasperID id) {
        return false;
    }

    @Override
    public Member get(KasperID aggregateIdentifier, Long expectedVersion) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Member get(KasperID aggregateIdentifier) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
