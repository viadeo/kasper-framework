// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.client.platform.configuration.KasperPlatformConfiguration;
import com.viadeo.kasper.client.platform.domain.DefaultDomainBundle;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.cqrs.query.*;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.exception.KasperSecurityException;
import com.viadeo.kasper.security.IdentityContextProvider;
import com.viadeo.kasper.security.SecurityConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(PlatformRunner.class)
@PlatformRunner.Configuration(SecuredPlatformRunnerITest.SecuredKasperPlatformConfiguration.class)
@PlatformRunner.Bundles(list = {SecuredPlatformRunnerITest.SecuredDomainBundle.class})
public class SecuredPlatformRunnerITest {

    final private static String SECURITY_TOKEN = "SET_BY_SECURITY_INTERCEPTOR";
    final private static String ERROR_MSG = "Can't decrypt security token";

    @Inject
    private CommandGateway commandGateway;

    @Inject
    private QueryGateway queryGateway;

    static private class IdentityProvider implements IdentityContextProvider {
        boolean shouldThrowException = false;
        @Override
        public void provideIdentity(Context context) {
            if (shouldThrowException) {
                throw new KasperSecurityException(ERROR_MSG);
            }
            context.setSecurityToken(SECURITY_TOKEN);
        }
        public void setShouldThrowException(boolean shouldThrowException) {
            this.shouldThrowException = shouldThrowException;
        }
    }

    static private IdentityProvider identityProvider = new IdentityProvider();

    static final class SecuredKasperPlatformConfiguration extends KasperPlatformConfiguration {
        SecuredKasperPlatformConfiguration() {

            super(new SecurityConfiguration() {
                @Override
                public IdentityContextProvider getIdentityContextProvider() {
                    return identityProvider;
                }
            });
        }
    }

    private static class SecuredDomain implements Domain {
    }

    private static class TestQuery implements Query {
    }

    @XKasperCommand
    private static class TestCommand implements Command {
    }

    private static class TestResult implements QueryResult {

        private String securityToken;

        TestResult(String securityToken) {
            this.securityToken = securityToken;
        }

        public String getSecurityToken() {
            return securityToken;
        }
    }

    @XKasperQueryHandler(domain = SecuredDomain.class)
    private static class TestQueryHandler extends QueryHandler<TestQuery, TestResult> {
        public QueryResponse<TestResult> retrieve(final QueryMessage<TestQuery> message) throws Exception {
            return QueryResponse.of(new TestResult(message.getContext().getSecurityToken()));
        }
    }

    @XKasperCommandHandler(domain = SecuredDomain.class)
    private static class TestCommandHandler extends CommandHandler<TestCommand> {
        public CommandResponse handle(final TestCommand command) {
            Context context = getContext();
            return CommandResponse.ok().withSecurityToken(context.getSecurityToken());
        }
    }

    static final class SecuredDomainBundle extends DefaultDomainBundle {
        SecuredDomainBundle() {
            super(new SecuredDomain());
        }

        @Override
        public List<CommandHandler> getCommandHandlers() {
            return Arrays.asList((CommandHandler) new TestCommandHandler());
        }

        @Override
        public List<QueryHandler> getQueryHandlers() {
            return Arrays.asList((QueryHandler) new TestQueryHandler());
        }

    }

    // ------------------------------------------------------------------------

    @Test
    public void securityInterceptorOnCommand_shouldSetSecurityToken() throws Exception {
        assertNotNull(commandGateway);

        // Given
        final Context context = new DefaultContext();

        // When
        final CommandResponse response = commandGateway.sendCommandAndWaitForAResponse(new TestCommand(), context);

        // Then
        assertEquals("Context's security token not set correctly", SECURITY_TOKEN, response.getSecurityToken().get());
    }

    @Test
    public void securityInterceptorOnQuery_shouldSetSecurityToken() throws Exception {
        assertNotNull(queryGateway);

        // Given
        final Context context = new DefaultContext();

        // When
        final QueryResponse<TestResult> response = queryGateway.retrieve(new TestQuery(), context);

        // Then
        assertEquals("Context's security token not set correctly",
                SECURITY_TOKEN, response.getResult().getSecurityToken());
    }

    @Test
    public void securityInterceptorOnQuery_shouldReturnQueryErrorWhenExceptionIsThrown() throws Exception {
        assertNotNull(queryGateway);

        // Given
        final Context context = new DefaultContext();
        identityProvider.setShouldThrowException(true);
        final QueryResponse<TestResult> response = queryGateway.retrieve(new TestQuery(), context);

        // When
        final KasperReason reason = response.getReason();

        // Then
        assertNotNull(reason);
        assertEquals("Interceptor didn't set INVALID_INPUT reason code",
                CoreReasonCode.INVALID_INPUT.name(), reason.getCode());
    }

    @Test
    public void securityInterceptorOnCommand_shouldReturnCommandErrorWhenExceptionIsThrown() throws Exception {
        assertNotNull(commandGateway);

        // Given
        final Context context = new DefaultContext();
        identityProvider.setShouldThrowException(true);
        final CommandResponse response = commandGateway.sendCommandAndWaitForAResponse(new TestCommand(), context);

        // When
        final KasperReason reason = response.getReason();

        // Then
        assertNotNull(reason);
        assertEquals("Interceptor didn't set INVALID_INPUT reason code",
                CoreReasonCode.INVALID_INPUT.name(), reason.getCode());
    }

}
