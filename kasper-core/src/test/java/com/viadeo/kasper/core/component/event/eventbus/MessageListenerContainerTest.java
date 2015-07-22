package com.viadeo.kasper.core.component.event.eventbus;

import com.rabbitmq.client.Channel;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.eventbus.MessageListenerContainer;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import org.jboss.logging.MDC;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class MessageListenerContainerTest {

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

    static class Foo extends EventListener<Event> {
        @Override
        public EventResponse handle(Context context, Event event) {
            return EventResponse.success();
        }
    }

    @Test
    public void executeListener_WithEventListenerClass_resetAndPopulateMdc() throws Throwable {

        MDC.put("foo", "bar");

        MessageListenerContainer messageListenerContainer = new MessageListenerContainer(connectionFactory, new Foo());
        messageListenerContainer.setMessageListener(new MessageListener());
        messageListenerContainer.setQueueNames("foo");
        messageListenerContainer.start();
        messageListenerContainer.executeListener(channel, message);

        assertNull("MDC was reseted", MDC.get("foo"));
        assertEquals("MDC <appRoute> contains the fqn of passed event listener", "com.viadeo.kasper.core.component.event.eventbus.MessageListenerContainerTest$Foo", MDC.get("appRoute"));
    }
}