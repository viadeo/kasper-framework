package com.viadeo.kasper.test.root.events;

import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.test.root.Facebook;

@XKasperEvent(action = "now_connected_to")
public class NewMemberConnectionEvent extends FacebookMemberEvent {
	private static final long serialVersionUID = -4357030340252792722L;

}
