package com.viadeo.kasper.platform.bundle.fixture.api;

import com.viadeo.kasper.api.annotation.XKasperDomain;
import com.viadeo.kasper.api.component.Domain;

@XKasperDomain(
        label = "Article",
        prefix = "article",
        description = "Article domain"
)
public class DummyDomain implements Domain {
}