package com.viadeo.kasper.platform.bundle.fixture.query.handler;

import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.component.query.AutowiredQueryHandler;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.platform.bundle.fixture.api.DummyDomain;
import com.viadeo.kasper.platform.bundle.fixture.api.DummyQuery;
import com.viadeo.kasper.platform.bundle.fixture.api.DummyQueryResult;
import com.viadeo.kasper.platform.bundle.fixture.api.DummyUnregisteredQuery;

@XKasperQueryHandler(description = "dummy", domain = DummyDomain.class)
@XKasperUnregistered
public class DummyUnregisteredQueryHandler extends AutowiredQueryHandler<DummyUnregisteredQuery, DummyQueryResult> {
}
