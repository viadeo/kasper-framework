// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.eventbus;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.jayway.awaitility.Awaitility;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.typesafe.config.Config;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.ContextHelper;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.common.serde.ObjectMapperProvider;
import com.viadeo.kasper.core.component.event.eventbus.spring.EventBusConfiguration;
import com.viadeo.kasper.core.component.event.eventbus.spring.RabbitMQConfiguration;
import com.viadeo.kasper.core.component.event.listener.AutowiredEventListener;
import com.viadeo.kasper.core.config.spring.KasperConfiguration;
import com.viadeo.kasper.core.context.spring.KasperContextConfiguration;
import com.viadeo.kasper.core.id.spring.KasperIDConfiguration;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import io.github.fallwizard.rabbitmq.mgmt.RabbitMgmtService;
import io.github.fallwizard.rabbitmq.mgmt.model.Queue;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.serializer.Serializer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.amqp.AmqpIOException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Collection;
import java.util.concurrent.Callable;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        KasperConfiguration.class,
        KasperContextConfiguration.class,
        KasperIDConfiguration.class,
        RabbitMQConfiguration.class,
        EventMessageHandlerITest.TestConfiguration.class,
        EventMessageHandlerITest.OverrideAmqpClusterConfiguration.class
})
@ActiveProfiles(profiles = "rabbitmq")
public class EventMessageHandlerITest {

    public static final int TIMEOUT = 500;

    private static TestEventListener eventListenerWrapper;
    private static final AutowiredEventListener<TestEvent> mockedEventListener = mock(AutowiredEventListener.class);

//    @Rule
//    public FreezeTimeRule freezeTimeRule = new FreezeTimeRule();

    @Inject
    private AMQPCluster cluster;

    @Inject
    private TestMessageConverter messageConverter;

    @Inject
    private RabbitTemplate rabbitTemplate;

    @Inject
    private AMQPComponentNameFormatter nameFormatter;

    @Inject
    private Config config;

    @Inject
    private RabbitAdmin rabbitAdmin;

    @Inject
    private RabbitMgmtService rabbitMgmtService;

    private String clusterName;
    private String exchangeName;
    private String queueName;
    private String deadLetterQueueName;

    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void init() throws Exception {
        KasperMetrics.setMetricRegistry(new MetricRegistry());
        eventListenerWrapper = new TestEventListener(mockedEventListener);
    }

    @Before
    public void setUp() throws Exception {
        clusterName = config.getString("runtime.eventbus.amqp.clusterName");
        exchangeName = nameFormatter.getFullExchangeName(cluster.getExchangeDescriptor().name, cluster.getExchangeDescriptor().version);
        deadLetterQueueName = nameFormatter.getDeadLetterQueueName(exchangeName, clusterName, eventListenerWrapper);
        queueName = nameFormatter.getQueueName(exchangeName, clusterName, eventListenerWrapper);

        try {
            rabbitAdmin.purgeQueue(deadLetterQueueName, false);
            rabbitAdmin.purgeQueue(queueName, false);
        } catch (AmqpIOException e) {
            // happen when the related queues are not existing
        }

        eventListenerWrapper.reset();

        Mockito.reset((AutowiredEventListener) mockedEventListener);
        cluster.subscribe(eventListenerWrapper);
        cluster.start();
    }

    @After
    public void tearDown() throws Exception {
        try {
            cluster.unsubscribe(eventListenerWrapper);
        } catch (Exception e) {
            // happen when we unsubscribe explicitly an event listener in our test
        }
    }

    @Test
    public void handle_withSuccessResponse_isOk() throws InterruptedException {
        // Given
        when(mockedEventListener.handle(any(Context.class), any(TestEvent.class))).thenReturn(EventResponse.success());
        ArgumentCaptor<TestEvent> eventCaptor = ArgumentCaptor.forClass(TestEvent.class);
        TestEvent expectedEvent = new TestEvent();

        // When
        cluster.publish(new GenericEventMessage<>(expectedEvent));

        synchronized (mockedEventListener) {
            mockedEventListener.wait(TIMEOUT);
        }

        // Then
        verify(mockedEventListener).handle(any(Context.class), eventCaptor.capture());
        assertEquals(1, eventCaptor.getAllValues().size());
        assertEquals(expectedEvent, eventCaptor.getValue());
        assertEquals(0, getQueueSize(queueName));
        assertEquals(0, getQueueSize(deadLetterQueueName));
    }

