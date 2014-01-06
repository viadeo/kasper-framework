package com.viadeo.kasper.core.interceptor;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.security.IdentityElementContextProvider;
import com.viadeo.kasper.security.SecurityConfiguration;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SecurityInterceptorUTest {
    @XKasperUnregistered
    static private final class DummyQuery implements Query { }

    @Test
    public void testIdentityElementProvider() throws Exception {
// Given
        final IdentityElementContextProvider provider = mock(IdentityElementContextProvider.class);
        final SecurityConfiguration securityConfiguration = new SecurityConfiguration() {
            @Override
            public List<IdentityElementContextProvider> getIdentityElementContextProviders() {
                return Collections.singletonList(provider);
            };
        };
        final BaseSecurityInterceptor securityInterceptor = new BaseSecurityInterceptor(securityConfiguration){};
        final Context context = mock(Context.class);

// When
        securityInterceptor.addSecurityIdentity(context);

// Then
        verify(provider).provideIdentityElement(refEq(context));
    }
}
