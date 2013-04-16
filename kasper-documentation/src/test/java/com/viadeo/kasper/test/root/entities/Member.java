package com.viadeo.kasper.test.root.entities;

import org.axonframework.domain.DomainEventStream;
import org.axonframework.domain.EventRegistrationCallback;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.joda.time.DateTime;

import com.viadeo.kasper.IDomain;
import com.viadeo.kasper.IKasperID;
import com.viadeo.kasper.er.IRootConcept;
import com.viadeo.kasper.er.annotation.XKasperConcept;
import com.viadeo.kasper.locators.IDomainLocator;
import com.viadeo.kasper.test.root.Facebook;
import com.viadeo.kasper.test.root.events.MemberCreatedEvent;

@XKasperConcept(domain = Facebook.class, label = Member.NAME)
public class Member implements IRootConcept {
	private static final long serialVersionUID = 2514520954354227657L;

	public static final String NAME = "FacebookMember";
	
	// ------------------------------------------------------------------------
	
	@EventHandler
	public void handleCreatedEvent(final MemberCreatedEvent event) {
		
	}
	
	// ------------------------------------------------------------------------
	
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
