package com.viadeo.kasper.test.root.listeners;

import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.event.annotation.XKasperEventListener;
import com.viadeo.kasper.test.root.Facebook;
import com.viadeo.kasper.test.root.events.MemberCreatedEvent;

@XKasperEventListener( domain = Facebook.class )
public class MemberCreatedEventListener extends EventListener<MemberCreatedEvent> {
	
}
