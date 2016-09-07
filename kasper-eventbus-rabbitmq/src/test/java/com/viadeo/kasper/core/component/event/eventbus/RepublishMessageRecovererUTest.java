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

import org.axonframework.eventhandling.EventListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.ListenerExecutionFailedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RepublishMessageRecovererUTest {

    @Mock
    private AmqpTemplate amqpTemplate;

    private String exchangeName;

    private RepublishMessageRecoverer recoverer;

    @Captor
    private ArgumentCaptor<Message> messageArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        exchangeName = "sink";
        recoverer = new RepublishMessageRecoverer(amqpTemplate, exchangeName);
    }

    private Message createMessage() {
        return createMessage(exchangeName, "#");
    }

    private Message createMessage(String exchangeName, String routingKey) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setReceivedRoutingKey(routingKey);
        messageProperties.setReceivedExchange(exchangeName);

        return new Message(
                "Hello".getBytes(),
                messageProperties
        );
    }

    @Test
    public void recover_shouldPublish() {
        // Given
        Message message = createMessage();
        Throwable throwable = new RuntimeException("bazinga!!");

        // When
        recoverer.recover(message, throwable);

        // Then
        verify(amqpTemplate).send(eq(exchangeName), eq("fallback." + message.getMessageProperties().getReceivedRoutingKey()), eq(message));
    }

    @Test
    public void recover_includeOriginalMessageInfoInTheHeader() {
        // Given
        Message message = createMessage("originalExchange", "originalRoutingKey");
        Throwable throwable = new RuntimeException("bazinga!!");

        // When
        recoverer.recover(message, throwable);

        // Then
        verify(amqpTemplate).send(
                eq(exchangeName),
                eq("fallback." + message.getMessageProperties().getReceivedRoutingKey()),
                messageArgumentCaptor.capture()
        );

        assertEquals(1, messageArgumentCaptor.getAllValues().size());

        Message actualMessage = messageArgumentCaptor.getValue();
        assertEquals("originalExchange", actualMessage.getMessageProperties().getHeaders().get("x-original-exchange"));
        assertEquals("originalRoutingKey", actualMessage.getMessageProperties().getHeaders().get("x-original-routingKey"));
    }

    @Test
    public void recover_withAnyThrowable_includeExceptionInfoInTheHeader() {
        // Given
        Message message = createMessage();
        Throwable throwable = new RuntimeException("bazinga!!");

        // When
        recoverer.recover(message, throwable);

        // Then
        verify(amqpTemplate).send(
                eq(exchangeName),
                eq("fallback." + message.getMessageProperties().getReceivedRoutingKey()),
                messageArgumentCaptor.capture()
        );

        assertEquals(1, messageArgumentCaptor.getAllValues().size());

        Message actualMessage = messageArgumentCaptor.getValue();
        assertEquals("bazinga!!", actualMessage.getMessageProperties().getHeaders().get("x-exception-message"));
        assertNotNull(RepublishMessageRecoverer.getStackTraceAsString(throwable), actualMessage.getMessageProperties().getHeaders().get("x-exception-stacktrace"));
    }

    @Test
    public void recover_withMessageHandlerException_includeExceptionInfoInTheHeader() {
        // Given
        Message message = createMessage();
        Throwable cause = new RuntimeException("bazinga!!");
        Class<EventListener> source = EventListener.class;
        Throwable throwable = new MessageHandlerException(source, cause);

        // When
        recoverer.recover(message, new ListenerExecutionFailedException("", throwable));

        // Then
        verify(amqpTemplate).send(
                eq(exchangeName),
                eq(source.getName()),
                messageArgumentCaptor.capture()
        );

        assertEquals(1, messageArgumentCaptor.getAllValues().size());

        Message actualMessage = messageArgumentCaptor.getValue();
        assertEquals(cause.getMessage(), actualMessage.getMessageProperties().getHeaders().get("x-exception-message"));
        assertNotNull(RepublishMessageRecoverer.getStackTraceAsString(cause), actualMessage.getMessageProperties().getHeaders().get("x-exception-stacktrace"));
        assertNotNull(source.getName(), actualMessage.getMessageProperties().getHeaders().get("x-exception-source"));
    }

    @Test
    public void getClassNameFromCanonicalName_withClassNameAndPackage_shouldReturnSimpleName() {
        // When
        final String classNameFromCanonicalName = RepublishMessageRecoverer.getClassNameFromCanonicalName("com.viadeo.platform.company.api.event.FollowCompanyEvent");

        // Then
        assertEquals("FollowCompanyEvent", classNameFromCanonicalName);
    }

    @Test
    public void getClassNameFromCanonicalName_withClass_shouldReturnSimpleName() {
        // When
        final String classNameFromCanonicalName = RepublishMessageRecoverer.getClassNameFromCanonicalName(this.getClass());

        // Then
        assertEquals("RepublishMessageRecovererUTest", classNameFromCanonicalName);
    }
}
