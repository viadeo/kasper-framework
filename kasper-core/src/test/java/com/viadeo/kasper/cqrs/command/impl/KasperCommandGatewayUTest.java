// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.impl;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.interceptor.InterceptorChainRegistry;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.cqrs.command.interceptor.KasperCommandInterceptor;
import com.viadeo.kasper.ddd.Domain;

import org.axonframework.commandhandling.CommandHandlerInterceptor;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.*;

public class KasperCommandGatewayUTest {

    private final KasperCommandGateway commandGateway;
    private final KasperCommandBus commandBus;
    private final DomainLocator domainLocator;
    private final CommandGateway decoratedCommandGateway;
    private final InterceptorChainRegistry<Command, CommandResponse> interceptorChainRegistry;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public KasperCommandGatewayUTest() throws Exception {
        final CommandGatewayFactoryBean<CommandGateway> commandGatewayFactoryBean = mock(CommandGatewayFactoryBean.class);
        decoratedCommandGateway = mock(CommandGateway.class);
        when(commandGatewayFactoryBean.getObject()).thenReturn(decoratedCommandGateway);
        commandBus = mock(KasperCommandBus.class);
        domainLocator = mock(DomainLocator.class);
        interceptorChainRegistry = mock(InterceptorChainRegistry.class);
        commandGateway = new KasperCommandGateway(commandGatewayFactoryBean, commandBus, domainLocator, interceptorChainRegistry);
    }

    @Before
    public void setUp() {
        reset(domainLocator, decoratedCommandGateway);
        when(domainLocator.getHandlerForCommandClass(Matchers.<Class<Command>>any()))
                .thenReturn(Optional.<CommandHandler>absent());
    }

    // ------------------------------------------------------------------------

    @Test
    public void sendCommand_shouldDelegateTheCall() throws Exception {
        // Given
        final Command command = mock(Command.class);
        final Context context = mock(Context.class);

        // When
        commandGateway.sendCommand(command, context);

        // Then
        verify(decoratedCommandGateway).sendCommand(refEq(command), refEq(context));
        verifyNoMoreInteractions(decoratedCommandGateway);
    }

    @Test
    public void sendCommandForFuture_shouldDelegateTheCall() throws Exception {
        // Given
        final Command command = mock(Command.class);
        final Context context = mock(Context.class);

        // When
        commandGateway.sendCommandForFuture(command, context);

        // Then
        verify(decoratedCommandGateway).sendCommandForFuture(refEq(command), refEq(context));
        verifyNoMoreInteractions(decoratedCommandGateway);
    }

    @Test
    public void sendCommandAndWaitForAResponse_shouldDelegateTheCall() throws Exception {
        // Given
        final Command command = mock(Command.class);
        final Context context = mock(Context.class);

        // When
        commandGateway.sendCommandAndWaitForAResponse(command, context);

        // Then
        verify(decoratedCommandGateway).sendCommandAndWaitForAResponse(refEq(command), refEq(context));
        verifyNoMoreInteractions(decoratedCommandGateway);
    }

    @Test
    public void sendCommandAndWaitForAResponseWithException_shouldDelegateTheCall() throws Exception {
        // Given
        final Command command = mock(Command.class);
        final Context context = mock(Context.class);

        // When
        commandGateway.sendCommandAndWaitForAResponseWithException(command, context);

        // Then
        verify(decoratedCommandGateway).sendCommandAndWaitForAResponseWithException(refEq(command), refEq(context));
        verifyNoMoreInteractions(decoratedCommandGateway);
    }

    @Test
    public void sendCommandAndWait_shouldDelegateTheCall() throws Exception {
        // Given
        final Command command = mock(Command.class);
        final Context context = mock(Context.class);
        final TimeUnit unit = mock(TimeUnit.class);

        // When
        commandGateway.sendCommandAndWait(command, context, 1000, unit);

        // Then
        verify(decoratedCommandGateway).sendCommandAndWait(refEq(command), refEq(context), anyLong(), refEq(unit));
        verifyNoMoreInteractions(decoratedCommandGateway);
    }

