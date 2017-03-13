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

import com.rabbitmq.client.Channel;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.listener.AutowiredEventListener;
import org.jboss.logging.MDC;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MessageListenerContainerUTest {

    @Mock
    private ConnectionFactory connectionFactory;

    @Mock
    private Message message;

    @Mock
    private Channel channel;

    static class MessageListener implements org.springframework.amqp.core.MessageListener {
        @Override
        public void onMessage(Message message) {

        }
    }

    static class Foo extends AutowiredEventListener<Event> {
        @Override
        public EventResponse handle(Context context, Event event) {
            return EventResponse.success();
        }
    }

    @Test
    public void start_a_message_listener_container_with_a_normal_policy_is_started() throws Exception {
        MessageListenerContainer messageListenerContainer = spy(new MessageListenerContainer(EventBusPolicy.NORMAL, connectionFactory, new Foo()));
        messageListenerContainer.setMessageListener(new MessageListener());
        messageListenerContainer.setQueueNames("foo");
        messageListenerContainer.start();
        verify(messageListenerContainer).doStart();
    }

    @Test
    public void start_a_message_listener_container_with_only_publish_policy_is_not_started() throws Exception {
        MessageListenerContainer messageListenerContainer = spy(new MessageListenerContainer(EventBusPolicy.ONLY_PUBLISH, connectionFactory, new Foo()));
        messageListenerContainer.setMessageListener(new MessageListener());
        messageListenerContainer.setQueueNames("foo");
        messageListenerContainer.start();
        verify(messageListenerContainer, never()).doStart();
    }

    @Test
    public void executeListener_WithEventListenerClass_resetAndPopulateMdc() throws Throwable {

        MDC.put("foo", "bar");

        MessageListenerContainer messageListenerContainer = new MessageListenerContainer(EventBusPolicy.NORMAL, connectionFactory, new Foo());
        messageListenerContainer.setMessageListener(new MessageListener());
        messageListenerContainer.setQueueNames("foo");
        messageListenerContainer.start();
        messageListenerContainer.executeListener(channel, message);

        assertNull("MDC was reseted", MDC.get("foo"));
        assertEquals("MDC <appRoute> contains the fqn of passed event listener", "com.viadeo.kasper.core.component.event.eventbus.MessageListenerContainerUTest$Foo", MDC.get("appRoute"));
    }
}
