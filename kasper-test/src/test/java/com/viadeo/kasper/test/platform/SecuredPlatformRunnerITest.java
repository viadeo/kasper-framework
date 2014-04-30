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
import com.viadeo.kasper.security.SecurityConfiguration;
import com.viadeo.kasper.security.annotation.XKasperPublic;
import com.viadeo.kasper.security.callback.ApplicationIdValidator;
import com.viadeo.kasper.security.callback.IdentityContextProvider;
import com.viadeo.kasper.security.callback.IpAddressValidator;
import com.viadeo.kasper.security.callback.SecurityTokenValidator;
import com.viadeo.kasper.security.exception.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(PlatformRunner.class)
@PlatformRunner.Configuration(SecuredPlatformRunnerITest.SecuredKasperPlatformConfiguration.class)
@PlatformRunner.Bundles({SecuredPlatformRunnerITest.SecuredDomainBundle.class})
public class SecuredPlatformRunnerITest {

    final private static String SECURITY_TOKEN = "DUMMY_SECRET";
    final private static String APPLICATION_ID = "APPLICATION_ID";
    final private static String IP_ADDRESS = "IP_ADDRESS";
    final private static String USER_ID = "SET_BY_SECURITY_INTERCEPTOR";
    final private static String INVALID_TOKEN_ERROR_MSG = "Security Token is invalid";
    final private static String INVALID_APPLICATION_ID_ERROR_MSG = "ApplicationId is invalid";
    final private static String INVALID_IP_ADDRESS_ERROR_MSG = "ip address is invalid";

    // ------------------------------------------------------------------------

    @Inject
    private CommandGateway commandGateway;

    @Inject
    private QueryGateway queryGateway;

    // ------------------------------------------------------------------------

    static private class RejectEmptyTokenValidator implements SecurityTokenValidator {
        @Override
        public void validate(String securityToken)
                throws KasperMissingSecurityTokenException,
                KasperInvalidSecurityTokenException {
            if ("".equals(securityToken)) {
                throw new KasperInvalidSecurityTokenException(
                        INVALID_TOKEN_ERROR_MSG,
                        CoreReasonCode.REQUIRE_AUTHENTICATION
                );
            }
        }
    }

    static private class RejectEmptyApplicationIdValidator implements ApplicationIdValidator {
        @Override
        public void validate(String applicationId)
                throws KasperMissingApplicationIdException,
                KasperInvalidApplicationIdException {
            if (null == applicationId || "".equals(applicationId)) {
                throw new KasperInvalidApplicationIdException(
                        INVALID_APPLICATION_ID_ERROR_MSG,
                        CoreReasonCode.INVALID_INPUT
                );
            }
        }
    }

    static private class RejectEmptyIpAddressValidator implements IpAddressValidator {
        @Override
        public void validate(String ipAddress)
                throws KasperMissingIpAddressException,
                KasperInvalidIpAddressException {
            if (null == ipAddress || "".equals(ipAddress)) {
                throw new KasperInvalidIpAddressException(
                        INVALID_IP_ADDRESS_ERROR_MSG,
                        CoreReasonCode.INVALID_INPUT
                );
            }
        }
    }


    static private class IdentityProvider implements IdentityContextProvider {
        @Override
        public void provideIdentity(final Context context) {
            if ( ! context.getSecurityToken().isEmpty()) {
                context.setUserId(USER_ID);
            }
        }
    }

    static final class SecuredKasperPlatformConfiguration extends KasperPlatformConfiguration {
        SecuredKasperPlatformConfiguration() {
            super(new SecurityConfiguration.Builder()
                    .withIdentityProvider(new IdentityProvider())
                    .withSecurityTokenValidator(new RejectEmptyTokenValidator())
                    .withApplicationIdValidator(new RejectEmptyApplicationIdValidator())
                    .withIpAddressValidator(new RejectEmptyIpAddressValidator())
                    .build());
        }
    }

    private static class SecuredDomain implements Domain { }

    private static class TestQuery implements Query { }

    @XKasperPublic
    private static class TestPublicQuery implements Query { }

    @XKasperCommand
    private static class TestCommand implements Command { }

    @XKasperPublic
    @XKasperCommand
    private static class TestPublicCommand implements Command { }

    private static class TestResult implements QueryResult { }






    @XKasperQueryHandler(domain = SecuredDomain.class)
    private static class TestQueryHandler extends QueryHandler<TestQuery, TestResult> {
        public QueryResponse<TestResult> retrieve(final QueryMessage<TestQuery> message) throws Exception {
            return QueryResponse.of(new TestResult());
        }
    }

    @XKasperQueryHandler(domain = SecuredDomain.class)
    private static class TestPublicQueryHandler extends QueryHandler<TestPublicQuery, TestResult> {
        public QueryResponse<TestResult> retrieve(final QueryMessage<TestPublicQuery> message) throws Exception {
            return QueryResponse.of(new TestResult());
        }
    }

    @XKasperCommandHandler(domain = SecuredDomain.class)
    private static class TestCommandHandler extends CommandHandler<TestCommand> {
        public CommandResponse handle(final TestCommand command) {
            final Context context = getContext();
            return CommandResponse.ok();
        }
    }

