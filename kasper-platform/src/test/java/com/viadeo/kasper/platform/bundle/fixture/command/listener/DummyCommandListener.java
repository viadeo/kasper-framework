package com.viadeo.kasper.platform.bundle.fixture.command.listener;

import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.annotation.XKasperEventListener;
import com.viadeo.kasper.core.component.event.listener.AutowiredEventListener;
import com.viadeo.kasper.platform.bundle.fixture.api.DummyDomain;
import com.viadeo.kasper.platform.bundle.fixture.api.DummyEvent;

@XKasperEventListener(description = "test", domain = DummyDomain.class)
public class DummyCommandListener extends AutowiredEventListener<DummyEvent> {
    @Override
    public EventResponse handle(Context context, DummyEvent event) {
        return EventResponse.success();
    }
}
