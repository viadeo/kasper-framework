// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.locators.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.resolvers.CommandHandlerResolver;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.ddd.Domain;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static com.viadeo.kasper.core.locators.impl.DefaultDomainLocator.getHandlerTags;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.MockitoAnnotations.initMocks;

public class DefaultDomainLocatorUTest {

    DefaultDomainLocator locator;

    @Mock
    CommandHandlerResolver commandHandlerResolver;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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

    // ------------------------------------------------------------------------

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        locator = new DefaultDomainLocator(commandHandlerResolver);
    }

    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("all")
    public void getHandlerTags_withNullCommand_shouldThrowNPE() {
        // Given
        final Command command = null;

        // Expect
        thrown.expect(NullPointerException.class);

        // When
        locator.getHandlerTags(command);
    }

    @Test
    @SuppressWarnings("all")
    public void getHandlerTags_withUnregisteredCommand_shouldReturnNull() {
        // Given
        final Command command = new TestCommand();

        doReturn(Optional.absent())
                .when(commandHandlerResolver).getHandlerClass(TestCommand.class);

        // When
        final Set<String> tags = locator.getHandlerTags(command);

        // Then
        assertEquals(newHashSet(), tags);
    }

    @Test
    public void getHandlerTags_withRegisteredCommand_shouldReturnTheTags() {
        // Given
        final Command command = new TestCommand();

        doReturn(Optional.of(TestCommandHandler_WithSeveralTags.class))
                .when(commandHandlerResolver).getHandlerClass(TestCommand.class);

        final Set<String> expectedTags = newHashSet(TEST_COMMAND_TAG, TEST_COMMAND_TAG_2);

        // When
        final Set<String> tags = locator.getHandlerTags(command);

        // Then
        assertEquals(expectedTags, tags);
    }

    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("all")
    public void getHandlerTags_withNullHandlerClass_shouldThrowNPE() {
        // Given
        final Class<? extends CommandHandler> handlerClass = null;

        // Expect
        thrown.expect(NullPointerException.class);

        // When
        getHandlerTags(handlerClass);
    }

    @Test
    @SuppressWarnings("all")
    public void getHandlerTags_withHandlerWithNoTags_shouldReturnEmpty() {
        // Given
        final Class<? extends CommandHandler> handlerClass = TestCommandHandler_WithNoTags.class;

        // When
        final Set<String> tags = getHandlerTags(handlerClass);

        // Then
        assertEquals(newHashSet(), tags);
    }

    @Test
    public void getHandlerTags_withHandlerWithOneTag_shouldReturnTheSingletonSet() {
        // Given
        final Class<? extends CommandHandler> handlerClass = TestCommandHandler_WithOneTag.class;

        // When
        final Set<String> tags = getHandlerTags(handlerClass);

        // Then
        assertEquals(newHashSet(TEST_COMMAND_TAG), tags);
    }

    @Test
    public void getHandlerTags_withHandlerWithSeveralTags_shouldReturnTheSet() {
        // Given
        final Class<? extends CommandHandler> handlerClass = TestCommandHandler_WithSeveralTags.class;

        // When
        final Set<String> tags = getHandlerTags(handlerClass);

        // Then
        assertEquals(newHashSet(TEST_COMMAND_TAG, TEST_COMMAND_TAG_2), tags);
    }

    @Test
    @SuppressWarnings("all")
    public void getHandlerTags_withHandlerWithoutAnnotations_shouldReturnEmpty() {
        // Given
        final Class<? extends CommandHandler> handlerClass = TestCommandHandler_WithoutAnnotation.class;

        // When
        final Set<String> tags = getHandlerTags(handlerClass);

        // Then
        assertEquals(newHashSet(), tags);
    }

}
