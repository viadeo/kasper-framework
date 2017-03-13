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
package com.viadeo.kasper.core.interceptor.authentication;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import com.viadeo.kasper.api.id.ID;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.core.component.annotation.XKasperCommandHandler;
import com.viadeo.kasper.core.component.annotation.XKasperPublic;
import com.viadeo.kasper.core.component.command.AutowiredCommandHandler;
import com.viadeo.kasper.core.component.command.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.component.query.AutowiredQueryHandler;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.core.id.TestFormats;
import com.viadeo.kasper.core.interceptor.CompositeInterceptorFactory;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.InterceptorFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationInterceptorUTest {

    private InMemoryAuthentication authenticator;
    private AuthenticationTokenGenerator<String> authenticationTokenGenerator;

    private Context context;
    private CompositeInterceptorFactory<Command, CommandResponse> commandFactoryOk;
    private CompositeInterceptorFactory<Command, CommandResponse> commandFactoryAuthenticated;
    private CompositeInterceptorFactory<Command, CommandResponse> commandFactoryError;
    private CompositeInterceptorFactory<Query, QueryResponse> queryFactory;

    @Before
    public void setUp() {
        context = Contexts.builder().build();

        InMemoryAuthentication inMemoryAuthentication = new InMemoryAuthentication();

        authenticator = inMemoryAuthentication;
        authenticationTokenGenerator = inMemoryAuthentication;

        CommandInterceptorFactory factory = AuthenticationInterceptor.Factories.forCommand(authenticator, authenticationTokenGenerator);
        @SuppressWarnings("unchecked")
        ArrayList<InterceptorFactory<Command, CommandResponse>> commandInterceptorFactoriesResponseOk = Lists.newArrayList(
                factory,
                new InterceptorFactory<Command, CommandResponse>() {
                    @Override
                    public Optional<InterceptorChain<Command, CommandResponse>> create(TypeToken<?> type) {
                        return Optional.of(
                                InterceptorChain.makeChain(new Interceptor<Command, CommandResponse>() {
                                                               @Override
                                                               public CommandResponse process(Command o, Context context, InterceptorChain chain) {
                                                                   return CommandResponse.ok();
                                                               }
                                                           }
                                ));
                    }
                }
        );
        @SuppressWarnings("unchecked")
        ArrayList<InterceptorFactory<Command, CommandResponse>> commandInterceptorFactoriesResponseAuthenticated = Lists.newArrayList(
                factory,
                new InterceptorFactory<Command, CommandResponse>() {
                    @Override
                    public Optional<InterceptorChain<Command, CommandResponse>> create(TypeToken<?> type) {
                        return Optional.of(
                                InterceptorChain.makeChain(new Interceptor<Command, CommandResponse>() {
                                                               @Override
                                                               public CommandResponse process(Command o, Context context, InterceptorChain chain) {
                                                                   return CommandResponse.doAuthenticate(new ID("kasper", "subject", TestFormats.UUID, UUID.randomUUID()));
                                                               }
                                                           }
                                ));
                    }
                }
        );
        ArrayList<InterceptorFactory<Command, CommandResponse>> commandInterceptorFactoriesResponseError = Lists.newArrayList(
                factory,
                new InterceptorFactory<Command, CommandResponse>() {
                    @Override
                    public Optional<InterceptorChain<Command, CommandResponse>> create(TypeToken<?> type) {
                        return Optional.of(
                                InterceptorChain.makeChain(new Interceptor<Command, CommandResponse>() {
                                                               @Override
                                                               public CommandResponse process(Command o, Context context, InterceptorChain chain) {
                                                                   return CommandResponse.error(CoreReasonCode.INTERNAL_COMPONENT_ERROR);
                                                               }
                                                           }
                                ));
                    }
                }
        );
        commandFactoryOk = new CompositeInterceptorFactory(commandInterceptorFactoriesResponseOk);
        commandFactoryAuthenticated = new CompositeInterceptorFactory(commandInterceptorFactoriesResponseAuthenticated);
        commandFactoryError = new CompositeInterceptorFactory(commandInterceptorFactoriesResponseError);

        ArrayList<InterceptorFactory<Query, ? extends QueryResponse>> queryInterceptorFactories = Lists.newArrayList(
                AuthenticationInterceptor.Factories.forQuery(authenticator, authenticationTokenGenerator)
                ,
                new InterceptorFactory<Query, QueryResponse>() {
                    @Override
                    public Optional<InterceptorChain<Query, QueryResponse>> create(TypeToken<?> type) {
                        return Optional.of(
                                InterceptorChain.makeChain(new Interceptor<Query, QueryResponse>() {
                                                               @Override
                                                               public QueryResponse process(Query o, Context context, InterceptorChain chain) {
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
        InterceptorChain<Command, CommandResponse> chain = commandFactoryOk.create(TypeToken.of(TestCommandHandler.class)).get();

        // When
        chain.next(new TestCommand(), context);
    }

    @Test(expected = KasperInvalidAuthenticationException.class)
    public void process_for_command_withTokenInContextButNotInStore_shouldReturnException() throws Exception {
        // Given
        InterceptorChain<Command, CommandResponse> chain = commandFactoryOk.create(TypeToken.of(TestCommandHandler.class)).get();
        context = Contexts.newFrom(context).withAuthenticationToken("token").build();

        // When
        chain.next(new TestCommand(), context);
    }

    @Test
    public void process_for_command_withTokenInContextAndStore_shouldReturnAuthenticated() throws Exception {
        // Given
        InterceptorChain<Command, CommandResponse> chain = commandFactoryOk.create(TypeToken.of(TestCommandHandler.class)).get();
        ID subjectID = new ID("kasper", "subject", TestFormats.UUID, UUID.randomUUID());
        String token = authenticationTokenGenerator.generate(subjectID, Maps.<String, Object>newHashMap());
        authenticator.addToken(token, subjectID);
        context = Contexts.newFrom(context).withAuthenticationToken(token).build();

        // When
        chain.next(new TestCommand(), context);
    }

    @Test
    public void process_for_publicCommand_withNoTokenInContext_shouldReturnAuthenticated() throws Exception {
        // Given
        InterceptorChain<Command, CommandResponse> chain = commandFactoryOk.create(TypeToken.of(TestPublicCommandHandler.class)).get();

        // When
        CommandResponse output = chain.next(new TestCommand(), context);
        assertFalse(output.getAuthenticationToken().isPresent());
    }

    @Test
    public void process_for_publicCommandWithTokenCreate_withNoTokenInContext_shouldCreateAuthenticationTokenInResponse() throws Exception {
        // Given
        InterceptorChain<Command, CommandResponse> chain = commandFactoryAuthenticated.create(TypeToken.of(TestPublicWithTokenCreateCommandHandler.class)).get();

        // When
        CommandResponse output = chain.next(new TestCommand(), context);
        assertTrue(output.getAuthenticationToken().isPresent());
    }

    @Test
    public void process_for_publicCommandWithTokenCreate_withNoTokenInContext_witCommandReturningError_shouldNotCreateAuthenticationTokenInResponse() throws Exception {
        // Given
        InterceptorChain<Command, CommandResponse> chain = commandFactoryError.create(TypeToken.of(TestPublicWithTokenCreateCommandHandler.class)).get();

        // When
        CommandResponse output = chain.next(new TestCommand(), context);
        assertFalse(output.getAuthenticationToken().isPresent());
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
        ID subjectID = new ID("kasper", "subject", TestFormats.UUID, UUID.randomUUID());
        context = Contexts.newFrom(context).withUserID(subjectID).build();
        String token = authenticationTokenGenerator.generate(subjectID, Maps.<String, Object>newHashMap());
        authenticator.addToken(token, subjectID);
        context = Contexts.newFrom(context).withAuthenticationToken(token).build();

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

    @XKasperPublic
    @XKasperCommandHandler(description = "", domain = Authentication.class)
    private static class TestPublicWithTokenCreateCommandHandler extends AutowiredCommandHandler<TestCommand> {

    }

    @XKasperQueryHandler(description = "", domain = Authentication.class)
    private static class TestQueryHandler extends AutowiredQueryHandler<TestQuery, TestQueryResult> {

    }

    @XKasperPublic
    @XKasperQueryHandler(description = "", domain = Authentication.class)
    private static class TestPublicQueryHandler extends AutowiredQueryHandler<TestQuery, TestQueryResult> {

    }

}
