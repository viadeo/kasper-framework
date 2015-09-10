package com.viadeo.kasper.platform.bundle.fixture;

import com.viadeo.kasper.platform.bundle.SpringBundle;
import com.viadeo.kasper.platform.bundle.fixture.api.DummyDomain;
import org.springframework.context.ApplicationContext;

import javax.inject.Inject;


public class DummySpringBundle extends SpringBundle {

    @Inject
    public DummySpringBundle(ApplicationContext applicationContext) {
        super(new DummyDomain(), applicationContext);
    }
}