    @XKasperCommandHandler(domain = SecuredDomain.class)
    private static class TestPublicCommandHandler extends CommandHandler<TestPublicCommand> {
        public CommandResponse handle(final TestPublicCommand command) {
            return CommandResponse.ok();
        }
    }

    static final class SecuredDomainBundle extends DefaultDomainBundle {
        SecuredDomainBundle() {
            super(new SecuredDomain());
        }

        @Override
        public List<CommandHandler> getCommandHandlers() {
            return Arrays.asList((CommandHandler) new TestCommandHandler(),
                    (CommandHandler) new TestPublicCommandHandler());
        }

        @Override
        public List<QueryHandler> getQueryHandlers() {
            return Arrays.asList((QueryHandler) new TestQueryHandler(),
                    (QueryHandler) new TestPublicQueryHandler());
        }

    }

    // ------------------------------------------------------------------------

    @Test
    public void issuingCommand_withNoSecurityToken_shouldResponseKasperReasonWithRequireAuthentication() throws Exception {
        // Given
        final Context unauthenticatedContext = getContext(no_flag);

        // When
        final CommandResponse response = sendCommand(unauthenticatedContext);

        // Then
        assertAuthenticationRequired(response.getReason());
    }

    @Test
    public void issuingCommand_withNoApplicationId_shouldResponseKasperReasonWithRequireApplicationId() throws Exception {
        // Given
        final Context authenticatedContext = getContext(flag_withSecurityToken);

        // When
        final CommandResponse response = sendCommand(authenticatedContext);

        // Then
        assertApplicationIdRequired(response.getReason());
    }

    @Test
    public void issuingCommand_withNoIpAddress_shouldResponseKasperReasonWithRequireIpAddress() throws Exception {
        // Given
        final Context authenticatedContext = getContext(flag_withSecurityToken + flag_withApplicationId);

        // When
        final CommandResponse response = sendCommand(authenticatedContext);

        // Then
        assertIpAddressRequired(response.getReason());
    }

    @Test
    public void issuingPublicCommand_withNoSecurityToken_shouldBeOk() throws Exception {
        // Given
        final Context unauthenticatedContext = getContext(flag_withApplicationId + flag_withIpAddress);
        String previousUserId = unauthenticatedContext.getUserId();

        // When
        final CommandResponse response = sendPublicCommand(unauthenticatedContext);

        // Then
        assertPublicRequestGoTroughWithoutAlteringSecurityIdentity(
                response.getReason(),
                previousUserId,
                unauthenticatedContext.getUserId()
        );
    }

    @Test
    public void issuingPublicCommand_withNoApplicationId_shouldResponseKasperReasonWithRequireApplicationId() throws Exception {
        // Given
        final Context authenticatedContext = getContext(flag_withSecurityToken);

        // When
        final CommandResponse response = sendPublicCommand(authenticatedContext);

        // Then
        assertApplicationIdRequired(
                response.getReason()
        );
    }

    @Test
    public void issuingPublicCommand_withNoIpAddress_shouldResponseKasperReasonWithRequireIpAddress() throws Exception {
        // Given
        final Context authenticatedContext = getContext(flag_withSecurityToken + flag_withApplicationId);

        // When
        final CommandResponse response = sendPublicCommand(authenticatedContext);

        // Then
        assertIpAddressRequired(
                response.getReason()
        );
    }

    @Test
    public void issuingPublicCommand_withSecurityToken_shouldSetSecurityIdentityInContext() throws Exception {
        // Given
        final Context authenticatedContext = getContext(flag_withSecurityToken + flag_withApplicationId + flag_withIpAddress);

        // When
        final CommandResponse response = sendPublicCommand(authenticatedContext);

        // Then
        assertSecurityIdentityProvided(response.isOK(), authenticatedContext.getUserId());
    }

    @Test
    public void issuingQuery_withNoSecurityToken_shouldResponseKasperReasonWithRequireAuthentication() throws Exception {
        // Given
        final Context unauthenticatedContext = getContext(no_flag);

        // When
        final QueryResponse<TestResult> response = queryGateway.retrieve(
                new TestQuery(),
                unauthenticatedContext
        );

        // Then
        assertAuthenticationRequired(response.getReason());
    }

    @Test
    public void issuingPublicQuery_withNoSecurityToken_shouldBeOk() throws Exception {
        // Given
        final Context unAuthenticatedContext = getContext(flag_withApplicationId + flag_withIpAddress);
        final String previousUserId = unAuthenticatedContext.getUserId();

        // When
        final QueryResponse<TestResult> response = sendPublicQuery(unAuthenticatedContext);

        // Then
        assertPublicRequestGoTroughWithoutAlteringSecurityIdentity(
                response.getReason(),
                previousUserId,
                unAuthenticatedContext.getUserId()
        );
    }

