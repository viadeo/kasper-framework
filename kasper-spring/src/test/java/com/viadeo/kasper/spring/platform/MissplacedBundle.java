package com.viadeo.kasper.spring.platform;


import com.viadeo.kasper.platform.bundle.fixture.api.DummyDomain;
import org.springframework.context.ApplicationContext;

import javax.inject.Inject;

public class MissplacedBundle extends SpringBundle {

    @Inject
    public MissplacedBundle(ApplicationContext applicationContext) {
        super(new DummyDomain(), applicationContext);
    }
}
