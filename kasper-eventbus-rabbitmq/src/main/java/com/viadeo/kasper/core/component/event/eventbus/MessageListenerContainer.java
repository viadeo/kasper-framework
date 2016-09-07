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
package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.rabbitmq.client.Channel;
import org.apache.log4j.MDC;
import org.axonframework.eventhandling.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import static com.google.common.base.Preconditions.checkNotNull;

public class MessageListenerContainer extends SimpleMessageListenerContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListenerContainer.class);

    private final EventListener eventListener;
    private final EventBusPolicy eventBusPolicy;

    public MessageListenerContainer(
            final EventBusPolicy eventBusPolicy,
            final ConnectionFactory connectionFactory,
            final EventListener eventListener
    ) {
        super(connectionFactory);
        this.eventBusPolicy = eventBusPolicy;
        this.eventListener = checkNotNull(eventListener);
    }

    @Override
    public void start() {
        if (eventBusPolicy == EventBusPolicy.NORMAL) {
            LOGGER.debug("starting message listener container on {}", Lists.newArrayList(getQueueNames()));
            super.start();
        } else {
            LOGGER.debug(
                    "The event bus policy do not allow to start the events consumption, <listener={}> <policy={}>",
                    eventListener.getClass().getName(), eventBusPolicy
            );
        }
    }

    @Override
    protected void executeListener(Channel channel, Message message) throws Throwable {
        MDC.clear();
        MDC.put("appRoute", eventListener.getClass().getName());

        super.executeListener(channel, message);
    }

    @Override
    public void stop() {
        LOGGER.debug("stopping message listener container on {}", Lists.newArrayList(getQueueNames()));
        super.stop();
    }

    @Override
    protected void invokeListener(Channel channel, Message message) throws Exception {
        checkNotNull(message);
        super.invokeListener(channel, new EventBusMessage(message, eventListener.getClass()));
    }

    public EventListener getEventListener() {
        return eventListener;
    }

    @VisibleForTesting
    @Override
    protected void doStart() throws Exception {
        super.doStart();
    }
}
