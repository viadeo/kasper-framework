package com.viadeo.kasper.core.component.eventbus;

import com.google.common.base.Optional;
import org.junit.Test;

import static org.junit.Assert.*;

public class AMQPComponentNameFormatterUTest {

    private AMQPComponentNameFormatter amqpComponentNameFormatter = new AMQPComponentNameFormatter();

    @Test
    public void extractQueueInfo_withNotMatchingQueueFormat_returnAbsent() {
        // Given
        String queueName = "miaou";

        // When
        Optional<QueueInfo> optionalClassName = amqpComponentNameFormatter.extractQueueInfo(queueName);

        // Then
        assertNotNull(optionalClassName);
        assertFalse(optionalClassName.isPresent());
    }

    @Test
    public void extractQueueInfo_withMatchingQueueFormat_returnClassName() {
        // Given
        String queueName = "exchangeName_clusterName_MyEventListenerClassName";

        // When
        Optional<QueueInfo> optionalClassName = amqpComponentNameFormatter.extractQueueInfo(queueName);

        // Then
        assertNotNull(optionalClassName);
        assertTrue(optionalClassName.isPresent());

        QueueInfo actual = optionalClassName.get();
        assertEquals("MyEventListenerClassName", actual.getEventListenerClassName());
        assertEquals("exchangeName", actual.getExchangeName());
    }

    @Test
    public void extractQueueInfo_withoutRespectNamingConvention_returnClassName() {
        // Given
        String queueName = "exchangeName_clusterName_com.viadeo.platform.letsmeet.query.listener.IndexMemberToMeet_on_LetsMeetPropositionAccepted_EventListener";

        // When
        Optional<QueueInfo> optionalClassName = amqpComponentNameFormatter.extractQueueInfo(queueName);

        // Then
        assertNotNull(optionalClassName);
        assertTrue(optionalClassName.isPresent());

        QueueInfo actual = optionalClassName.get();
        assertEquals("com.viadeo.platform.letsmeet.query.listener.IndexMemberToMeet_on_LetsMeetPropositionAccepted_EventListener", actual.getEventListenerClassName());
        assertEquals("exchangeName", actual.getExchangeName());
    }

    @Test
    public void extractQueueInfo_withoutRespectNamingConvention_with_returnClassName() {
        // Given
        amqpComponentNameFormatter.setQueueNameFormat("%exchange%_%cluster%_%listener%");
        String queueName = "exchangeName_clusterName_com.viadeo.platform.letsmeet.query.listener.IndexMemberToMeet_on_LetsMeetPropositionAccepted_EventListener";

        // When
        Optional<QueueInfo> optionalClassName = amqpComponentNameFormatter.extractQueueInfo(queueName);

        // Then
        assertNotNull(optionalClassName);
        assertTrue(optionalClassName.isPresent());

        QueueInfo actual = optionalClassName.get();
        assertEquals("com.viadeo.platform.letsmeet.query.listener.IndexMemberToMeet_on_LetsMeetPropositionAccepted_EventListener", actual.getEventListenerClassName());
        assertEquals("exchangeName", actual.getExchangeName());
    }
}
