package com.viadeo.kasper.test.root.events;

import com.viadeo.kasper.event.annotation.XKasperEvent;

@XKasperEvent(action = "now_connected_to")
public class NewMemberConnectionEvent implements FacebookMemberEvent {
	private static final long serialVersionUID = -4357030340252792722L;

}
