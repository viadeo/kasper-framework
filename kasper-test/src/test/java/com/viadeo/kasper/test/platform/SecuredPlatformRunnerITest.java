package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.client.platform.configuration.KasperPlatformConfiguration;
import com.viadeo.kasper.client.platform.domain.DefaultDomainBundle;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.IdentityElementContextProvider;
import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.cqrs.command.*;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.cqrs.query.*;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.security.SecurityConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(PlatformRunner.class)
@PlatformRunner.Configuration(SecuredPlatformRunnerITest.SecuredKasperPlatformConfiguration.class)
@PlatformRunner.Bundles(list = {SecuredPlatformRunnerITest.SecuredDomainBundle.class})
public class SecuredPlatformRunnerITest {
    final private static String SECURITY_TOKEN = "SET_BY_SECURITY_LAYER";
    @Inject
    public CommandGateway commandGateway;

    @Inject
    public QueryGateway queryGateway;

    static final class SecuredKasperPlatformConfiguration extends KasperPlatformConfiguration {
        SecuredKasperPlatformConfiguration() {
            super(new SecurityConfiguration() {
                @Override
                public List<IdentityElementContextProvider> getIdentityElementContextProvider() {
                    IdentityElementContextProvider provider = new IdentityElementContextProvider() {
                        @Override
                        public void provideIdentityElement(Context context) {
                            context.setSecurityToken(SECURITY_TOKEN);
                        }
                    };
                    return Collections.singletonList(provider);
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


    public static class TestResult implements QueryResult {
        private String securityToken;

        TestResult(String securityToken) {
            this.securityToken = securityToken;
        }
        public String getSecurityToken() { return securityToken;}
    }

    @XKasperQueryHandler(domain = SecuredDomain.class)
    public static class TestGetSomeDataQueryHandler extends QueryHandler<TestQuery, TestResult> {
        public QueryResponse<TestResult> retrieve(final QueryMessage<TestQuery> message) throws Exception {
            return QueryResponse.of(new TestResult(message.getContext().getUserId()));
        }
    }

    @XKasperCommandHandler(domain = SecuredDomain.class)
    public static class TestCommandHandler extends CommandHandler<TestCommand> {
        public CommandResponse handle(final TestCommand command) {
            Context context = getContext();
//            System.out.println("+++++++++++++++++ " + context.getUserId());
            return CommandResponse.ok();
        }
    }

    public static class SecuredDomainBundle extends DefaultDomainBundle {
        SecuredDomainBundle() {
            super(new SecuredDomain());
        }
        @Override
        public List<CommandHandler> getCommandHandlers() {
            return Arrays.asList((CommandHandler)new TestCommandHandler());
        }

        @Override
        public List<QueryHandler> getQueryHandlers() {
            return Arrays.asList((QueryHandler)new TestGetSomeDataQueryHandler());
        }

    }

    @Test
    public void securityInterceptorOnCommand_shouldSetSecurityToken() throws Exception {
        assertNotNull(commandGateway);
        Context context = new DefaultContext();
        CommandResponse response = commandGateway.sendCommandAndWaitForAResponse(new TestCommand(), context);
        assertEquals("Context's security token not set correctly", response.getSecurityToken(), SECURITY_TOKEN);
    }

    @Test
    public void securityInterceptorOnQuery_shouldSetSecurityToken() throws Exception {
        assertNotNull(queryGateway);
        Context context = new DefaultContext();
        QueryResponse<TestResult> response = queryGateway.retrieve(new TestQuery(), context);
        assertEquals("Context's security token not set correctly", response.getResult().getSecurityToken(), SECURITY_TOKEN);
    }

}
