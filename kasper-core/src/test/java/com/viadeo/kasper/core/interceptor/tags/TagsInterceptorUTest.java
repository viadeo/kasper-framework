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
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor.tags;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.context.Tags;
import com.viadeo.kasper.core.TestDomain;
import com.viadeo.kasper.core.component.annotation.XKasperCommandHandler;
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.component.command.AutowiredCommandHandler;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.ResetMdcContextMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.MDC;

import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static com.viadeo.kasper.api.context.Context.TAGS_SHORTNAME;
import static com.viadeo.kasper.core.TestDomain.TestCommand;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TagsInterceptorUTest {

    static final Context DEFAULT_CONTEXT = Contexts.empty();
    static final Object INPUT = new Object();
    static final Object OUTPUT = new Object();

    //// setup

    @Mock
    InterceptorChain<Object, Object> chain;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public ResetTagsCache resetTagsCache = new ResetTagsCache();

    @Rule
    public ResetMdcContextMap resetMdcContextMap = new ResetMdcContextMap();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    //// process

    @Test
    public void process_WithoutTagsOnTheHandlerAndWhenCallToNextSucceeds_ShouldDoNothing() throws Exception {
        // Given
        @XKasperUnregistered
        @XKasperCommandHandler(domain = TestDomain.class)
        class TestCommandHandler extends AutowiredCommandHandler<TestCommand> {
        }

        when(chain.next(same(INPUT), eq(DEFAULT_CONTEXT)))
                .thenReturn(OUTPUT);

        // When
        final TagsInterceptor<Object,Object> tagsInterceptor = interceptor(TestCommandHandler.class);
        final Object result = tagsInterceptor.process(INPUT, DEFAULT_CONTEXT, chain);

        // Then
        assertEquals(OUTPUT, result);
    }

    @Test
    public void process_WithoutTagsOnTheHandlerAndWhenCallToNextThrows_ShouldDoNothing() throws Exception {
        // Given
        @XKasperUnregistered
        @XKasperCommandHandler(domain = TestDomain.class)
        class TestCommandHandler extends AutowiredCommandHandler<TestCommand> {
        }

        // Expect
        final RuntimeException exception = new RuntimeException();
        when(chain.next(same(INPUT), eq(DEFAULT_CONTEXT)))
                .thenThrow(exception);
        thrown.expect(sameInstance(exception));

        // When
        final TagsInterceptor<Object,Object> tagsInterceptor = interceptor(TestCommandHandler.class);
        tagsInterceptor.process(INPUT, DEFAULT_CONTEXT, chain);
    }

    @Test
    public void process_WithTagsOnTheHandler_ShouldAddThemToTheContextAndMdcContextMapForTheNextHandlerInTheChain() throws Exception {
        // Given
        final String tagOnHandler = "this-is-a-tag";
        final String otherTagOnHandler = "this-is-another-tag";

        @XKasperUnregistered
        @XKasperCommandHandler(domain = TestDomain.class, tags = {tagOnHandler, otherTagOnHandler})
        class TestCommandHandler extends AutowiredCommandHandler<TestCommand> {
        }
        final Set<String> tagsOnHandler = newHashSet(tagOnHandler, otherTagOnHandler);

        // Expect
        when(chain.next(same(INPUT), any(Context.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final Object[] arguments = invocation.getArguments();
                final Context alteredContext = (Context) arguments[1];

                final Set<String> tagsInContext = alteredContext.getTags();
                assertEquals(tagsOnHandler, tagsInContext);

                final Set<String> tagsInMdcContextMap = getMdcContextMap();
                assertEquals(tagsOnHandler, tagsInMdcContextMap);

                return OUTPUT;
            }
        });

        // When
        final TagsInterceptor<Object,Object> tagsInterceptor = interceptor(TestCommandHandler.class);
        final Object result = tagsInterceptor.process(INPUT, DEFAULT_CONTEXT, chain);

        // Then
        assertEquals(OUTPUT, result);
    }

    @Test
    public void process_WithTagsAlreadyInContext_ShouldAddHandlerTagsToTheExistingOnes() throws Exception {
        // Given
        final String tagOnHandler = "this-is-a-tag";
        final String otherTagOnHandler = "this-is-another-tag";

        @XKasperUnregistered
        @XKasperCommandHandler(domain = TestDomain.class, tags = {tagOnHandler, otherTagOnHandler})
        class TestCommandHandler extends AutowiredCommandHandler<TestCommand> {
        }
        final Set<String> tagsOnHandler = newHashSet(tagOnHandler, otherTagOnHandler);

        final Set<String> tagsAlreadyInContext = newHashSet("a-tag-already-in-context");
        final Context context = new Context.Builder().withTags(tagsAlreadyInContext).build();

        final Set<String> tagsAlreadyInMdcContextMap = newHashSet("a-tag-already-in-mdc-context-map");
        setMdcContextMap(tagsAlreadyInMdcContextMap);

        // Expect
        when(chain.next(same(INPUT), any(Context.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final Object[] arguments = invocation.getArguments();
                final Context alteredContext = (Context) arguments[1];

                final Set<String> tagsInContext = alteredContext.getTags();
                final Set<String> expectedTagsInContext = Sets.union(tagsAlreadyInContext, tagsOnHandler);
                assertEquals(expectedTagsInContext, tagsInContext);

                final Set<String> tagsInMdcContextMap = getMdcContextMap();
                final Set<String> expectedTagsInContextMap = Sets.union(tagsAlreadyInMdcContextMap, tagsOnHandler);
                assertEquals(expectedTagsInContextMap, tagsInMdcContextMap);

                return OUTPUT;
            }
        });

        // When
        final TagsInterceptor<Object,Object> tagsInterceptor = interceptor(TestCommandHandler.class);
        final Object result = tagsInterceptor.process(INPUT, context, chain);

        // Then
        assertEquals(OUTPUT, result);
    }

    @Test
    public void process_WithTagsOnTheHandler_ShouldRestoreMdcContextMapAfterExecutionOfNextHandlerInTheChain() throws Exception {
        // Given
        final String tagOnHandler = "this-is-a-tag";
        final String otherTagOnHandler = "this-is-another-tag";

        @XKasperUnregistered
        @XKasperCommandHandler(domain = TestDomain.class, tags = {tagOnHandler, otherTagOnHandler})
        class TestCommandHandler extends AutowiredCommandHandler<TestCommand> {
        }

        final Set<String> tagsAlreadyInMdcContextMap = newHashSet("a-tag-already-in-mdc-context-map");
        setMdcContextMap(tagsAlreadyInMdcContextMap);

        // When
        final TagsInterceptor<Object,Object> tagsInterceptor = interceptor(TestCommandHandler.class);
        tagsInterceptor.process(INPUT, DEFAULT_CONTEXT, chain);

        // Then
        verify(chain).next(same(INPUT), any(Context.class));
        final Set<String> tagsInMdcContextMap = getMdcContextMap();
        assertEquals(tagsAlreadyInMdcContextMap, tagsInMdcContextMap);
    }

    @Test
    public void process_WithTagsOnTheHandler_ShouldRestoreMdcContextMapAfterExecutionOfNextHandlerInTheChainFails() throws Exception {
        // Given
        final String tagOnHandler = "this-is-a-tag";
        final String otherTagOnHandler = "this-is-another-tag";

        @XKasperUnregistered
        @XKasperCommandHandler(domain = TestDomain.class, tags = {tagOnHandler, otherTagOnHandler})
        class TestCommandHandler extends AutowiredCommandHandler<TestCommand> {
        }

        final Set<String> tagsAlreadyInMdcContextMap = newHashSet("a-tag-already-in-mdc-context-map");
        setMdcContextMap(tagsAlreadyInMdcContextMap);

        // Expect
        final RuntimeException expectedException = new RuntimeException();
        when(chain.next(same(INPUT), any(Context.class)))
                .thenThrow(expectedException);

        try {
            // When
            final TagsInterceptor<Object,Object> tagsInterceptor = interceptor(TestCommandHandler.class);
            tagsInterceptor.process(INPUT, DEFAULT_CONTEXT, chain);
            fail("should have thrown at this point");
        } catch (Exception e) {
            // Then
            assertSame(expectedException, e);
            final Set<String> tagsInMdcContextMap = getMdcContextMap();
            assertEquals(tagsAlreadyInMdcContextMap, tagsInMdcContextMap);
        }

    }

    // ------------------------------------------------------------------------

    private static TagsInterceptor<Object,Object> interceptor(Class<?> type) {
        return new TagsInterceptor<>(TypeToken.of(type));
    }

    private static Set<String> getMdcContextMap() {
        final String tagsAsString = MDC.get(TAGS_SHORTNAME);
        return Tags.valueOf(tagsAsString);
    }

    private static void setMdcContextMap(Set<String> tagsAlreadyInMdcContextMap) {
        final String tags = Tags.toString(tagsAlreadyInMdcContextMap);
        final Map<String, String> contextMap = ImmutableMap.of(TAGS_SHORTNAME, tags);
        MDC.setContextMap(contextMap);
    }

}
