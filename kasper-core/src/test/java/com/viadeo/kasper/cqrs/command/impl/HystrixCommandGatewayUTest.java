package com.viadeo.kasper.cqrs.command.impl;

import com.codahale.metrics.MetricRegistry;
import com.google.common.util.concurrent.Futures;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.interceptor.InterceptorChainRegistry;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.axonframework.commandhandling.interceptors.JSR303ViolationException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class HystrixCommandGatewayUTest {

    private CommandGateway commandGateway;
    private HystrixCommandGateway hystrixCommandGateway;
    private Command command;
    private Context context;

    @Before
    public void init() {
        commandGateway = mock(CommandGateway.class);
        KasperCommandGateway bloatGateway = createBloatGateway(commandGateway);
        hystrixCommandGateway = new HystrixCommandGateway(bloatGateway, new MetricRegistry());
        command = mock(Command.class);
        context = mock(Context.class);
    }

    @Test(timeout = 2000)
    public void sendCommand_should_fallback_on_timeout() throws Exception {
        // Given
        doAnswer(new SlowAnswer(2500)).when(commandGateway).sendCommand(any(Command.class), any(Context.class));
        long initialFallbackCount = hystrixCommandGateway.getFallbackCount();

        try {
            // When
            hystrixCommandGateway.sendCommand(command, context);

            // Then
            assertTrue(initialFallbackCount < hystrixCommandGateway.getFallbackCount());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void sendCommand_should_work() throws Exception {
        // Given
        doAnswer(new SlowAnswer(0)).when(commandGateway).sendCommand(any(Command.class), any(Context.class));

        // When
        hystrixCommandGateway.sendCommand(command, context);

        // Then
        assertEquals(0, hystrixCommandGateway.getFallbackCount());
    }

    @Test(timeout = 2000)
    public void sendCommandAndWait_should_fallback_on_timeout() throws Exception {
        // Given
        doAnswer(new SlowAnswer(2500)).when(commandGateway).sendCommandAndWait(any(Command.class), any(Context.class), anyInt(), any(TimeUnit.class));
        long initialFallbackCount = hystrixCommandGateway.getFallbackCount();

        // When
        hystrixCommandGateway.sendCommandAndWait(command, context, 50L, TimeUnit.MILLISECONDS);

        // Then
        assertTrue(initialFallbackCount < hystrixCommandGateway.getFallbackCount());
    }

    @Test
    public void sendCommandAndWait_should_work() throws Exception {
        // Given
        doAnswer(new SlowAnswer(0)).when(commandGateway).sendCommand(any(Command.class), any(Context.class));

        // When
        hystrixCommandGateway.sendCommandAndWait(command, context, 1, TimeUnit.MINUTES);

        // Then
        assertEquals(0, hystrixCommandGateway.getFallbackCount());
    }

    @Test(timeout = 2000L)
    public void sendCommandForFuture_should_return_error_response_on_fallback() throws Exception {
        // Given
        // Simulate that the sendCommandForFuture block for 2000 ms
        doAnswer(new FakeCommandResponseAnswer(2500)).when(commandGateway).sendCommandForFuture(any(Command.class), any(Context.class));

        // When
        Future<CommandResponse> commandResponseFuture = hystrixCommandGateway.sendCommandForFuture(command, context);

        // Then
        try {
            commandResponseFuture.get(1000, TimeUnit.MILLISECONDS);
            assertFalse(commandResponseFuture.get().isOK());
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // not yet finished
            fail();
        }
    }

    @Ignore("this test produces errors on other tests")
    @Test
    public void sendCommandForFuture_should_work() throws Exception {
        // Given
        when(commandGateway.sendCommandForFuture(any(Command.class), any(Context.class))).thenReturn(Futures.immediateFuture(CommandResponse.ok()));

        // When
        Future<CommandResponse> commandResponseFuture = hystrixCommandGateway.sendCommandForFuture(command, context);

        // Then
        assertTrue(commandResponseFuture.get().isOK());
    }

    @Test
    public void any_should_not_enter_fallback_on_interceptor_exceptions() throws Exception {
        //Given (exception throws by interceptor)
        doThrow(new JSR303ViolationException("error in validation", Collections.EMPTY_SET)).when(commandGateway).sendCommand(any(Command.class), any(Context.class));
        doThrow(new JSR303ViolationException("error in validation", Collections.EMPTY_SET)).when(commandGateway).sendCommandForFuture(any(Command.class), any(Context.class));
        doThrow(new JSR303ViolationException("error in validation", Collections.EMPTY_SET)).when(commandGateway).sendCommandAndWait(any(Command.class), any(Context.class), anyLong(), any(TimeUnit.class));


        Exception exception = null;

        // When
        try {
            hystrixCommandGateway.sendCommandForFuture(command, context);
            assertEquals(0L, hystrixCommandGateway.getFallbackCount());
        } catch (HystrixBadRequestException e) {
            // c'est normal, on rebalance l'exception a la couche appelante
            exception = e;
        }

        assertNotNull(exception);
        exception = null;

        try {
            hystrixCommandGateway.sendCommand(command, context);
            assertEquals(0L, hystrixCommandGateway.getFallbackCount());
        } catch (HystrixBadRequestException e) {
            // c'est normal, on rebalance l'exception a la couche appelante
            exception = e;
        }

        assertNotNull(exception);
        exception = null;

        try {
            hystrixCommandGateway.sendCommandAndWait(command, context, 1L, TimeUnit.SECONDS);
            assertEquals(0L, hystrixCommandGateway.getFallbackCount());
        } catch (HystrixBadRequestException e) {
            // c'est normal, on rebalance l'exception a la couche appelante
            exception = e;
        }

        assertNotNull(exception);

    }

    //-------------- test classes and methods

    private KasperCommandGateway createBloatGateway(CommandGateway cg) {
        if (cg == null) throw new NullPointerException("boulet c'est null ton param");
        try {
            final CommandGatewayFactoryBean<CommandGateway> cgfb = mock(CommandGatewayFactoryBean.class);

            when(cgfb.getObject()).thenReturn(cg);
            KasperCommandBus cb = mock(KasperCommandBus.class);
            DomainLocator dl = mock(DomainLocator.class);
            InterceptorChainRegistry icr = mock(InterceptorChainRegistry.class);
            return new KasperCommandGateway(cgfb, cb, dl, icr);
        } catch (Exception e) {
            // devrait pas arriver
            return null;
        }
    }

    private class SlowAnswer implements Answer<Void> {

        private int sleepInMs = 10000; // default

        public SlowAnswer(int sleepInMs) {
            this.sleepInMs = sleepInMs;
        }

        @Override
        public Void answer(InvocationOnMock invocation) {
            if (sleepInMs > 0) {
                try {
                    Thread.sleep(sleepInMs);
                } catch (InterruptedException e) {
                    // interrupted
                }
            }
            return null;
        }
    }

    private class FakeCommandResponseAnswer implements Answer<Future<CommandResponse>> {

        private int sleepInMs = 0; // default

        public FakeCommandResponseAnswer(int sleepInMs) {
            this.sleepInMs = sleepInMs;
        }

        @Override
        public Future<CommandResponse> answer(InvocationOnMock invocation) {
            if (sleepInMs > 0) {
                try {
                    Thread.sleep(sleepInMs);
                } catch (InterruptedException e) {
                    // interrupted
                }
            }
            return Futures.immediateFuture(CommandResponse.ok());
        }
    }


}