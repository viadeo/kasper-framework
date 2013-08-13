package com.viadeo.kasper.test.timelines.entities;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.er.ComponentConcept;
import com.viadeo.kasper.er.annotation.XKasperConcept;
import com.viadeo.kasper.test.timelines.Timelines;
import org.axonframework.domain.DomainEventMessage;
import org.axonframework.eventsourcing.AbstractEventSourcedAggregateRoot;
import org.joda.time.DateTime;

@XKasperConcept(domain = Timelines.class, label = Media.NAME)
public class Media implements ComponentConcept<Status> {
	private static final long serialVersionUID = -5482616251141907946L;

	public static final String NAME = "StatusMedia";
	
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

	@SuppressWarnings("rawtypes")
	@Override
	public void handleRecursively(DomainEventMessage arg0) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void registerAggregateRoot(AbstractEventSourcedAggregateRoot arg0) {
		// TODO Auto-generated method stub
		
	}

}