    @Test
    public void handle_withIgnoredResponse_isOk() throws InterruptedException {
        // Given
        when(mockedEventListener.handle(any(Context.class), any(TestEvent.class))).thenReturn(EventResponse.ignored());
        ArgumentCaptor<TestEvent> eventCaptor = ArgumentCaptor.forClass(TestEvent.class);
        TestEvent expectedEvent = new TestEvent();

        // When
        cluster.publish(new GenericEventMessage<>(expectedEvent));

        synchronized (mockedEventListener) {
            mockedEventListener.wait(TIMEOUT);
        }

        // Then
        verify(mockedEventListener).handle(any(Context.class), eventCaptor.capture());
        assertEquals(1, eventCaptor.getAllValues().size());
        assertEquals(expectedEvent, eventCaptor.getValue());
        assertEquals(0, getQueueSize(queueName));
        assertEquals(0, getQueueSize(deadLetterQueueName));
    }

    @Ignore("a weak test")
    @Test
    public void handle_withUnexpectedException_shouldRetry5Times_thenPublishInDeadLetter() throws InterruptedException {
        // Given
        eventListenerWrapper.setExpectedNumberTimes(5);
        doThrow(new RuntimeException("Bazinga!!")).when(mockedEventListener).handle(any(Context.class), any(TestEvent.class));
        ArgumentCaptor<TestEvent> eventCaptor = ArgumentCaptor.forClass(TestEvent.class);
        TestEvent testEvent = new TestEvent();

        // When
        cluster.publish(new GenericEventMessage<>(testEvent));

        synchronized (mockedEventListener) {
            mockedEventListener.wait(TIMEOUT);
        }

        // Then
        verify(mockedEventListener, times(5)).handle(any(Context.class), eventCaptor.capture());
        assertEquals(5, eventCaptor.getAllValues().size());
        for (TestEvent event : eventCaptor.getAllValues()) {
            assertEquals(testEvent, event);
        }

        assertEquals(0, getQueueSize(queueName));
        assertEquals(1, getQueueSize(deadLetterQueueName));

        Message message = rabbitTemplate.receive(nameFormatter.getDeadLetterQueueName(exchangeName, clusterName, eventListenerWrapper));
        assertNotNull(message);
        assertEquals(testEvent, ((GenericEventMessage) messageConverter.fromMessage(message)).getPayload());
    }

    @Test
    public void handle_withFailureResponse_shouldRetry5Times_thenPublishInDeadLetter() throws InterruptedException {
        // Given
        eventListenerWrapper.setExpectedNumberTimes(5);
        when(mockedEventListener.handle(any(Context.class), any(TestEvent.class)))
                .thenReturn(EventResponse.failure(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, new RuntimeException("npe"))));
        ArgumentCaptor<TestEvent> eventCaptor = ArgumentCaptor.forClass(TestEvent.class);
        TestEvent testEvent = new TestEvent();

        // When
        cluster.publish(new GenericEventMessage<>(testEvent));

        synchronized (mockedEventListener) {
            mockedEventListener.wait(TIMEOUT);
        }

        // Then
        verify(mockedEventListener, times(5)).handle(any(Context.class), eventCaptor.capture());
        assertEquals(5, eventCaptor.getAllValues().size());
        for (TestEvent event : eventCaptor.getAllValues()) {
            assertEquals(testEvent, event);
        }

