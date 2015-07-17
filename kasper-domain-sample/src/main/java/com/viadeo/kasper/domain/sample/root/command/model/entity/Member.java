// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.root.command.model.entity;

import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.component.command.aggregate.annotation.XKasperConcept;
import com.viadeo.kasper.domain.sample.root.api.Facebook;
import com.viadeo.kasper.domain.sample.root.api.event.MemberCreatedEvent;
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