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
package com.viadeo.kasper.exposition.http;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sun.jersey.api.client.ClientResponse;
import com.viadeo.kasper.api.annotation.XKasperAlias;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.api.response.KasperResponse.Status;
import com.viadeo.kasper.client.HTTPCommandResponse;
import com.viadeo.kasper.common.exposition.HttpContextHeaders;
import com.viadeo.kasper.core.component.annotation.XKasperCommandHandler;
import com.viadeo.kasper.core.component.annotation.XKasperUnexposed;
import com.viadeo.kasper.core.component.command.AutowiredCommandHandler;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.command.CommandMessage;
import com.viadeo.kasper.core.component.command.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.component.command.repository.Repository;
import com.viadeo.kasper.core.component.event.interceptor.EventInterceptorFactory;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.platform.bundle.DefaultDomainBundle;
import com.viadeo.kasper.platform.bundle.DomainBundle;
import org.junit.Test;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.*;
import java.util.*;

import static org.junit.Assert.*;

public class HttpCommandExposerTest extends BaseHttpExposerTest {

    private static final String SECURITY_TOKEN = "42-4242-24-2424";
    private static final String AUTHENTICATION_TOKEN = "42-4242-24-2424";
    public static final String NEED_VALIDATION_2_ALIAS = "needvalidation2";

    public static class CreateAccountCommand implements Command {
        private static final long serialVersionUID = 424842094873929150L;

        private Optional<Long> delay = Optional.absent();
        private String name;
        private boolean throwException;
        private String code;
        private List<String> messages;

        public Optional<Long> getDelay() {
            return delay;
        }

        public String getName() {
            return this.name;
        }

        public boolean isThrowException() {
            return throwException;
        }

        public String getCode() {
            return code;
        }

        public List<String> getMessages() {
            return messages;
        }

    }

    // ------------------------------------------------------------------------

    @XKasperCommandHandler(domain = AccountDomain.class)
    public static class CreateAccountCommandHandler extends AutowiredCommandHandler<CreateAccountCommand> {
        static String createAccountCommandName = null;

