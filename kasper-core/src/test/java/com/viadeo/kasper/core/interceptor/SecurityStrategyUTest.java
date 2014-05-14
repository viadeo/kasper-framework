// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.security.strategy.impl.DefaultPublicSecurityStrategy;
import com.viadeo.kasper.security.strategy.impl.DefaultSecurityStrategy;
import com.viadeo.kasper.security.configuration.SecurityConfiguration;
import com.viadeo.kasper.security.strategy.SecurityStrategy;
import com.viadeo.kasper.security.authz.mgt.AuthorizationSecurityManager;
import com.viadeo.kasper.security.callback.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SecurityStrategyUTest {

    @Mock
    SecurityTokenValidator tokenValidator;

    @Mock
    IdentityContextProvider identityProvider;

    @Mock
    ApplicationIdValidator applicationIdValidator;

    @Mock
    IpAddressValidator ipAddressValidator;

    @Mock
    AuthorizationValidator authorizationValidator;

    @Mock
    AuthorizationSecurityManager authorizationSecurityManager;

    SecurityConfiguration securityConfiguration;


    // ------------------------------------------------------------------------

    @Before
    public void setup() {
        initMocks(this);
        securityConfiguration = new SecurityConfiguration.Builder()
                .withSecurityTokenValidator(tokenValidator)
                .withIdentityProvider(identityProvider)
                .withApplicationIdValidator(applicationIdValidator)
                .withIpAddressValidator(ipAddressValidator)
                .withAuthorizationValidator(authorizationValidator)
                .build();
    }

    // ------------------------------------------------------------------------

    @Test
    public void applySecurityBeforeRequest_onNonPublicRequest_shouldInvokeAuthenticationCallbacks()
            throws Exception {

        // Given
        final SecurityStrategy securityStrategy = new DefaultSecurityStrategy(securityConfiguration, QueryHandler.class);
        final Context context = mock(Context.class);

        // When
        securityStrategy.beforeRequest(context);

        // Then
        verify(tokenValidator).validate(refEq(context.getSecurityToken()));
        verify(identityProvider).provideIdentity(refEq(context));
        verify(applicationIdValidator).validate(context.getApplicationId());
        verify(ipAddressValidator).validate(context.getIpAddress());
        verify(authorizationValidator).validate(context, QueryHandler.class);
    }

    @Test
    public void applySecurityBeforeRequest_onPublicRequest_shouldNotInvokeAuthenticationCallbacks()
            throws Exception {

        // Given
        final SecurityStrategy securityStrategy = new DefaultPublicSecurityStrategy(securityConfiguration, QueryHandler.class);
        final Context context = mock(Context.class);

        // When
        securityStrategy.beforeRequest(context);

        // Then
        verifyZeroInteractions(tokenValidator);
        verify(identityProvider).provideIdentity(refEq(context));
        verify(applicationIdValidator).validate(context.getApplicationId());
        verify(ipAddressValidator).validate(context.getIpAddress());
        verify(authorizationValidator).validate(context, QueryHandler.class);
    }

}
