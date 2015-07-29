package com.viadeo.kasper.platform.bundle.fixture.query.handler;

import com.viadeo.kasper.core.component.query.AutowiredQueryHandler;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.platform.bundle.fixture.api.DummyDomain;
import com.viadeo.kasper.platform.bundle.fixture.api.DummyQuery;
import com.viadeo.kasper.platform.bundle.fixture.api.DummyQueryResult;

@XKasperQueryHandler(description = "dummy", domain = DummyDomain.class)
public class DummyQueryHandler extends AutowiredQueryHandler<DummyQuery, DummyQueryResult> {
}