        Awaitility.await()
                .atMost(TIMEOUT, MILLISECONDS)
                .pollDelay(100, MILLISECONDS)
                .until(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return 0 == getQueueSize(queueName);
                    }
                });

        assertEquals(0, getQueueSize(queueName));
        assertEquals(1, getQueueSize(deadLetterQueueName));

        Message message = rabbitTemplate.receive(nameFormatter.getDeadLetterQueueName(exchangeName, clusterName, eventListenerWrapper));
        assertNotNull(message);
        assertEquals(testEvent, ((GenericEventMessage) messageConverter.fromMessage(message)).getPayload());
    }

    @Test
    public void handle_withErrorResponse_isOk() throws InterruptedException {
        // Given
        when(mockedEventListener.handle(any(Context.class), any(TestEvent.class))).thenReturn(EventResponse.error(new KasperReason(CoreReasonCode.INVALID_ID)));
        ArgumentCaptor<TestEvent> eventCaptor = ArgumentCaptor.forClass(TestEvent.class);
        TestEvent expectedEvent = new TestEvent();

        // When
        cluster.publish(new GenericEventMessage<>(expectedEvent));

        synchronized (mockedEventListener) {
            mockedEventListener.wait(TIMEOUT);
        }

        // Then
        verify(mockedEventListener).handle(any(Context.class), eventCaptor.capture());
        assertEquals(1, eventCaptor.getAllValues().size());
        assertEquals(expectedEvent, eventCaptor.getValue());
        assertEquals(0, getQueueSize(queueName));
        assertEquals(0, getQueueSize(deadLetterQueueName));
    }

    @Ignore("a weak test")
    @Test
    public void handle_withTemporarilyUnavailableResponse_shouldRetry5Times_thenRequeue() throws InterruptedException {
        // Given
        eventListenerWrapper.setExpectedNumberTimes(5);
        when(mockedEventListener.handle(any(Context.class), any(TestEvent.class)))
                .thenReturn(EventResponse.temporarilyUnavailable(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_TIMEOUT, new RuntimeException("timeout"))));
        ArgumentCaptor<TestEvent> eventCaptor = ArgumentCaptor.forClass(TestEvent.class);
        TestEvent expectedEvent = new TestEvent();

        // When
        cluster.publish(new GenericEventMessage<>(expectedEvent));

        // ...we un-subscribe the event listener in order to not consume the requeued event
        cluster.unsubscribe(eventListenerWrapper);

        synchronized (mockedEventListener) {
            mockedEventListener.wait(TIMEOUT);
        }

        // Then
        verify(mockedEventListener, times(5)).handle(any(Context.class), eventCaptor.capture());
        assertEquals(5, eventCaptor.getAllValues().size());
        for (TestEvent event : eventCaptor.getAllValues()) {
            assertEquals(expectedEvent, event);
        }

        Awaitility.await()
                .atMost(2, SECONDS)
                .pollDelay(200, MILLISECONDS)
                .until(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return 1 == getMessageSize(queueName);
                    }
                });

        assertEquals(1, getMessageSize(queueName));
        assertEquals(0, getQueueSize(deadLetterQueueName));

        // Clean
        try {
            rabbitAdmin.declareQueue(new org.springframework.amqp.core.Queue(queueName));
        } catch (AmqpIOException e) {
            // happen when the related queues are not existing
        }
    }

    @Test
    public void handle_withTemporarilyUnavailableResponse_withExpiredMessage_shouldRetry5Times_thenPublishInDeadLetter() throws InterruptedException {
        // Given
        eventListenerWrapper.setExpectedNumberTimes(5);
        messageConverter.setExpired();
        when(mockedEventListener.handle(any(Context.class), any(TestEvent.class)))
                .thenReturn(EventResponse.temporarilyUnavailable(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_TIMEOUT, new RuntimeException("timeout"))));
        ArgumentCaptor<TestEvent> eventCaptor = ArgumentCaptor.forClass(TestEvent.class);
        TestEvent expectedEvent = new TestEvent();

        // When
        cluster.publish(new GenericEventMessage<>(expectedEvent));

        synchronized (mockedEventListener) {
            mockedEventListener.wait(TIMEOUT);
        }

        // Then
        verify(mockedEventListener, times(5)).handle(any(Context.class), eventCaptor.capture());
        assertEquals(5, eventCaptor.getAllValues().size());
        for (TestEvent event : eventCaptor.getAllValues()) {
            assertEquals(expectedEvent, event);
        }
        assertEquals(expectedEvent, eventCaptor.getValue());

        Awaitility.await()
                .atMost(TIMEOUT, MILLISECONDS)
                .pollDelay(100, MILLISECONDS)
                .until(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return 0 == getQueueSize(queueName);
                    }
                });

        assertEquals(0, getQueueSize(queueName));
        assertEquals(1, getQueueSize(deadLetterQueueName));
    }


    protected int getQueueSize(final String name) {
        return rabbitAdmin.getRabbitTemplate().execute(new ChannelCallback<AMQP.Queue.DeclareOk>() {
            public AMQP.Queue.DeclareOk doInRabbit(Channel channel) throws Exception {
                return channel.queueDeclarePassive(name);
            }
        }).getMessageCount();
    }

    protected int getMessageSize(final String name) {
        int messageSize = 0;
        Optional<Collection<Queue>> collectionOptional = rabbitMgmtService.queues().allOnVHost("/");
        if (collectionOptional.isPresent()) {
            Queue queue = Maps.uniqueIndex(collectionOptional.get(), new Function<Queue, String>() {
                @Override
                public String apply(Queue input) {
                    return input.getName();
                }
            }).get(name);

            messageSize = (int) queue.getMessagesReady() + (int) queue.getMessagesUnacknowledged();
        }
        return messageSize;
    }

    @Configuration
    public static class OverrideAmqpClusterConfiguration extends EventBusConfiguration.AmqpClusterConfiguration {
        @Bean
        public MessageListenerContainerManager messageListenerContainerManager(
                final Config config,
                final MetricRegistry metricRegistry,
                final MessageListenerContainerFactory messageListenerContainerFactory,
                final MessageConverter messageConverter,
                final MessageRecoverer messageRecoverer,
                final MessageListenerContainerController messageListenerContainerController
        ) {
            DefaultMessageListenerContainerManager containerManager = new DefaultMessageListenerContainerManager(
                    messageListenerContainerFactory,
                    metricRegistry,
                    messageConverter,
                    messageListenerContainerController
            ) {
                @Override
                protected Object createDelegateMessageListener(MessageConverter messageConverter, org.axonframework.eventhandling.EventListener eventListener, MetricRegistry metricRegistry, boolean enabledMessageHandling) {
                    return new EventMessageHandler(
                            messageConverter,
                            (AutowiredEventListener) eventListener,
                            metricRegistry,
                            messageRecoverer
                    );
                }
            };
            containerManager.setEnabledMessageHandling(config.getBoolean("runtime.eventbus.amqp.enableListeners"));
            return containerManager;
        }

        @Bean
        public TestMessageConverter messageConverter(ContextHelper contextHelper, ObjectMapper objectMapper) {
            return new TestMessageConverter(contextHelper, createEventMessageSerializer(objectMapper));
        }

    }

    @Configuration
    public static class TestConfiguration {
        @Bean
        public MetricRegistry metricRegistry() {
            return new MetricRegistry();
        }

        @Bean
        public ObjectMapper objectMapper() {
            return ObjectMapperProvider.INSTANCE.mapper();
        }
    }

    public static class TestMessageConverter extends EventBusMessageConverter {

        private boolean expired;

        public TestMessageConverter(final ContextHelper contextHelper, final Serializer serializer) {
            super(contextHelper, serializer);
        }

        @Override
        public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
            final EventMessage eventMessage = (EventMessage) object;
            final DateTime expiredDate = expired ? eventMessage.getTimestamp().minusHours(5) : eventMessage.getTimestamp();
            final GenericEventMessage<Object> expiredEventMessage = new GenericEventMessage<>(
                    eventMessage.getIdentifier(),
                    expiredDate,
                    eventMessage.getPayload(),
                    eventMessage.getMetaData()
            );

            return super.toMessage(expiredEventMessage, messageProperties);
        }

        public void setExpired() {
            expired = true;
        }
    }

    public static class TestEvent implements Event {

        private final DateTime dateTime;

        public TestEvent() {
            this(DateTime.now());
        }

        @JsonCreator
        public TestEvent(@JsonProperty("dateTime") DateTime dateTime) {
            this.dateTime = dateTime.toDateTime(DateTimeZone.UTC);
        }

        public DateTime getDateTime() {
            return dateTime;
        }

        @Override
        public int hashCode() {
            return 31 * super.hashCode() + Objects.hashCode(dateTime);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final TestEvent other = (TestEvent) obj;
            return Objects.equal(this.dateTime, other.dateTime);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("dateTime", dateTime)
                    .toString();
        }
    }

    public static class TestEventListener extends AutowiredEventListener<TestEvent> {

        private final AutowiredEventListener<TestEvent> eventListener;
        private int expectedNumberTimes;
        private int actualNumberTimes;

        public TestEventListener(AutowiredEventListener<TestEvent> eventListener) {
            this.eventListener = eventListener;
            this.expectedNumberTimes = 1;
            this.actualNumberTimes = 0;
        }

        @Override
        public EventResponse handle(Context context, TestEvent event) {
            EventResponse response;
            actualNumberTimes++;

            if (actualNumberTimes == expectedNumberTimes) {
                response = eventListener.handle(context, event);
                synchronized (eventListener) {
                    eventListener.notify();
                }
            } else {
                response = eventListener.handle(context, event);
            }

            return response;
        }

        public void setExpectedNumberTimes(int expectedNumberTimes) {
            this.expectedNumberTimes = expectedNumberTimes;
        }

        public void reset() {
            this.actualNumberTimes = 0;
        }
    }

}
