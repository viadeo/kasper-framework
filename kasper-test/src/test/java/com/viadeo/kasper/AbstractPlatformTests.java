package com.viadeo.kasper;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.platform.Platform;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
            System.setProperty("spring_files", "classpath*:config/spring/cache/spring-cache-*.xml");
            context = new ClassPathXmlApplicationContext("spring/kasper/kasper-platform.xml");
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
