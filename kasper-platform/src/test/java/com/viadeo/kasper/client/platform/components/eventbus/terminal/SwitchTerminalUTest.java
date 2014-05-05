// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus.terminal;

import org.axonframework.domain.EventMessage;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.Cluster;
import org.axonframework.eventhandling.EventBusTerminal;
import org.axonframework.eventhandling.SimpleCluster;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.*;

public class SwitchTerminalUTest {

    @Test(expected = NullPointerException.class)
    public void init_withNullAsPrimary_throwException() {
        // Given
        final EventBusTerminal primary = null;
        final EventBusTerminal secondary = new DefaultTerminal();

        // When
        new SwitchTerminal(primary, secondary);

        // Then throw an exception
    }

    @Test(expected = NullPointerException.class)
    public void init_withNullAsSecondary_throwException() {
        // Given
        final EventBusTerminal primary = new DefaultTerminal();
        final EventBusTerminal secondary = null;

        // When
        new SwitchTerminal(primary, secondary);

        // Then throw an exception
    }

    @Test()
    public void init_withPrimaryAndSecondary_isOk() {
        // Given
        final EventBusTerminal primary = new DefaultTerminal();
        final EventBusTerminal secondary = new DefaultTerminal();

        // When
        new SwitchTerminal(primary, secondary);

        // Then throw no exception
    }

    @Test
    public void onClusterCreated_delegateOnlyToThePrimary() {
        // Given
        final EventBusTerminal primary = mock(EventBusTerminal.class);
        final EventBusTerminal secondary = mock(EventBusTerminal.class);

        final SwitchTerminal terminal = new SwitchTerminal(primary, secondary);

        final Cluster cluster = new SimpleCluster("test");

        // When
        terminal.onClusterCreated(cluster);

        // Then
        verify(primary).onClusterCreated(refEq(cluster));
        verify(secondary, never()).onClusterCreated(refEq(cluster));
    }

    @Test
    public void publish_withNullAsMessages_isOk() {
        // Given
        final EventBusTerminal primary = mock(EventBusTerminal.class);
        final EventBusTerminal secondary = mock(EventBusTerminal.class);

        final SwitchTerminal terminal = new SwitchTerminal(primary, secondary);

        final EventMessage<String>[] messages = null;

        // When
        terminal.publish(messages);

        // Then
        verify(primary, never()).publish(any(EventMessage[].class));
        verify(secondary, never()).publish(any(EventMessage[].class));
    }

    @Test
    public void publish_withNullAsArrayOfMessages_isOk() {
        // Given
        final EventBusTerminal primary = mock(EventBusTerminal.class);
        final EventBusTerminal secondary = mock(EventBusTerminal.class);

        final SwitchTerminal terminal = new SwitchTerminal(primary, secondary);

        // When
        terminal.publish((EventMessage[])null);

        // Then
        verifyZeroInteractions(primary, secondary);
    }

    @Test
    public void publish_knowingThatThePrimaryIsInError_shouldPublishOnTheSecondary() throws InterruptedException {
        // Given
        final CountDownLatch countDownLatch = new CountDownLatch(2);

        final Cluster cluster = new SimpleCluster("test") {
            @Override
            public void publish(EventMessage... events) {
                super.publish(events);
                countDownLatch.countDown();
            }
        };

        final EventBusTerminal primary = mock(DefaultTerminal.class);
        doThrow(new RuntimeException("Test error")).when(primary).publish(any(EventMessage.class));

        final EventBusTerminal secondary = spy(new DefaultTerminal());
        secondary.onClusterCreated(cluster);

        final SwitchTerminal terminal = new SwitchTerminal(primary, secondary);

        final EventMessage<String> message = new GenericEventMessage<>("Hello!");

        // When
        terminal.publish(message);
        countDownLatch.await(500, TimeUnit.MILLISECONDS);

        // Then
        assertEquals(1, countDownLatch.getCount());
        verify(primary).publish(refEq(message));
        verify(secondary).publish(refEq(message));
    }

    @Test
    public void publish_knowingThatTheSecondaryIsInError_shouldPublishOnThePrimary() throws InterruptedException {
        // Given
        final CountDownLatch countDownLatch = new CountDownLatch(2);

        final Cluster cluster = new SimpleCluster("test") {
            @Override
            public void publish(EventMessage... events) {
                super.publish(events);
                countDownLatch.countDown();
            }
        };

        final EventBusTerminal primary = spy(new DefaultTerminal());
        primary.onClusterCreated(cluster);

        final EventBusTerminal secondary = mock(DefaultTerminal.class);
        doThrow(new RuntimeException("Test error")).when(secondary).publish(any(EventMessage.class));

        final SwitchTerminal terminal = new SwitchTerminal(primary, secondary);

        final EventMessage<String> message = new GenericEventMessage<>("Hello!");

        // When
        terminal.publish(message);
        countDownLatch.await(500, TimeUnit.MILLISECONDS);

        // Then
        assertEquals(1, countDownLatch.getCount());
        verify(primary).publish(refEq(message));
        verify(secondary).publish(refEq(message));
    }

    @Test
    public void publish_shouldPublishOnThePrimaryAndTheSecondary() throws InterruptedException {
        // Given
        final CountDownLatch countDownLatch = new CountDownLatch(2);

        final Cluster cluster = new SimpleCluster("test") {
            @Override
            public void publish(EventMessage... events) {
                super.publish(events);
                countDownLatch.countDown();
            }
        };

        final EventBusTerminal primary = spy(new DefaultTerminal());
        primary.onClusterCreated(cluster);

        final EventBusTerminal secondary = spy(new DefaultTerminal());
        secondary.onClusterCreated(cluster);

        final SwitchTerminal terminal = new SwitchTerminal(primary, secondary);

        final EventMessage<String> message = new GenericEventMessage<>("Hello!");

        // When
        terminal.publish(message);
        countDownLatch.await(500, TimeUnit.MILLISECONDS);

        // Then
        assertEquals(0, countDownLatch.getCount());
        verify(primary).publish(refEq(message));
        verify(secondary).publish(refEq(message));
    }
}