        @Override
        public CommandResponse handle(final CreateAccountCommand command) {
            if (command.getDelay().isPresent())
                try {
                    Thread.sleep(command. getDelay().get());
                } catch (InterruptedException e) {
                    return CommandResponse.error(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, e));
                }
            if (command.isThrowException())
                throw new KasperException("Something bad happened!");
            if (command.getCode() != null)
                return CommandResponse.error(new KasperReason(command.getCode(), command.getMessages()));
            createAccountCommandName = command.getName();
            return CommandResponse.ok().withSecurityToken(SECURITY_TOKEN).withAuthenticationToken(AUTHENTICATION_TOKEN);
        }
    }

    public static class NeedValidationCommand implements Command {
        private static final long serialVersionUID = -6767141217213758937L;
        @NotNull @Size(min = 1) public String str;
        @Valid @NotNull public InnerObject innerObject;
    }

    public static class InnerObject {
        @Min(2) @Max(5) private int age;
    }

    @XKasperCommandHandler(domain = AccountDomain.class)
    public static class NeedValidationCommandHandler extends AutowiredCommandHandler<NeedValidationCommand> { }

    @XKasperAlias(values = {NEED_VALIDATION_2_ALIAS})
    public static class NeedValidationWithAlias implements Command {
        private static final long serialVersionUID = -8083928873466120009L;
    }

    @XKasperCommandHandler(domain = AccountDomain.class)
    public static class NeedValidationWithAliasCommandHandler extends AutowiredCommandHandler<NeedValidationWithAlias> {

        @Override
        public CommandResponse handle(NeedValidationWithAlias command) {
            return CommandResponse.ok();
        }
    }

    public static class UnexposedCommand implements Command {}

    public static class ContextCheckCommand implements Command {
        private static final long serialVersionUID = 674842094842929150L;

        private String contextName;

        public ContextCheckCommand(final String contextName) {
            this.contextName = contextName;
        }

        public String getContextName() {
            return this.contextName;
        }
    }

    public static final String RETURNED_SECURITY_TOKEN = UUID.randomUUID().toString();

    @XKasperCommandHandler(domain = AccountDomain.class)
    public static class ContextCheckCommandHandler extends AutowiredCommandHandler<ContextCheckCommand> {
        @Override
        public CommandResponse handle(final CommandMessage<ContextCheckCommand> message) {
            return CommandResponse.ok().withSecurityToken(RETURNED_SECURITY_TOKEN);
        }
    }

    @XKasperUnexposed
    @XKasperCommandHandler(domain = AccountDomain.class)
    public static class UnexposedCommandHandler extends AutowiredCommandHandler<UnexposedCommand> {
        @Override
        public CommandResponse handle(UnexposedCommand command) {
            return CommandResponse.ok();
        }
    }

    // ------------------------------------------------------------------------

    public HttpCommandExposerTest() {
        Locale.setDefault(Locale.US);
    }

    // ------------------------------------------------------------------------

    @Override
    protected HttpExposer getHttpExposer() {
        return httpExposurePlugin.getCommandExposer();
    }

    @Override
    protected DomainBundle getDomainBundle(){

        return new DefaultDomainBundle(
                  Lists.<CommandHandler>newArrayList(
                          new NeedValidationCommandHandler(),
                          new CreateAccountCommandHandler(),
                          new NeedValidationWithAliasCommandHandler(),
                          new UnexposedCommandHandler(),
                          new ContextCheckCommandHandler()
                  )
                , Lists.<QueryHandler>newArrayList()
                , Lists.<Repository>newArrayList()
                , Lists.<EventListener>newArrayList()
                , Lists.<Saga>newArrayList()
                , Lists.<QueryInterceptorFactory>newArrayList()
                , Lists.<CommandInterceptorFactory>newArrayList()
                , Lists.<EventInterceptorFactory>newArrayList()
                , new AccountDomain()
                , "AccountDomain"
        );
    }

    // ------------------------------------------------------------------------

    @Test
    public void testCommandNotFound() throws Exception {
        // Given an unknown command
        @SuppressWarnings("serial")
        final Command unknownCommand = new Command() {};

        // When
        final CommandResponse response = client().send(Contexts.empty(), unknownCommand);

        // Then
        assertEquals(Status.ERROR, response.getStatus());
        assertNotNull(response.getReason().getMessages().toArray()[0]);
        assertFalse(response.getSecurityToken().isPresent());
    }

    // ------------------------------------------------------------------------

    @Test
    public void testSuccessfulCommand() throws Exception {
        // Given valid input
        final CreateAccountCommand command = new CreateAccountCommand();
        command.name = "foo bar";

        // When
        final CommandResponse response = client().send(Contexts.empty(), command);

        // Then
        assertEquals(Status.OK, response.getStatus());
        assertEquals(command.name, CreateAccountCommandHandler.createAccountCommandName);
        assertTrue(response.getSecurityToken().isPresent());
        assertEquals(SECURITY_TOKEN, response.getSecurityToken().get());
        assertTrue(response.getAuthenticationToken().isPresent());
        assertEquals(AUTHENTICATION_TOKEN, response.getAuthenticationToken().get());
    }

    // ------------------------------------------------------------------------

    @Test
    public void testCommandWithFullContext() throws Exception {
        // Given
        final Command command = new ContextCheckCommand(getContextName());

        // When
        final CommandResponse response = client().send(getFullContext(), command);

        // Then
        assertTrue(response.isOK());
        assertTrue(response.getSecurityToken().isPresent());
        assertEquals(RETURNED_SECURITY_TOKEN, response.getSecurityToken().get());
    }

    // ------------------------------------------------------------------------

    @Test
    public void testCommand_withSyncCall_isOk() throws Exception {
        // Given valid input
        final CreateAccountCommand command = new CreateAccountCommand();
        command.name = "foo bar";

        Context context = Contexts.builder()
                .with(Context.CALL_TYPE, CallTypes.SYNC.name())
                .build();

        // When
        final CommandResponse response = client().send(context, command);

        // Then
        assertEquals(Status.OK, response.getStatus());
    }

    @Test
    public void testCommand_withAsyncCall_isAccepted() throws Exception {
        // Given valid input
        final CreateAccountCommand command = new CreateAccountCommand();
        command.name = "foo bar";

        Context context = Contexts.builder()
                .with(Context.CALL_TYPE, CallTypes.ASYNC.name())
                .build();

        // When
        final CommandResponse response = client().send(context, command);

        // Then
        assertEquals(Status.ACCEPTED, response.getStatus());
    }

    @Test
    public void testCommand_withTimeCall_isOk() throws Exception {
        // Given valid input
        final CreateAccountCommand command = new CreateAccountCommand();
        command.name = "foo bar";

        Context context = Contexts.builder()
                .with(Context.CALL_TYPE, "time(100)")
                .build();

        // When
        final CommandResponse response = client().send(context, command);

        // Then
        assertEquals(Status.OK, response.getStatus());
    }

    @Test
    public void testCommand_withExceededTimeCall_isAccepted() throws Exception {
        // Given valid input
        final CreateAccountCommand command = new CreateAccountCommand();
        command.name = "foo bar";
        command.delay = Optional.of(500L);

        Context context = Contexts.builder()
                .with(Context.CALL_TYPE, "time(100)")
                .build();

        // When
        final CommandResponse response = client().send(context, command);

        // Then
        assertEquals(Status.ACCEPTED, response.getStatus());
    }

    @Test
    public void testCommand_withTimeCall_withUnexpectedExecutionException_isInError() throws Exception {
        // Given valid input
        final CreateAccountCommand command = new CreateAccountCommand();
        command.name = "foo bar";
        command.code = CoreReasonCode.CONFLICT.name();
        command.messages = ImmutableList.of("ignored");

        Context context = Contexts.builder()
                .with(Context.CALL_TYPE, "time(100)")
                .build();

        // When
        final CommandResponse response = client().send(context, command);

        // Then
        assertEquals(Status.ERROR, response.getStatus());
        assertEquals("ignored", response.getReason().getMessages().toArray()[0]);
    }

    // ------------------------------------------------------------------------

    @Test
    public void testExceptionCommand() throws Exception {
        // Given valid input
        final CreateAccountCommand command = new CreateAccountCommand();
        command.name = "foo bar";
        command.throwException = true;

        // When
        final CommandResponse response = client().send(Contexts.empty(), command);

        // Then
        assertEquals(Status.ERROR, response.getStatus());
    }

    @Test
    public void testCommandResponseWithStatusCode() throws Exception {
        // Given valid input
        final CreateAccountCommand command = new CreateAccountCommand();
        command.code = CoreReasonCode.CONFLICT.name();
        command.messages = ImmutableList.of("ignored");

        // When
        final CommandResponse response = client().send(Contexts.empty(), command);

        // Then
        assertEquals(Status.ERROR, response.getStatus());
        assertEquals(command.getCode(), response.getReason().getCode());
        assertTrue(response instanceof HTTPCommandResponse);

        HTTPCommandResponse httpResponse = (HTTPCommandResponse) response;
        assertEquals(Response.Status.CONFLICT, httpResponse.getHTTPStatus());
    }

    // ------------------------------------------------------------------------

    @Test
    public void testCommandResponseWithListOfErrors() throws Exception {
        // Given valid input
        final CreateAccountCommand command = new CreateAccountCommand();
        command.code = "code";
        command.messages = ImmutableList.of("a", "aa", "aaa");

        // When
        final CommandResponse response = client().send(Contexts.empty(), command);

        // Then
        assertEquals(Status.ERROR, response.getStatus());
        assertEquals(command.getCode(), response.getReason().getCode());

        final Collection<String> messages = response.getReason().getMessages();
        final String[] responseMessages = messages.toArray(new String[messages.size()]);

        for (int i = 0; i < command.getMessages().size(); i++) {
            assertEquals(command.getMessages().get(i), responseMessages[i]);
        }
    }

    @Test
    public void testJSR303Validation() {
        // Given
        final NeedValidationCommand command = new NeedValidationCommand();
        command.str = "";
        command.innerObject = new InnerObject();

        // When
        final CommandResponse response = client().send(Contexts.empty(), command);

        // Then
        assertFalse(response.isOK());

        final List<String> errorStrings = new ArrayList<>();
        errorStrings.add("innerObject.age : must be greater than or equal to 2");
        errorStrings.add("str : size must be between 1 and 2147483647");

        for (final String errorMessage : response.getReason().getMessages()) {
            if ( ! errorStrings.contains(errorMessage)) {
                fail(String.format("Cannot find expected validation message : %s", errorMessage));
            }
            errorStrings.remove(errorMessage);
        }
        assertEquals(0, errorStrings.size());
    }

    @Test
    public void testAliasedCommand() throws MalformedURLException, URISyntaxException {
        // Given
        final String commandPath = NEED_VALIDATION_2_ALIAS;
        final NeedValidationWithAlias needValidationWithAlias = new NeedValidationWithAlias();

        // When
        final ClientResponse response = httpClient()
                .resource(new URL(new URL(url()), commandPath).toURI())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .put(ClientResponse.class, needValidationWithAlias);

        // Then
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testXKasperServerNameInHeader() throws MalformedURLException, URISyntaxException, UnknownHostException {
        // Given
        final String expectedServerName = InetAddress.getLocalHost().getCanonicalHostName();
        final String commandPath = NEED_VALIDATION_2_ALIAS;
        final NeedValidationWithAlias needValidationWithAlias = new NeedValidationWithAlias();

        // When
        final ClientResponse response = httpClient()
                .resource(new URL(new URL(url()), commandPath).toURI())
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .put(ClientResponse.class, needValidationWithAlias);

        // Then
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(expectedServerName, response.getHeaders().getFirst(HttpContextHeaders.HEADER_SERVER_NAME.toHeaderName()));
    }

    @Test
    public void testUnexposedCommandHandler() {
        // Given
        final UnexposedCommand command = new UnexposedCommand();

        // When
        final CommandResponse response = client().send(Contexts.empty(), command);

        // Then
        assertEquals(Status.ERROR, response.getStatus());
        assertNotNull(response.getReason().getMessages().toArray()[0]);
        assertFalse(response.getSecurityToken().isPresent());
    }

    // ------------------------------------------------------------------------


}















