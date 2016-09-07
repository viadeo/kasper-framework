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
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring.core;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.core.component.annotation.XKasperSaga;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.SagaIdReconciler;
import com.viadeo.kasper.core.component.event.saga.exception.SagaInstantiationException;
import com.viadeo.kasper.core.component.event.saga.factory.DefaultSagaFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.context.support.GenericApplicationContext;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class DefaultSpringSagaFactoryUTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    public GenericApplicationContext applicationContext;
    public DefaultSagaFactory factory;

    @Before
    public void setUp() throws Exception {
        applicationContext = new GenericApplicationContext();
        factory = new DefaultSpringSagaFactory(applicationContext);
    }

    @Test
    public void create_withoutConstructor_isOk() {
        // When
        SagaWithoutConstructor instance = factory.create(UUID.randomUUID().toString(), SagaWithoutConstructor.class);

        // Then
        assertNotNull(instance);
    }

    @Test
    public void create_withoutParametersInConstructor_isOk() {
        // When
        SagaWithConstructor instance = factory.create(UUID.randomUUID().toString(), SagaWithConstructor.class);

        // Then
        assertNotNull(instance);
    }

    @Test
    public void create_withParametersInConstructor_isOk() {
        // Given
        CommandGateway commandGateway = mock(CommandGateway.class);
        applicationContext.getBeanFactory().registerSingleton("commandGateway", commandGateway);

        // When
        SagaWithParameterInConstructor instance = factory.create(UUID.randomUUID().toString(), SagaWithParameterInConstructor.class);

        // Then
        assertNotNull(instance);
        assertEquals(commandGateway, instance.getCommandGateway());
    }

    @Test
    public void create_withParametersInConstructor_withNoCandidates_isOk() {
        // Then
        expectedException.expect(SagaInstantiationException.class);
        expectedException.expectMessage(String.format(
                "Error instantiating saga of '%s'",
                SagaWithParameterInConstructor.class.getName()
        ));

        // When
        factory.create(UUID.randomUUID().toString(), SagaWithParameterInConstructor.class);
    }

    class TestDomain implements Domain {}

    static abstract class AbstractSaga implements Saga {
        @Override
        public Optional<SagaIdReconciler> getIdReconciler() {
            return Optional.absent();
        }
    }

    @XKasperSaga(domain = TestDomain.class)
    public static class SagaWithoutConstructor extends AbstractSaga {}

    @XKasperSaga(domain = TestDomain.class)
    public static class SagaWithConstructor extends AbstractSaga {
        public SagaWithConstructor() {}
    }

    @XKasperSaga(domain = TestDomain.class)
    static class SagaWithParameterInConstructor extends AbstractSaga {
        private final CommandGateway commandGateway;

        public SagaWithParameterInConstructor(final CommandGateway commandGateway) {
            this.commandGateway = commandGateway;
        }

        public CommandGateway getCommandGateway() {
            return commandGateway;
        }
    }
}
