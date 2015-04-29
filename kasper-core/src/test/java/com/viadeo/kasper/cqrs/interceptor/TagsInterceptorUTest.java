// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.interceptor;

import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.ddd.Domain;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.*;

public class TagsInterceptorUTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void retrieveTags_withNull_shouldThrowNPE() {
        // Given
        final TagsInterceptor<Object> tagsInterceptor = new TagsInterceptor<>(TypeToken.of(Object.class));

        // Expect
        thrown.expect(NullPointerException.class);

        // When
        tagsInterceptor.retrieveTags(null);
    }

    // --- queries

    @Test
    public void retrieveTags_fromQueryHandler_withoutTags_shouldReturnEmpty() {
        // Given
        final Class<?> handlerClass = TestQueryHandler_WithNoTags.class;
        final TagsInterceptor<Object> tagsInterceptor = new TagsInterceptor<>(TypeToken.of(handlerClass));

        // When
        Set<String> tags = tagsInterceptor.retrieveTags(handlerClass);

        // Then
        assertNotNull(tags);
        assertTrue(tags.isEmpty());
        assertEquals(tags, TagsInterceptor.CACHE_TAGS.get(handlerClass));
    }

    @Test
    public void retrieveTags_fromQueryHandler_withOneTag_shouldReturnTheSingletonSet() {
        // Given
        final Class<?> handlerClass = TestQueryHandler_WithOneTag.class;
        final TagsInterceptor<Object> tagsInterceptor = new TagsInterceptor<>(TypeToken.of(handlerClass));

        // When
        final Set<String> tags = tagsInterceptor.retrieveTags(handlerClass);

        // Then
        final Set<String> expectedTags = newHashSet(TEST_COMMAND_TAG);
        assertNotNull(tags);
        assertEquals(expectedTags, tags);
        assertEquals(expectedTags, TagsInterceptor.CACHE_TAGS.get(handlerClass));
    }

    @Test
    public void retrieveTags_fromQueryHandler_withSeveralTags_shouldReturnTheSet() {
        // Given
        final Class<?> handlerClass = TestQueryHandler_WithSeveralTags.class;
        final TagsInterceptor<Object> tagsInterceptor = new TagsInterceptor<>(TypeToken.of(handlerClass));

        // When
        final Set<String> tags = tagsInterceptor.retrieveTags(handlerClass);

        // Then
        final Set<String> expectedTags = newHashSet(TEST_COMMAND_TAG, TEST_COMMAND_TAG_2);
        assertNotNull(tags);
        assertEquals(expectedTags, tags);
        assertEquals(expectedTags, TagsInterceptor.CACHE_TAGS.get(handlerClass));
    }

    @Test
    @SuppressWarnings("all")
    public void retrieveTags_fromQueryHandler_withoutAnnotations_shouldReturnEmpty() {
        // Given
        final Class<?> handlerClass = TestQueryHandler_WithoutAnnotation.class;
        final TagsInterceptor<Object> tagsInterceptor = new TagsInterceptor<>(TypeToken.of(handlerClass));

        // When
        final Set<String> tags = tagsInterceptor.retrieveTags(handlerClass);

        // Then
        final Set<String> expectedTags = newHashSet(TEST_COMMAND_TAG, TEST_COMMAND_TAG_2);
        assertNotNull(tags);
        assertTrue(tags.isEmpty());
        assertEquals(tags, TagsInterceptor.CACHE_TAGS.get(handlerClass));
    }

    // --- commands

    @Test
    public void retrieveTags_fromCommandHandler_withoutTags_shouldReturnEmpty() {
        // Given
        final Class<?> handlerClass = TestCommandHandler_WithNoTags.class;
        final TagsInterceptor<Object> tagsInterceptor = new TagsInterceptor<>(TypeToken.of(handlerClass));

        // When
        Set<String> tags = tagsInterceptor.retrieveTags(handlerClass);

        // Then
        assertNotNull(tags);
        assertTrue(tags.isEmpty());
        assertEquals(tags, TagsInterceptor.CACHE_TAGS.get(handlerClass));
    }

    @Test
    public void retrieveTags_fromCommandHandler_withOneTag_shouldReturnTheSingletonSet() {
        // Given
        final Class<?> handlerClass = TestCommandHandler_WithOneTag.class;
        final TagsInterceptor<Object> tagsInterceptor = new TagsInterceptor<>(TypeToken.of(handlerClass));

        // When
        final Set<String> tags = tagsInterceptor.retrieveTags(handlerClass);

        // Then
        final Set<String> expectedTags = newHashSet(TEST_COMMAND_TAG);
        assertNotNull(tags);
        assertEquals(expectedTags, tags);
        assertEquals(expectedTags, TagsInterceptor.CACHE_TAGS.get(handlerClass));
    }

    @Test
    public void retrieveTags_fromCommandHandler_withSeveralTags_shouldReturnTheSet() {
        // Given
        final Class<?> handlerClass = TestCommandHandler_WithSeveralTags.class;
        final TagsInterceptor<Object> tagsInterceptor = new TagsInterceptor<>(TypeToken.of(handlerClass));

        // When
        final Set<String> tags = tagsInterceptor.retrieveTags(handlerClass);

        // Then
        final Set<String> expectedTags = newHashSet(TEST_COMMAND_TAG, TEST_COMMAND_TAG_2);
        assertNotNull(tags);
        assertEquals(expectedTags, tags);
        assertEquals(expectedTags, TagsInterceptor.CACHE_TAGS.get(handlerClass));
    }

    @Test
    @SuppressWarnings("all")
    public void retrieveTags_fromCommandHandler_withoutAnnotations_shouldReturnEmpty() {
        // Given
        final Class<?> handlerClass = TestCommandHandler_WithoutAnnotation.class;
        final TagsInterceptor<Object> tagsInterceptor = new TagsInterceptor<>(TypeToken.of(handlerClass));

        // When
        final Set<String> tags = tagsInterceptor.retrieveTags(handlerClass);

        // Then
        final Set<String> expectedTags = newHashSet(TEST_COMMAND_TAG, TEST_COMMAND_TAG_2);
        assertNotNull(tags);
        assertTrue(tags.isEmpty());
        assertEquals(tags, TagsInterceptor.CACHE_TAGS.get(handlerClass));
    }

    // ------------------------------------------------------------------------

    private static final String TEST_COMMAND_TAG = "this-is-a-tag";

    private static final String TEST_COMMAND_TAG_2 = "this-is-another-tag";

    @XKasperUnregistered
    private static class TestDomain implements Domain { }

    @XKasperUnregistered
    private static class TestCommand implements Command { }

    @XKasperUnregistered
    private static class TestQuery implements Query { }

    @XKasperUnregistered
    private static class TestQueryResult implements QueryResult { }

    @XKasperUnregistered
    @XKasperQueryHandler(domain = TestDomain.class)
    private static class TestQueryHandler_WithNoTags extends QueryHandler<TestQuery, TestQueryResult> { }

    @XKasperUnregistered
    @XKasperQueryHandler(domain = TestDomain.class, tags = TEST_COMMAND_TAG)
    private static class TestQueryHandler_WithOneTag extends QueryHandler<TestQuery, TestQueryResult> { }

    @XKasperUnregistered
    @XKasperQueryHandler(domain = TestDomain.class, tags = {TEST_COMMAND_TAG, TEST_COMMAND_TAG_2})
    private static class TestQueryHandler_WithSeveralTags extends QueryHandler<TestQuery, TestQueryResult> { }

    @XKasperUnregistered
    private static class TestQueryHandler_WithoutAnnotation extends QueryHandler<TestQuery, TestQueryResult> { }

    @XKasperUnregistered
    @XKasperCommandHandler(domain = TestDomain.class)
    private static class TestCommandHandler_WithNoTags extends CommandHandler<TestCommand> { }

    @XKasperUnregistered
    @XKasperCommandHandler(domain = TestDomain.class, tags = TEST_COMMAND_TAG)
    private static class TestCommandHandler_WithOneTag extends CommandHandler<TestCommand> { }

    @XKasperUnregistered
    @XKasperCommandHandler(domain = TestDomain.class, tags = {TEST_COMMAND_TAG, TEST_COMMAND_TAG_2})
    private static class TestCommandHandler_WithSeveralTags extends CommandHandler<TestCommand> { }

    @XKasperUnregistered
    private static class TestCommandHandler_WithoutAnnotation extends CommandHandler<TestCommand> { }
}
