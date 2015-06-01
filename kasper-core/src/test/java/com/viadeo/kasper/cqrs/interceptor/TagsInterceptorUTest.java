// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.interceptor;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.Tags;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
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
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.MDC;

import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TagsInterceptorUTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public SaveAndRestoreMdcContextMap preserveLogs = new SaveAndRestoreMdcContextMap();

    //// process

    @Test
    @SuppressWarnings("unchecked")
    public void process_WithTagsOnTheHandler_ShouldAddThemToTheContextAndMdcContextMapForTheNextHandlerInTheChain() throws Exception {
        // Given
        @XKasperUnregistered
        @XKasperQueryHandler(domain = TestDomain.class, tags = {TEST_COMMAND_TAG, TEST_COMMAND_TAG_2})
        class TestQueryHandler extends QueryHandler<TestQuery, TestQueryResult> {
        }
        final TagsInterceptor<Object> tagsInterceptor = new TagsInterceptor<>(TypeToken.of(TestQueryHandler.class));

        final Object input = new Object();
        final InterceptorChain<Object, Object> chain = mock(InterceptorChain.class);
        final Context context = new Context.Builder().build();

        // Expect
        when(chain.next(anyObject(), any(Context.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        final Object[] arguments = invocation.getArguments();
                        assertSame(input, arguments[0]);
                        assertThat(arguments[1], instanceOf(Context.class));
                        final Context alteredContext = (Context) arguments[1];

                        final Set<String> tags = newHashSet(TEST_COMMAND_TAG, TEST_COMMAND_TAG_2);
                        final String formattedTags = Tags.toString(tags);

                        assertThat(alteredContext.getTags(), equalTo(tags));
                        assertThat(MDC.get(Context.TAGS_SHORTNAME), equalTo(formattedTags));

                        return null;
                    }
                });

        // When
        tagsInterceptor.process(input, context, chain);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void process_WithTagsAlreadyInContext_ShouldAddHandlerTagsToTheExistingOnes() throws Exception {
        // Given
        @XKasperUnregistered
        @XKasperQueryHandler(domain = TestDomain.class, tags = {TEST_COMMAND_TAG, TEST_COMMAND_TAG_2})
        class TestQueryHandler extends QueryHandler<TestQuery, TestQueryResult> {
        }
        final TagsInterceptor<Object> tagsInterceptor = new TagsInterceptor<>(TypeToken.of(TestQueryHandler.class));

        final Object input = new Object();
        final InterceptorChain<Object, Object> chain = mock(InterceptorChain.class);

        final String aTagAlreadyInContext = "a-tag-already-in-context";
        final Context context = new Context.Builder().withTags(newHashSet(aTagAlreadyInContext)).build();
        final String aTagAlreadyInMdcContextMap = "a-tag-already-in-mdc-context-map";
        MDC.setContextMap(ImmutableMap.of(Context.TAGS_SHORTNAME, aTagAlreadyInMdcContextMap));

        // Expect
        when(chain.next(anyObject(), any(Context.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        final Object[] arguments = invocation.getArguments();
                        final Context alteredContext = (Context) arguments[1];

                        final Set<String> tagsInContext = alteredContext.getTags();
                        final Set<String> expectedTagsInContext = newHashSet(aTagAlreadyInContext, TEST_COMMAND_TAG, TEST_COMMAND_TAG_2);
                        assertThat(tagsInContext, equalTo(expectedTagsInContext));

                        final String tagsInMdcContextMap = MDC.get(Context.TAGS_SHORTNAME);
                        assertThat(tagsInMdcContextMap, containsString(aTagAlreadyInMdcContextMap));
                        assertThat(tagsInMdcContextMap, containsString(TEST_COMMAND_TAG));
                        assertThat(tagsInMdcContextMap, containsString(TEST_COMMAND_TAG_2));

                        return null;
                    }
                });

        // When
        tagsInterceptor.process(input, context, chain);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void process_WithTagsOnTheHandler_ShouldRestoreMdcContextMapAfterExecutionOfNextHandlerInTheChain() throws Exception {
        // Given
        @XKasperUnregistered
        @XKasperQueryHandler(domain = TestDomain.class, tags = {TEST_COMMAND_TAG, TEST_COMMAND_TAG_2})
        class TestQueryHandler extends QueryHandler<TestQuery, TestQueryResult> {
        }
        final TagsInterceptor<Object> tagsInterceptor = new TagsInterceptor<>(TypeToken.of(TestQueryHandler.class));

        final Object input = new Object();
        final InterceptorChain<Object, Object> chain = mock(InterceptorChain.class);

        final String aTagAlreadyInContext = "a-tag-already-in-context";
        final Context context = new Context.Builder().withTags(newHashSet(aTagAlreadyInContext)).build();
        final String aTagAlreadyInMdcContextMap = "a-tag-already-in-mdc-context-map";
        MDC.setContextMap(ImmutableMap.of(Context.TAGS_SHORTNAME, aTagAlreadyInMdcContextMap));

        // When
        tagsInterceptor.process(input, context, chain);

        // Then
        final String tagsInMdcContextMap = MDC.get(Context.TAGS_SHORTNAME);
        assertThat(tagsInMdcContextMap, containsString(aTagAlreadyInMdcContextMap));
        assertThat(tagsInMdcContextMap, not(containsString(TEST_COMMAND_TAG)));
        assertThat(tagsInMdcContextMap, not(containsString(TEST_COMMAND_TAG_2)));
    }

    //// retrieveTags

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

    private static class SaveAndRestoreMdcContextMap implements TestRule {
        @Override
        public Statement apply(final Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    Map originalContextMap = Objects.firstNonNull(MDC.getCopyOfContextMap(), newHashMap());
                    try {
                        base.evaluate();
                    } finally {
                        MDC.setContextMap(originalContextMap);
                    }
                }
            };
        }
    }
}
