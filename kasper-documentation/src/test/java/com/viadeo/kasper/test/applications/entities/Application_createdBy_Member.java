package com.viadeo.kasper.test.applications.entities;

import com.viadeo.kasper.IDomain;
import com.viadeo.kasper.IKasperID;
import com.viadeo.kasper.er.IComponentRelation;
import com.viadeo.kasper.er.annotation.XKasperRelation;
import com.viadeo.kasper.locators.IDomainLocator;
import com.viadeo.kasper.test.applications.Applications;
import com.viadeo.kasper.test.root.entities.Member;
import org.axonframework.domain.DomainEventMessage;
import org.axonframework.eventsourcing.AbstractEventSourcedAggregateRoot;
import org.joda.time.DateTime;

@XKasperRelation(domain = Applications.class, label = Application_createdBy_Member.NAME)
public class Application_createdBy_Member implements IComponentRelation<Application, Member> {
	private static final long serialVersionUID = 5614663819769099928L;
	
	public static final String NAME = "created_by";

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
