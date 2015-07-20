package com.viadeo.kasper.client.platform.bundle.fixture.missplaced;


import com.viadeo.kasper.client.platform.bundle.SpringBundle;
import com.viadeo.kasper.client.platform.bundle.fixture.api.DummyDomain;
import org.springframework.context.ApplicationContext;

import javax.inject.Inject;

public class MissplacedBundle extends SpringBundle {

    @Inject
    public MissplacedBundle(ApplicationContext applicationContext) {
        super(new DummyDomain(), applicationContext);
    }
}
