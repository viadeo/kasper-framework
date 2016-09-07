// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
package com.viadeo.kasper.spring.platform;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.ConfigFactory;
import com.viadeo.kasper.platform.builder.PlatformContext;
import com.viadeo.kasper.platform.bundle.fixture.DummySpringBundle;
import com.viadeo.kasper.platform.bundle.fixture.command.handler.DummyCommandHandler;
import com.viadeo.kasper.platform.bundle.fixture.command.listener.DummyCommandListener;
import com.viadeo.kasper.platform.bundle.fixture.infra.DummyBackend;
import com.viadeo.kasper.spring.core.ConfigPropertySource;
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
    private PlatformContext platformContext;
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

        SpringBundle springBundle = new DummySpringBundle(context);
        springBundle.configure(platformContext);

        // command
        ApplicationContext command = springBundle.getCommandContext();
        assertNotNull(command.getBeansOfType(DummyBackend.class));
        assertFalse(command.containsBean("missplacedBean"));
        assertTrue(command.containsBean("commandBean"));
        assertTrue(command.containsBean("dummyCommandHandler"));
        assertFalse(command.containsBean("dummyCommandUnregisteredHandler"));
        assertTrue(command.containsBean("dummyCommandListener"));
        assertEquals(1, command.getBean(DummyCommandHandler.class).getFoo());

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
        springBundle.configure(platformContext);
    }
}