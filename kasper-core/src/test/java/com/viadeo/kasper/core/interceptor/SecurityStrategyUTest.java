// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.DefaultPublicSecurityStrategy;
import com.viadeo.kasper.security.DefaultSecurityStrategy;
import com.viadeo.kasper.security.SecurityConfiguration;
import com.viadeo.kasper.security.SecurityStrategy;
import com.viadeo.kasper.security.callback.IdentityContextProvider;
import com.viadeo.kasper.security.callback.SecurityTokenValidator;
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

    SecurityConfiguration securityConfiguration;

    @Before
    public void setup() {
        initMocks(this);
        securityConfiguration = new SecurityConfiguration.Builder().
                withSecurityTokenValidator(tokenValidator).
                withIdentityProvider(identityProvider).
                build();
    }

    @Test
    public void applySecurityBeforeRequest_onNonPublicRequest_shouldInvokeAuthenticationCallbacks() throws Exception {
        // Given
        final SecurityStrategy securityStrategy = new DefaultSecurityStrategy(securityConfiguration);
        final Context context = mock(Context.class);

        // When
        securityStrategy.beforeRequest(context);

        // Then
        verify(tokenValidator).validate(refEq(context.getSecurityToken()));
        verify(identityProvider).provideIdentity(refEq(context));
    }

    @Test
    public void applySecurityBeforeRequest_onPublicRequest_shouldNotInvokeAuthenticationCallbacks() throws Exception {
        // Given
        final SecurityStrategy securityStrategy = new DefaultPublicSecurityStrategy(securityConfiguration);
        final Context context = mock(Context.class);

        // When
        securityStrategy.beforeRequest(context);

        // Then
        verifyZeroInteractions(tokenValidator);
        verifyZeroInteractions(identityProvider);
    }
}
