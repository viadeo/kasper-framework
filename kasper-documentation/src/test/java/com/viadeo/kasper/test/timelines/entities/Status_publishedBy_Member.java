package com.viadeo.kasper.test.timelines.entities;

import com.viadeo.kasper.IDomain;
import com.viadeo.kasper.IKasperID;
import com.viadeo.kasper.er.IComponentRelation;
import com.viadeo.kasper.er.annotation.XKasperRelation;
import com.viadeo.kasper.locators.IDomainLocator;
import com.viadeo.kasper.test.root.entities.Member;
import com.viadeo.kasper.test.timelines.Timelines;
import org.axonframework.domain.DomainEventMessage;
import org.axonframework.eventsourcing.AbstractEventSourcedAggregateRoot;
import org.joda.time.DateTime;

@XKasperRelation(domain = Timelines.class, label = Status_publishedBy_Member.NAME, description = Status_publishedBy_Member.DESCRIPTION)
public class Status_publishedBy_Member implements IComponentRelation<Status, Member> {
	private static final long serialVersionUID = -2716720198122914254L;

	public static final String NAME = "published_by";
	public static final String DESCRIPTION = "A status is published by a Member";

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
	
	
}
