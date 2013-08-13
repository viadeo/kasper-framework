package com.viadeo.kasper.test.root.events;

import com.viadeo.kasper.event.annotation.XKasperEvent;

@XKasperEvent(action = "created")
public class MemberCreatedEvent extends FacebookMemberEvent {
	private static final long serialVersionUID = -3530058587014151484L;

}
