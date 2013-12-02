package com.viadeo.kasper.test.root.entities;

import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.er.annotation.XKasperConcept;
import com.viadeo.kasper.test.root.Facebook;
import com.viadeo.kasper.test.root.events.MemberCreatedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;

@XKasperConcept(domain = Facebook.class, label = Member.NAME)
public class Member extends Concept {
	private static final long serialVersionUID = 2514520954354227657L;

	public static final String NAME = "FacebookMember";
	
	// ------------------------------------------------------------------------
	
	@EventHandler
	public void handleCreatedEvent(final MemberCreatedEvent event) {
		
	}
	
}
