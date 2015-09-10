package com.viadeo.kasper.platform.bundle.fixture.query.listener;

import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.annotation.XKasperEventListener;
import com.viadeo.kasper.core.component.event.listener.QueryEventListener;
import com.viadeo.kasper.platform.bundle.fixture.api.DummyDomain;
import com.viadeo.kasper.platform.bundle.fixture.api.DummyEvent;

@XKasperEventListener(description = "dummy", domain = DummyDomain.class)
public class DummyQueryListener extends QueryEventListener<DummyEvent> {
    @Override
    public EventResponse handle(Context context, DummyEvent event) {
        return EventResponse.success();
    }
}