    @Test
    public void issuingPublicQuery_withNoApplicationId_shouldResponseKasperReasonWithRequireApplicationId() throws Exception {
        // Given
        final Context authenticatedContext = getContext(flag_withSecurityToken);

        // When
        final QueryResponse<TestResult> response = sendPublicQuery(authenticatedContext);

        // Then
        assertApplicationIdRequired(response.getReason());
    }

    @Test
    public void issuingPublicQuery_withNoIpAddress_shouldResponseKasperReasonWithRequireIpAddress() throws Exception {
        // Given
        final Context authenticatedContext = getContext(flag_withSecurityToken + flag_withApplicationId);

        // When
        final QueryResponse<TestResult> response = sendPublicQuery(authenticatedContext);

        // Then
        assertIpAddressRequired(response.getReason());
    }

    @Test
    public void issuingPublicQuery_withSecurityToken_shouldSetSecurityIdentityInContext() throws Exception {
        // Given
        final Context authenticatedContext = getContext(flag_withSecurityToken + flag_withApplicationId + flag_withIpAddress);
        String previousUserId = authenticatedContext.getUserId();

        // When
        final QueryResponse<TestResult> response = sendPublicQuery(authenticatedContext);

        // Then
        assertSecurityIdentityProvided(response.isOK(), authenticatedContext.getUserId());
    }

    @Test
    public void issuingCommand_withSecurityToken_shouldSetSecurityIdentityInContext() throws Exception {
        // Given
        final Context authenticatedContext = getContext(flag_withSecurityToken + flag_withApplicationId + flag_withIpAddress);

        // When
        final CommandResponse response = sendCommand(authenticatedContext);

        // Then
        assertSecurityIdentityProvided(response.isOK(), authenticatedContext.getUserId());
    }

    @Test
    public void issuingQuery_withSecurityToken_shouldSetSecurityIdentityInContext() throws Exception {
        // Given
        final Context authenticatedContext = getContext(flag_withSecurityToken + flag_withApplicationId + flag_withIpAddress);

        // When
        final QueryResponse<TestResult> response = sendQuery(authenticatedContext);

        // Then
        assertSecurityIdentityProvided(response.isOK(), authenticatedContext.getUserId());
    }

    // ------------------------------------------------------------------------

    private CommandResponse sendCommand(final Context context) throws Exception {
        return commandGateway.sendCommandAndWaitForAResponse(new TestCommand(), context);
    }

    private CommandResponse sendPublicCommand(final Context context) throws Exception {
        return commandGateway.sendCommandAndWaitForAResponse(new TestPublicCommand(), context);
    }

    private QueryResponse sendQuery(final Context context) throws Exception {
        return queryGateway.retrieve(new TestQuery(), context);
    }

    private QueryResponse sendPublicQuery(final Context context) throws Exception {
        return queryGateway.retrieve(new TestPublicQuery(), context);
    }

    private static final int no_flag = 0;
    private static final int flag_withSecurityToken = 0x0001;
    private static final int flag_withApplicationId = 0x0002;
    private static final int flag_withIpAddress = 0x0004;

    private Context getContext(final int flags) {
        final Context context = new DefaultContext();
        if((flags & flag_withSecurityToken) > 0){
            context.setSecurityToken(SECURITY_TOKEN);
        }
        if((flags & flag_withApplicationId) > 0){
            context.setApplicationId(APPLICATION_ID);
        }
        if((flags & flag_withIpAddress) > 0){
            context.setIpAddress(IP_ADDRESS);
        }
        return context;
    }

    private void assertAuthenticationRequired(KasperReason kasperReason) {
        assertNotNull(kasperReason);
        assertEquals(
                "Security Interceptor didn't set correct Kasper Reason",
                new KasperReason(CoreReasonCode.REQUIRE_AUTHENTICATION, INVALID_TOKEN_ERROR_MSG),
                kasperReason
        );
    }

    private void assertApplicationIdRequired(KasperReason kasperReason) {
        assertNotNull(kasperReason);
        assertEquals(
                "Application Id Required",
                new KasperReason(CoreReasonCode.INVALID_INPUT, INVALID_APPLICATION_ID_ERROR_MSG),
                kasperReason
        );
    }

    private void assertIpAddressRequired(KasperReason kasperReason) {
        assertNotNull(kasperReason);
        assertEquals(
                "IpAddress Required",
                new KasperReason(CoreReasonCode.INVALID_INPUT, INVALID_IP_ADDRESS_ERROR_MSG),
                kasperReason
        );
    }

    private void assertSecurityIdentityProvided(boolean responseIsOK, String actualUserId) {
        assertTrue("Security Interceptor didn't let go a valid authenticated request", responseIsOK);
        assertEquals("Security Interceptor didn't set correct UserId", USER_ID, actualUserId);
    }

    private void assertPublicRequestGoTroughWithoutAlteringSecurityIdentity(final KasperReason kasperReason,
                                                                            final String previousUserId,
                                                                            final String actualUserId) {
        assertNull(
                "Security Interceptor didn't let go a request on a public command",
                kasperReason
        );

        assertEquals(
                "Security Interceptor shouldn't change user Id on public request",
                previousUserId,
                actualUserId
        );
    }

}
