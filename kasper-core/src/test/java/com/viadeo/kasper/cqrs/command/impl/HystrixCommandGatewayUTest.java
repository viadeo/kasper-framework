// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
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

    private CommandGateway cg;
    private HystrixCommandGateway hystrixCommandGateway;
    private Command command;
    private Context context;

    // ------------------------------------------------------------------------

    @Before
    public void init() {
        cg = mock(CommandGateway.class);

        final KasperCommandGateway bloatGateway = createBloatGateway(cg);
        hystrixCommandGateway = new HystrixCommandGateway(bloatGateway, new MetricRegistry());
        command = mock(Command.class);
        context = mock(Context.class);
    }

    // ------------------------------------------------------------------------

//    @Test(timeout = 1400)
    public void sendCommand_should_fallback_on_timeout() throws Exception {

        // Given
        doAnswer(new SlowAnswer(1500)).when(cg).sendCommand(any(Command.class), any(Context.class));

        // When
        try {
            final long initialFallbackCount = hystrixCommandGateway.getFallbackCount();
            hystrixCommandGateway.sendCommand(command, context);
            assertTrue(initialFallbackCount < hystrixCommandGateway.getFallbackCount());
        } catch (final Exception e) {
            fail();
        }
        // Then

    }

//    @Test(timeout = 2000)
    public void sendCommandAndWait_should_fallback_on_timeout() throws Exception {
        // Given
        // Simulate cascading blocks
        doAnswer(new SlowAnswer(2000)).when(cg).sendCommandAndWait(any(Command.class), any(Context.class), anyInt(), any(TimeUnit.class));
        doAnswer(new SlowAnswer(1000)).when(cg).sendCommand(any(Command.class), any(Context.class));

        // When
        final long initialFallbackCount = hystrixCommandGateway.getFallbackCount();
        hystrixCommandGateway.sendCommandAndWait(command, context, 50L, TimeUnit.MILLISECONDS);

        // Then
        assertTrue(initialFallbackCount < hystrixCommandGateway.getFallbackCount());
    }

//    @Test(timeout = 2000L)
    public void sendCommandForFuture_should_return_error_response_on_fallback() throws Exception {
        // Given
        // Simulate that the sendCommandForFuture block for 2000 ms
        doAnswer(new FakeCommandResponseAnswer(2000)).when(cg).sendCommandForFuture(any(Command.class), any(Context.class));

        // When
        final Future<CommandResponse> commandResponseFuture = hystrixCommandGateway.sendCommandForFuture(command, context);

        // Then
        try {
            commandResponseFuture.get(1000, TimeUnit.MILLISECONDS);
            assertFalse(commandResponseFuture.get().isOK());
        } catch (final InterruptedException | ExecutionException | TimeoutException e) {
            // not yet finished
            fail();
        }
    }

    @Test
    public void sendCommandForFuture_should_work() throws Exception {
        // Given
        doAnswer(new FakeCommandResponseAnswer(0)).when(cg).sendCommandForFuture(any(Command.class), any(Context.class));

        // When
        final Future<CommandResponse> commandResponseFuture = hystrixCommandGateway.sendCommandForFuture(command, context);

        // Then
        try {
            commandResponseFuture.get(500, TimeUnit.MILLISECONDS);
            assertTrue(commandResponseFuture.get().isOK());
        }catch (final InterruptedException | ExecutionException | TimeoutException e) {
            // not yet finished
            fail();
        }
    }

    @Test
    public void any_should_not_enter_fallback_on_interceptor_exceptions() throws Exception {
        // Given (exception throws by interceptor)
        doThrow(new JSR303ViolationException("error in validation", Collections.EMPTY_SET)).when(cg).sendCommand(any(Command.class), any(Context.class));
        doThrow(new JSR303ViolationException("error in validation", Collections.EMPTY_SET)).when(cg).sendCommandForFuture(any(Command.class), any(Context.class));
        doThrow(new JSR303ViolationException("error in validation", Collections.EMPTY_SET)).when(cg).sendCommandAndWait(any(Command.class), any(Context.class), anyLong(), any(TimeUnit.class));

        // When / Then
        try {
            hystrixCommandGateway.sendCommandForFuture(command, context);
            assertEquals(0L, hystrixCommandGateway.getFallbackCount());
        } catch (final HystrixBadRequestException e) {
            // c'est normal, on rebalance l'exception a la couche appelante
        }

        // When / Then
        try {
            hystrixCommandGateway.sendCommand(command, context);
            assertEquals(0L, hystrixCommandGateway.getFallbackCount());
        } catch (final HystrixBadRequestException e) {
            // c'est normal, on rebalance l'exception a la couche appelante
        }

        // When / Then
        try {
            hystrixCommandGateway.sendCommandAndWait(command, context, 1L, TimeUnit.SECONDS);
            assertEquals(0L, hystrixCommandGateway.getFallbackCount());
        } catch (final HystrixBadRequestException e) {
            // c'est normal, on rebalance l'exception a la couche appelante
        }

    }

    //-------------- test classes and methods

    private KasperCommandGateway createBloatGateway(final CommandGateway cg) {
        if (null == cg) {
            throw new NullPointerException("Hey noob ! Your param is just weird !");
        }

        try {
            final CommandGatewayFactoryBean<CommandGateway> cgfb = mock(CommandGatewayFactoryBean.class);

            when(cgfb.getObject()).thenReturn(cg);
            final KasperCommandBus cb = mock(KasperCommandBus.class);
            final DomainLocator dl = mock(DomainLocator.class);
            final InterceptorChainRegistry icr = mock(InterceptorChainRegistry.class);
            return new KasperCommandGateway(cgfb, cb, dl, icr);

        } catch (final Exception e) {
            // should not occur
            return null;
        }
    }

    // ------------------------------------------------------------------------

    private class SlowAnswer implements Answer<Void> {

        private int sleepInMs = 10000; // default

        public SlowAnswer(final int sleepInMs) {
            this.sleepInMs = sleepInMs;
        }

        @Override
        public Void answer(final InvocationOnMock invocation) {
            if (sleepInMs > 0) {
                try {
                    Thread.sleep(sleepInMs);
                } catch (final InterruptedException e) {
                    // interrupted
                }
            }
            return null;
        }
    }

    private class FakeCommandResponseAnswer implements Answer<Future<CommandResponse>> {

        private int sleepInMs = 0; // default

        public FakeCommandResponseAnswer(final int sleepInMs) {
            this.sleepInMs = sleepInMs;
        }

        @Override
        public Future<CommandResponse> answer(final InvocationOnMock invocation) {
            if (sleepInMs > 0) {
                try {
                    Thread.sleep(sleepInMs);
                } catch (final InterruptedException e) {
                    // interrupted
                }
            }
            return Futures.immediateFuture(CommandResponse.ok());
        }
    }

}
