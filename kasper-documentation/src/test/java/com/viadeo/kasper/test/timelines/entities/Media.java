package com.viadeo.kasper.test.timelines.entities;

import com.viadeo.kasper.IDomain;
import com.viadeo.kasper.IKasperID;
import com.viadeo.kasper.er.IComponentConcept;
import com.viadeo.kasper.er.annotation.XKasperConcept;
import com.viadeo.kasper.locators.IDomainLocator;
import com.viadeo.kasper.test.timelines.Timelines;
import org.axonframework.domain.DomainEventMessage;
import org.axonframework.eventsourcing.AbstractEventSourcedAggregateRoot;
import org.joda.time.DateTime;

@XKasperConcept(domain = Timelines.class, label = Media.NAME)
public class Media implements IComponentConcept<Status> {
	private static final long serialVersionUID = -5482616251141907946L;

	public static final String NAME = "StatusMedia";
	
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
