package com.viadeo.kasper.test.root.entities;

import com.viadeo.kasper.IDomain;
import com.viadeo.kasper.IKasperID;
import com.viadeo.kasper.er.IRootRelation;
import com.viadeo.kasper.er.annotation.XKasperRelation;
import com.viadeo.kasper.locators.IDomainLocator;
import com.viadeo.kasper.test.root.Facebook;
import org.axonframework.domain.DomainEventStream;
import org.axonframework.domain.EventRegistrationCallback;
import org.joda.time.DateTime;

@XKasperRelation(domain = Facebook.class, label = Member_connectedTo_Member.NAME)
public class Member_connectedTo_Member implements IRootRelation<Member, Member> {
	
	private static final long serialVersionUID = 2799008865289493137L;

	public static final String NAME = "connected_to";
	
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
	public void setDomainLocator(final IDomainLocator domainLocator) {
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
	public void initializeState(final DomainEventStream arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEventRegistrationCallback(final EventRegistrationCallback arg0) {
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
