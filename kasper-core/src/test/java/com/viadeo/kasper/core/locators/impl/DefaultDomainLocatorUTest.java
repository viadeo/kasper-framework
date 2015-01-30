package com.viadeo.kasper.core.locators.impl;

import static com.google.common.collect.Sets.newHashSet;
import static com.viadeo.kasper.core.locators.impl.DefaultDomainLocator.getHandlerTags;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.resolvers.CommandHandlerResolver;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.ddd.Domain;

public class DefaultDomainLocatorUTest {

    DefaultDomainLocator locator;

    @Mock
    CommandHandlerResolver commandHandlerResolver;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        locator = new DefaultDomainLocator(commandHandlerResolver);
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
        locator.getHandlerClass(command);
    }

    @Test
    public void getHandlerClass_withUnregisteredCommand_shouldReturnNull() {
        // Given
        Command command = new TestCommand();

        // When
        Class<? extends CommandHandler> handlerClass = locator.getHandlerClass(command);

        // Then
        assertNull(handlerClass);
    }

    @Test
    public void getHandlerClass_withRegisteredCommand_shouldReturnTheHandlersClass() {
        // Given
        Command command = new TestCommand();
        CommandHandler registeredHandler = new TestCommandHandler_WithSeveralTags();

        doReturn(TestCommand.class)
                .when(commandHandlerResolver).getCommandClass(TestCommandHandler_WithSeveralTags.class);
        locator.registerHandler(registeredHandler);

        // When
        Class<? extends CommandHandler> handlerClass = locator.getHandlerClass(command);

        // Then
        assertEquals(registeredHandler.getClass(), handlerClass);
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
        getHandlerTags(handlerClass);
    }

    @Test
    @SuppressWarnings("all")
    public void getHandlerTags_withHandlerWithNoTags_shouldReturnEmpty() {
        // Given
        Class<? extends CommandHandler> handlerClass = TestCommandHandler_WithNoTags.class;

        // When
        Set<String> tags = getHandlerTags(handlerClass);

        // Then
        assertEquals(newHashSet(), tags);
    }

    @Test
    public void getHandlerTags_withHandlerWithOneTag_shouldReturnTheSingletonSet() {
        // Given
        Class<? extends CommandHandler> handlerClass = TestCommandHandler_WithOneTag.class;

        // When
        Set<String> tags = getHandlerTags(handlerClass);

        // Then
        assertEquals(newHashSet(TEST_COMMAND_TAG), tags);
    }

    @Test
    public void getHandlerTags_withHandlerWithSeveralTags_shouldReturnTheSet() {
        // Given
        Class<? extends CommandHandler> handlerClass = TestCommandHandler_WithSeveralTags.class;

        // When
        Set<String> tags = getHandlerTags(handlerClass);

        // Then
        assertEquals(newHashSet(TEST_COMMAND_TAG, TEST_COMMAND_TAG_2), tags);
    }

    @Test
    @SuppressWarnings("all")
    public void getHandlerTags_withHandlerWithoutAnnotations_shouldReturnEmpty() {
        // Given
        Class<? extends CommandHandler> handlerClass = TestCommandHandler_WithoutAnnotation.class;

        // When
        Set<String> tags = getHandlerTags(handlerClass);

        // Then
        assertEquals(newHashSet(), tags);
    }

    // ------------------------------------------------------------------------

    private static final String TEST_COMMAND_TAG = "this-is-a-tag";

    private static final String TEST_COMMAND_TAG_2 = "this-is-another-tag";

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

    @XKasperUnregistered
    private static class TestCommandHandler_WithoutAnnotation extends CommandHandler<TestCommand> {
    }

}
