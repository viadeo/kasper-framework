// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello;

import com.viadeo.kasper.domain.sample.hello.api.HelloDomain;
import com.viadeo.kasper.platform.bundle.SpringBundle;
import org.springframework.context.ApplicationContext;

import javax.inject.Inject;

public class HelloBundle extends SpringBundle {

    @Inject
    public HelloBundle(ApplicationContext context) {
        super(new HelloDomain(), context);
    }
}
