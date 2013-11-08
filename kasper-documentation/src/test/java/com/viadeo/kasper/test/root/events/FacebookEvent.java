package com.viadeo.kasper.test.root.events;

import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.event.domain.impl.AbstractDomainEvent;
import com.viadeo.kasper.test.root.Facebook;

public class FacebookEvent extends AbstractDomainEvent<Facebook> {
	private static final long serialVersionUID = 3546039505852588248L;


    protected FacebookEvent() {
        super(DefaultContextBuilder.get());
    }
}
