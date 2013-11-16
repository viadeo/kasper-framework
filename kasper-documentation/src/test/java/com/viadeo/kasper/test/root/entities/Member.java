package com.viadeo.kasper.test.root.entities;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.er.RootConcept;
import com.viadeo.kasper.er.annotation.XKasperConcept;
import com.viadeo.kasper.test.root.Facebook;
import com.viadeo.kasper.test.root.events.MemberCreatedEvent;
import org.axonframework.domain.DomainEventStream;
import org.axonframework.domain.EventRegistrationCallback;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.joda.time.DateTime;

@XKasperConcept(domain = Facebook.class, label = Member.NAME)
public class Member implements RootConcept {
	private static final long serialVersionUID = 2514520954354227657L;

	public static final String NAME = "FacebookMember";
	
	// ------------------------------------------------------------------------
	
	@EventHandler
	public void handleCreatedEvent(final MemberCreatedEvent event) {
		
	}
	
	// ------------------------------------------------------------------------
	
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

    @Override
    public void setVersion(Long version) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public KasperID getEntityId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
