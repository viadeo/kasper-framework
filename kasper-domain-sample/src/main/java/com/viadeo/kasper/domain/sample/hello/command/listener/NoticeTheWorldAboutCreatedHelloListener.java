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
package com.viadeo.kasper.domain.sample.hello.command.listener;

import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.annotation.XKasperEventListener;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.event.listener.AutowiredEventListener;
import com.viadeo.kasper.domain.sample.hello.api.HelloDomain;
import com.viadeo.kasper.domain.sample.hello.api.command.NoticeTheWorldCommand;
import com.viadeo.kasper.domain.sample.hello.api.event.BuddyWasUnableToRespondErrorEvent;
import com.viadeo.kasper.domain.sample.hello.api.event.HelloCreatedEvent;

import javax.inject.Inject;

@XKasperEventListener(
        domain = HelloDomain.class,
        description = "Notice the world for each created Hello message"
)
public class NoticeTheWorldAboutCreatedHelloListener extends AutowiredEventListener<HelloCreatedEvent> {

    private CommandGateway commandGateway;
    private KasperEventBus eventBus;

    @Inject
    public NoticeTheWorldAboutCreatedHelloListener(CommandGateway commandGateway, KasperEventBus eventBus) {
        this.commandGateway = commandGateway;
        this.eventBus = eventBus;
    }

    @Override
    public EventResponse handle(final Context context, final HelloCreatedEvent event) {
        final String response = String.format(
            "Hi all, %s received an hello message",
            event.getForBuddy()
        );

        try {

            /**
             * Classical pattern : send a command from a command listener
             */
            commandGateway.sendCommand(
                    new NoticeTheWorldCommand(response),
                /* forward the current context */
                    context
            );

        } catch (final Exception e) {

            /**
             * Will perhaps be replaced by this.publish() in some time
             */
            eventBus.publish(
                    context,
                    new BuddyWasUnableToRespondErrorEvent(
                            event.getEntityId(),
                            response
                    )
            );

        }

        return EventResponse.success();
    }

}
