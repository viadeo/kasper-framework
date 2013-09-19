// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper;

import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.configuration.DefaultPlatformSpringConfiguration;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public abstract class AbstractPlatformTests {

    private static ApplicationContext context = null;

    private static Platform staticPlatform = null;
    private Platform platform = null;

    // ------------------------------------------------------------------------

    public AbstractPlatformTests() {
        this(true);
    }

    // ------------------------------------------------------------------------

    public AbstractPlatformTests(final boolean uniquePlatform) {

        if (null == context) {
            context = new AnnotationConfigApplicationContext(DefaultPlatformSpringConfiguration.class);
        }

        if (null == staticPlatform) {
            staticPlatform = context.getBean(Platform.class);
            staticPlatform.boot();
        }

        if (uniquePlatform) {
            platform = staticPlatform;
        } else {
            platform = context.getBean(Platform.class);
            platform.boot();
        }

    }

    // ------------------------------------------------------------------------

    protected Platform getPlatform() {
        return this.platform;
    }

    // ------------------------------------------------------------------------

    protected Context newContext() {
        return new DefaultContextBuilder().build();
    }

}