    @Test
    public void sendCommandAndWaitForever_shouldDelegateTheCall() throws Exception {
        // Given
        final  Command command = mock(Command.class);
        final Context context = mock(Context.class);

        // When
        commandGateway.sendCommandAndWaitForever(command, context);

        // Then
        verify(decoratedCommandGateway).sendCommandAndWaitForever(refEq(command), refEq(context));
        verifyNoMoreInteractions(decoratedCommandGateway);
    }

    @Test(expected = NullPointerException.class)
    public void register_withNullAsCommandHandler_shouldThrownException() {
        // Given
        final CommandHandler commandHandler = null;

        // When
        commandGateway.register(commandHandler);

        // Then throws an exception
    }

    @Test
    public void register_withCommandHandler_shouldBeRegistered() {
        // Given
        final CommandHandler commandHandler = mock(CommandHandler.class);
        when(commandHandler.getCommandClass()).thenReturn(Command.class);

        // When
        commandGateway.register(commandHandler);

        // Then
        verify(domainLocator).registerHandler(refEq(commandHandler));
        verifyNoMoreInteractions(domainLocator);

        ArrayList<CommandHandlerInterceptor> handlerInterceptors = Lists.<CommandHandlerInterceptor>newArrayList(mock(KasperCommandInterceptor.class));
        verify(commandBus).subscribe(refEq(Command.class.getName()), any(org.axonframework.commandhandling.CommandHandler.class));
        verify(commandBus).setHandlerInterceptors(refEq(handlerInterceptors));
        verifyNoMoreInteractions(commandBus);

        verify(commandHandler).setCommandGateway(refEq(commandGateway));
        verify(commandHandler).getCommandClass();
        verifyNoMoreInteractions(commandHandler);

        verify(interceptorChainRegistry).create(eq(commandHandler.getClass()), any(CommandInterceptorFactory.class));
        verifyNoMoreInteractions(interceptorChainRegistry);
    }

    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    public void enrichMdcAndMdcContextMap_withRegisteredCommandWithTags_shouldAddItsTagsToTheContextBeforeEnrichingTheMdcContextMap() {
        // Given
        Command command = new TestCommand();
        when(domainLocator.getHandlerForCommandClass(command.getClass()))
                .thenReturn(Optional.<CommandHandler>of(new TestCommandHandler_WithSeveralTags()));
        Set<String> expectedTags = newHashSet(TEST_COMMAND_TAG, TEST_COMMAND_TAG_2);

        Context context = mock(Context.class);
        Set<String> initialSetOfTags = Sets.newHashSet();
        when(context.getTags())
                .thenReturn(initialSetOfTags);

        when(context.setTags(expectedTags))
                .thenReturn(context);

        Map<String, String> initialContextMap = ImmutableMap.of("foo", "bar");
        MDC.setContextMap(initialContextMap);

        Map<String, String> extendedContextMap = ImmutableMap.of("baz", "qux");
        when(context.asMap(initialContextMap))
                .thenReturn(extendedContextMap);


        // When
        commandGateway.enrichContextAndMdcContextMap(command, context);

        // Then
        InOrder inOrder = inOrder(context);
        inOrder.verify(context)
                .getTags();
        inOrder.verify(context)
                .setTags(expectedTags);
        inOrder.verify(context)
                .asMap(initialContextMap);
        inOrder.verifyNoMoreInteractions();

        assertEquals(extendedContextMap, MDC.getCopyOfContextMap());
    }

    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("all")
    public void getHandlerClass_withNull_shouldThrowNPE() {
        // Given
        Command command = null;

        // Expect
        thrown.expect(NullPointerException.class);

        // When
        commandGateway.getHandlerClass(command);
    }

    @Test
    public void getHandlerClass_withUnregisteredCommand_shouldReturnNull() {
        // Given
        Command command = new TestCommand();
        when(domainLocator.getHandlerForCommandClass(command.getClass()))
                .thenReturn(Optional.<CommandHandler>absent());

        // When
        Class<? extends CommandHandler> handlerClass = commandGateway.getHandlerClass(command);

        // Then
        assertNull(handlerClass);
    }

