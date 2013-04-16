package com.viadeo.kasper.test.root.listeners;

import org.axonframework.domain.EventMessage;

import com.viadeo.kasper.event.IEventListener;
import com.viadeo.kasper.event.IEventMessage;
import com.viadeo.kasper.event.annotation.XKasperEventListener;
import com.viadeo.kasper.test.root.events.MemberCreatedEvent;

@XKasperEventListener
public class MemberCreatedEventListener implements IEventListener<MemberCreatedEvent> {
	
	@SuppressWarnings("rawtypes")
	@Override
	public void handle(final EventMessage arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handle(final IEventMessage<MemberCreatedEvent> eventMessage) {
		// TODO Auto-generated method stub
		
	}

}
