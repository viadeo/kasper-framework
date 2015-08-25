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
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

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