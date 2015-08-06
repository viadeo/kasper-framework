package com.viadeo.kasper.platform.bundle;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.ConfigFactory;
import com.viadeo.kasper.core.config.ConfigPropertySource;
import com.viadeo.kasper.platform.builder.BuilderContext;
import com.viadeo.kasper.platform.bundle.fixture.DummyBundle;
import com.viadeo.kasper.platform.bundle.fixture.command.handler.DummyCommandHandler;
import com.viadeo.kasper.platform.bundle.fixture.command.listener.DummyCommandListener;
import com.viadeo.kasper.platform.bundle.fixture.infra.DummyBackend;
import com.viadeo.kasper.platform.bundle.fixture.missplaced.MissplacedBundle;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SpringBundleUTest {

    @Mock
    private BuilderContext builderContext;
    private AnnotationConfigApplicationContext context;

    @Before
    public void setUp() throws Exception {

        context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addLast(new ConfigPropertySource(ConfigFactory.parseMap(ImmutableMap.of("foo", 1)), "typesafeconfig"));
    }

    @Test
    public void init_withParentContext_createsAnIsolatedContext() throws Exception {

        context.refresh();
        context.register(DummyBackend.class);

        SpringBundle springBundle = new DummyBundle(context);
        springBundle.configure(builderContext);

        // command
        ApplicationContext command = springBundle.getCommandContext();
        assertNotNull(command.getBeansOfType(DummyBackend.class));
        assertFalse(command.containsBean("missplacedBean"));
        assertTrue(command.containsBean("commandBean"));
        assertTrue(command.containsBean("dummyCommandHandler"));
        assertFalse(command.containsBean("dummyCommandUnregisteredHandler"));
        assertTrue(command.containsBean("dummyCommandListener"));
        Assert.assertEquals(1, command.getBean(DummyCommandHandler.class).getFoo());

        // query
        ApplicationContext query = springBundle.getQueryContext();
        assertFalse(query.containsBean("missplacedBean"));
        assertTrue(query.containsBean("queryBean"));
        assertTrue(query.containsBean("dummyQueryHandler"));
        assertFalse(query.containsBean("dummyQueryUnregisteredHandler"));
        assertTrue(query.containsBean("dummyQueryListener"));

        assertEquals(1, springBundle.getCommandHandlers().size());
        assertEquals(1, springBundle.getQueryHandlers().size());
        assertEquals(2, springBundle.getEventListeners().size());
        assertTrue(springBundle.getEventListeners().get(0) instanceof DummyCommandListener);


    }

    @Test(expected = IllegalStateException.class)
    public void init_withMissPlacedBundle_ThrowsIllegalStateException() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        SpringBundle springBundle = new MissplacedBundle(context);
        springBundle.configure(builderContext);
    }
}