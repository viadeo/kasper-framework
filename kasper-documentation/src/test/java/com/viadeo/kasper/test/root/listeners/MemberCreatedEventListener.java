package com.viadeo.kasper.test.root.listeners;

import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.event.EventMessage;
import com.viadeo.kasper.event.annotation.XKasperEventListener;
import com.viadeo.kasper.test.root.Facebook;
import com.viadeo.kasper.test.root.events.MemberCreatedEvent;

@XKasperEventListener( domain = Facebook.class )
public class MemberCreatedEventListener implements EventListener<MemberCreatedEvent> {
	
	@SuppressWarnings("rawtypes")
	@Override
	public void handle(final org.axonframework.domain.EventMessage arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handle(final EventMessage<MemberCreatedEvent> eventMessage) {
		// TODO Auto-generated method stub
		
	}

}
