package com.viadeo.kasper.test.timelines.entities;

import com.viadeo.kasper.Domain;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.er.RootConcept;
import com.viadeo.kasper.er.annotation.XKasperConcept;
import com.viadeo.kasper.test.timelines.Timelines;
import org.axonframework.domain.DomainEventStream;
import org.axonframework.domain.EventRegistrationCallback;
import org.joda.time.DateTime;

@XKasperConcept(domain = Timelines.class, label = Timeline.NAME)
public class Timeline implements RootConcept {
	private static final long serialVersionUID = 191636014372954922L;
	
	public static final String NAME = "MemberTimeline";
	
	@Override
	public Domain getDomain() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDomainLocator(DomainLocator domainLocator) {
		// TODO Auto-generated method stub

	}

	@Override
	public <I extends KasperID> I getEntityId() {
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
	public KasperID getIdentifier() {
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
