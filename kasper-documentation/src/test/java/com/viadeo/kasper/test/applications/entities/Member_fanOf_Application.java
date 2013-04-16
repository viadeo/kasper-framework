package com.viadeo.kasper.test.applications.entities;

import org.axonframework.domain.DomainEventStream;
import org.axonframework.domain.EventRegistrationCallback;
import org.joda.time.DateTime;

import com.viadeo.kasper.IDomain;
import com.viadeo.kasper.IKasperID;
import com.viadeo.kasper.er.IRootRelation;
import com.viadeo.kasper.er.annotation.XKasperRelation;
import com.viadeo.kasper.locators.IDomainLocator;
import com.viadeo.kasper.test.applications.Applications;
import com.viadeo.kasper.test.root.entities.Member;

@XKasperRelation(domain = Applications.class, label = Member_fanOf_Application.NAME, description = Member_fanOf_Application.DESCRIPTION)
public class Member_fanOf_Application implements IRootRelation<Member, Application> {
	private static final long serialVersionUID = -6384465229521499408L;
	
	public static final String NAME = "fan_of";
	public static final String DESCRIPTION = "Tha Member is a big fan of this application";

	@Override
	public IKasperID getSourceIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IKasperID getTargetIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isBidirectional() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IDomain getDomain() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDomainLocator(IDomainLocator domainLocator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <I extends IKasperID> I getEntityId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DateTime getCreationDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DateTime getModificationDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initializeState(DomainEventStream arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEventRegistrationCallback(EventRegistrationCallback arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void commitEvents() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IKasperID getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getUncommittedEventCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DomainEventStream getUncommittedEvents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDeleted() {
		// TODO Auto-generated method stub
		return false;
	}

}
