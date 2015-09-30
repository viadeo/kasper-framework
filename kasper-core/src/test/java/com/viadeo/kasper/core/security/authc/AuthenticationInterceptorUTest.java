package com.viadeo.kasper.core.security.authc;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.api.annotation.XKasperCommand;
import com.viadeo.kasper.api.annotation.XKasperDomain;
import com.viadeo.kasper.api.annotation.XKasperQuery;
import com.viadeo.kasper.api.annotation.XKasperQueryResult;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.core.component.annotation.XKasperCommandHandler;
import com.viadeo.kasper.core.component.annotation.XKasperPublic;
import com.viadeo.kasper.core.component.command.AutowiredCommandHandler;
import com.viadeo.kasper.core.component.query.AutowiredQueryHandler;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.core.interceptor.CompositeInterceptorFactory;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.InterceptorFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationInterceptorUTest {

    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private MetricRegistry metricRegistry;

    private Authenticator authenticator;

    private Context context;
    private CompositeInterceptorFactory<Command, CommandResponse> commandFactory;
    private CompositeInterceptorFactory<Query, QueryResponse> queryFactory;

    @Before
    public void setUp() {
        context = Contexts.builder().build();

        authenticator = new InMemoryAuthentication(
                new UUIDAuthenticationTokenGenerator()
        );

        @SuppressWarnings("unchecked")
        ArrayList<InterceptorFactory<Command, CommandResponse>> commandInterceptorFactories = Lists.newArrayList(
                AuthenticationInterceptor.Factories.forCommand(metricRegistry, authenticator)
                ,
                new InterceptorFactory<Command, CommandResponse>() {
                    @Override
                    public Optional<InterceptorChain<Command, CommandResponse>> create(TypeToken<?> type) {
                        return Optional.of(
                                InterceptorChain.makeChain(new Interceptor<Command, CommandResponse>() {
                                                               @Override
                                                               public CommandResponse process(Command o, Context context, InterceptorChain chain) throws Exception {
                                                                   return CommandResponse.accepted();
                                                               }
                                                           }
                                ));
                    }
                }
        );
        commandFactory = new CompositeInterceptorFactory(commandInterceptorFactories);

        ArrayList<InterceptorFactory<Query, ? extends QueryResponse>> queryInterceptorFactories = Lists.newArrayList(
                AuthenticationInterceptor.Factories.forQuery(metricRegistry, authenticator)
                ,
                new InterceptorFactory<Query, QueryResponse>() {
                    @Override
                    public Optional<InterceptorChain<Query, QueryResponse>> create(TypeToken<?> type) {
                        return Optional.of(
                                InterceptorChain.makeChain(new Interceptor<Query, QueryResponse>() {
                                                               @Override
                                                               public QueryResponse process(Query o, Context context, InterceptorChain chain) throws Exception {
                                                                   return QueryResponse.of(new TestQueryResult());
                                                               }
                                                           }
                                ));
                    }
                }
        );
        queryFactory = new CompositeInterceptorFactory(queryInterceptorFactories);

    }

    // ========================================================================

    @Test(expected = KasperInvalidAuthenticationException.class)
    public void process_for_command_withNoTokenInContext_shouldReturnException() throws Exception {
        // Given
        InterceptorChain<Command, CommandResponse> chain = commandFactory.create(TypeToken.of(TestCommandHandler.class)).get();

        // When
        chain.next(new TestCommand(), context);
    }

    @Test(expected = KasperInvalidAuthenticationException.class)
    public void process_for_command_withTokenInContextButNotInStore_shouldReturnException() throws Exception {
        // Given
        InterceptorChain<Command, CommandResponse> chain = commandFactory.create(TypeToken.of(TestCommandHandler.class)).get();
        context = Contexts.newFrom(context).withAuthenticationToken("token").build();

        // When
        chain.next(new TestCommand(), context);
    }

    @Test
    public void process_for_command_withTokenInContextAndStore_shouldReturnAuthenticated() throws Exception {
        // Given
        InterceptorChain<Command, CommandResponse> chain = commandFactory.create(TypeToken.of(TestCommandHandler.class)).get();
        context = Contexts.newFrom(context).withAuthenticationToken(authenticator.createAuthenticationToken(context)).build();

        // When
        chain.next(new TestCommand(), context);
    }

    @Test
    public void process_for_publicCommand_withNoTokenInContext_shouldReturnAuthenticated() throws Exception {
        // Given
        InterceptorChain<Command, CommandResponse> chain = commandFactory.create(TypeToken.of(TestPublicCommandHandler.class)).get();

        // When
        chain.next(new TestCommand(), context);
    }

    // ========================================================================

    @Test(expected = KasperInvalidAuthenticationException.class)
    public void process_for_query_withNoTokenInContext_shouldReturnException() throws Exception {
        // Given
        InterceptorChain<Query, QueryResponse> chain = queryFactory.create(TypeToken.of(TestQueryHandler.class)).get();

        // When
        chain.next(new TestQuery(), context);
    }

    @Test(expected = KasperInvalidAuthenticationException.class)
    public void process_for_query_withTokenInContextButNotInStore_shouldReturnException() throws Exception {
        // Given
        InterceptorChain<Query, QueryResponse> chain = queryFactory.create(TypeToken.of(TestQueryHandler.class)).get();
        context = Contexts.newFrom(context).withAuthenticationToken("token").build();

        // When
        chain.next(new TestQuery(), context);
    }

    @Test
    public void process_for_query_withTokenInContextAndStore_shouldReturnAuthenticated() throws Exception {
        // Given
        InterceptorChain<Query, QueryResponse> chain = queryFactory.create(TypeToken.of(TestQueryHandler.class)).get();
        context = Contexts.newFrom(context).withAuthenticationToken(authenticator.createAuthenticationToken(context)).build();

        // When
        chain.next(new TestQuery(), context);
    }

    @Test
    public void process_for_publicQuery_withNoTokenInContext_shouldReturnAuthenticated() throws Exception {
        // Given
        InterceptorChain<Query, QueryResponse> chain = queryFactory.create(TypeToken.of(TestPublicQueryHandler.class)).get();

        // When
        chain.next(new TestQuery(), context);
    }

    // ========================================================================

    @XKasperDomain(
            label = "Authentication",
            prefix = "sec",
            description = "The Authentication domain",
            owner = "Emmanuel Camper <ecamper@viadeoteam.com>"
    )
    public static class Authentication implements Domain {
    }

    @XKasperCommand
    private static class TestCommand implements Command {
    }

    @XKasperQuery
    private static class TestQuery implements Query {
    }

    @XKasperQueryResult
    private static class TestQueryResult implements QueryResult {
    }

    @XKasperCommandHandler(description = "", domain = Authentication.class)
    private static class TestCommandHandler extends AutowiredCommandHandler<TestCommand> {

    }

    @XKasperPublic
    @XKasperCommandHandler(description = "", domain = Authentication.class)
    private static class TestPublicCommandHandler extends AutowiredCommandHandler<TestCommand> {

    }

    @XKasperQueryHandler(description = "", domain = Authentication.class)
    private static class TestQueryHandler extends AutowiredQueryHandler<TestQuery, TestQueryResult> {

    }

    @XKasperPublic
    @XKasperQueryHandler(description = "", domain = Authentication.class)
    private static class TestPublicQueryHandler extends AutowiredQueryHandler<TestQuery, TestQueryResult> {

    }

}