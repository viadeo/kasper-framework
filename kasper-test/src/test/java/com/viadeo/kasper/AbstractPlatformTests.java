package com.viadeo.kasper;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.viadeo.kasper.context.IContext;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.platform.IPlatform;

public abstract class AbstractPlatformTests {

    private static ApplicationContext context = null;

    private static IPlatform staticPlatform = null;
    private IPlatform platform = null;

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
            staticPlatform = context.getBean(IPlatform.class);
            staticPlatform.boot();
        }

        if (uniquePlatform) {
            platform = staticPlatform;
        } else {
            platform = context.getBean(IPlatform.class);
            platform.boot();
        }

    }

    // ------------------------------------------------------------------------

    protected IPlatform getPlatform() {
        return this.platform;
    }

    // ------------------------------------------------------------------------

    protected IContext newContext() {
        return new DefaultContextBuilder().buildDefault();
    }

}