    @Test
    public void getHandlerClass_withRegisteredCommand_shouldReturnTheHandlersClass() {
        // Given
        Command command = new TestCommand();
        CommandHandler registeredHandler = new TestCommandHandler_WithSeveralTags();
        when(domainLocator.getHandlerForCommandClass(command.getClass()))
                .thenReturn(Optional.of(registeredHandler));

        // When
        Class<? extends CommandHandler> handlerClass = commandGateway.getHandlerClass(command);

        // Then
        assertEquals(registeredHandler.getClass(), handlerClass);
    }

    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("all")
    public void addHandlerTagsToContext_withNullContext_shouldThrowNPE() {
        // Given
        Context context = null;
        Class<? extends CommandHandler> handlerClass = TestCommandHandler_WithSeveralTags.class;

        // Expect
        thrown.expect(NullPointerException.class);

        // When
        KasperCommandGateway.addHandlerTagsToContext(handlerClass, context);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void addHandlerTagsToContext_withHandlerWithSeveralTags_shouldAddThemToExistingTagsInContext() {
        // Given
        Context context = mock(Context.class);

        Set<String> initialSetOfTags = Sets.newHashSet("preexisting tag");
        when(context.getTags())
                .thenReturn(initialSetOfTags);

        when(context.setTags(anySet()))
                .thenReturn(context);

        Class<? extends CommandHandler> handlerClass = TestCommandHandler_WithSeveralTags.class;
        Set<String> expectedTags = newHashSet("preexisting tag", TEST_COMMAND_TAG, TEST_COMMAND_TAG_2);

        // When
        KasperCommandGateway.addHandlerTagsToContext(handlerClass, context);

        // Then
        InOrder inOrder = inOrder(context);
        inOrder.verify(context)
                .getTags();
        inOrder.verify(context)
                .setTags(expectedTags);
        inOrder.verifyNoMoreInteractions();
    }

    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("all")
    public void getHandlerTags_withNull_shouldThrowNPE() {
        // Given
        Class<? extends CommandHandler> handlerClass = null;

        // Expect
        thrown.expect(NullPointerException.class);

        // When
        KasperCommandGateway.getHandlerTags(handlerClass);
    }

    @Test
    @SuppressWarnings("all")
    public void getHandlerTags_withHandlerWithNoTags_shouldReturnEmpty() {
        // Given
        Class<? extends CommandHandler> handlerClass = TestCommandHandler_WithNoTags.class;

        // When
        Set<String> tags = KasperCommandGateway.getHandlerTags(handlerClass);

        // Then
        assertEquals(newHashSet(), tags);
    }

    @Test
    public void getHandlerTags_withHandlerWithOneTag_shouldReturnTheSingletonSet() {
        // Given
        Class<? extends CommandHandler> handlerClass = TestCommandHandler_WithOneTag.class;

        // When
        Set<String> tags = KasperCommandGateway.getHandlerTags(handlerClass);

        // Then
        assertEquals(newHashSet(TEST_COMMAND_TAG), tags);
    }

    @Test
    public void getHandlerTags_withHandlerWithSeveralTags_shouldReturnTheSet() {
        // Given
        Class<? extends CommandHandler> handlerClass = TestCommandHandler_WithSeveralTags.class;

        // When
        Set<String> tags = KasperCommandGateway.getHandlerTags(handlerClass);

        // Then
        assertEquals(newHashSet(TEST_COMMAND_TAG, TEST_COMMAND_TAG_2), tags);
    }

    // ------------------------------------------------------------------------

    private static final String TEST_COMMAND_TAG = "this is a tag";

    private static final String TEST_COMMAND_TAG_2 = "this is another tag";

    @XKasperUnregistered
    private static class TestDomain implements Domain {
    }

    @XKasperUnregistered
    private static class TestCommand implements Command {
    }

    @XKasperUnregistered
    @XKasperCommandHandler(domain = TestDomain.class)
    private static class TestCommandHandler_WithNoTags extends CommandHandler<TestCommand> {
    }

    @XKasperUnregistered
    @XKasperCommandHandler(domain = TestDomain.class, tags = TEST_COMMAND_TAG)
    private static class TestCommandHandler_WithOneTag extends CommandHandler<TestCommand> {
    }

    @XKasperUnregistered
    @XKasperCommandHandler(domain = TestDomain.class, tags = {TEST_COMMAND_TAG, TEST_COMMAND_TAG_2})
    private static class TestCommandHandler_WithSeveralTags extends CommandHandler<TestCommand> {
    }

}
