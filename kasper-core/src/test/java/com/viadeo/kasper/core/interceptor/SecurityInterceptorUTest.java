package com.viadeo.kasper.core.interceptor;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.security.IdentityElementContextProvider;
import com.viadeo.kasper.security.SecurityConfiguration;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SecurityInterceptorUTest {
    static private final String USER_ID = "UserIdSetByInterceptor";
    @XKasperUnregistered
    static private final class DummyQuery implements Query { }

    @Test
    public void testIdentityElementProvider() throws Exception {
// Given

        final IdentityElementContextProvider provider = new IdentityElementContextProvider() {
            @Override
            public void provideIdentityElement(Context context) {
                context.setUserId(USER_ID);
            }
        };
        final SecurityConfiguration securityConfiguration = new SecurityConfiguration() {
            @Override
            public void addIdentityElementContextProvider(IdentityElementContextProvider provider) {
                throw new UnsupportedOperationException();
            }

            @Override
            public List<IdentityElementContextProvider> getIdentityElementContextProvider() {
                return Collections.singletonList(provider);
            };
        };
        final SecurityInterceptor securityInterceptor = new SecurityInterceptor(securityConfiguration);
        final Context context = new DefaultContext();
// When
        try {
            securityInterceptor.process(null, context, InterceptorChain.tail());
        } catch (IllegalStateException ignore) {
            // next to tail will throw Exception, but we don't care here.
        }

// Then
        assertEquals("Context's user Id has not been set correctly", USER_ID, context.getUserId());
    }
}
