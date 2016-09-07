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
package com.viadeo.kasper.core.component.command.gateway;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.context.Tags;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.core.component.command.AutowiredCommandHandler;
import com.viadeo.kasper.core.component.command.CommandMessage;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.axonframework.unitofwork.DefaultUnitOfWorkFactory;
import org.axonframework.unitofwork.UnitOfWorkFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class KasperCommandGatewayITest {

    static {
        // pre-load Tags class, to initialize its fields
        try {
            Class.forName(Tags.class.getName());
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private KasperCommandGateway commandGateway;

    // ------------------------------------------------------------------------

    public static class TestCommand implements Command {
    }

    // ------------------------------------------------------------------------

    @Before
    public void setUp() throws Exception {
        final MetricRegistry metricRegistry = new MetricRegistry();
        final UnitOfWorkFactory uowFactory = new DefaultUnitOfWorkFactory();
        final KasperCommandBus commandBus = new KasperCommandBus(metricRegistry);
        commandBus.setUnitOfWorkFactory(uowFactory);

        this.commandGateway = new KasperCommandGateway(commandBus);

        KasperMetrics.setMetricRegistry(metricRegistry);
    }

    // ------------------------------------------------------------------------

    final Object lock = new Object();

    @Test
    public void sendCommand_isOk() throws Exception {
        // Given
        final List<Command> captor = Lists.newArrayList();

        commandGateway.register(new AutowiredCommandHandler<TestCommand>() {
            @Override
            public CommandResponse handle(final CommandMessage message) {
                captor.add(message.getCommand());
                synchronized (lock) {
                    lock.notify();
                }
                return CommandResponse.ok();
            }
        });

        final Command command = new TestCommand();

        // When
        commandGateway.sendCommand(command, Contexts.empty());

        synchronized (lock) {
            lock.wait(500);
        }

        // Then
        assertEquals(1, captor.size());
        assertEquals(command, captor.get(0));
    }

    @Test(timeout = 100)
    public void sendCommand_shouldFireAndForget() throws Exception {
        // Given
        commandGateway.register(new AutowiredCommandHandler<TestCommand>() {
            @Override
            public CommandResponse handle(CommandMessage message) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    return CommandResponse.error(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, e));
                }
                return CommandResponse.ok();
            }
        });
        final Command command = new TestCommand();

        // When
        commandGateway.sendCommand(command, Contexts.empty());

        // Then forget the call
    }

}
