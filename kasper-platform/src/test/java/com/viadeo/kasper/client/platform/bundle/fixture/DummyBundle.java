package com.viadeo.kasper.client.platform.bundle.fixture;

import com.viadeo.kasper.client.platform.bundle.SpringBundle;
import com.viadeo.kasper.client.platform.bundle.fixture.api.DummyDomain;
import org.springframework.context.ApplicationContext;

import javax.inject.Inject;


public class DummyBundle extends SpringBundle {

    @Inject
    public DummyBundle(ApplicationContext applicationContext) {
        super(new DummyDomain(), applicationContext);
    }
}
