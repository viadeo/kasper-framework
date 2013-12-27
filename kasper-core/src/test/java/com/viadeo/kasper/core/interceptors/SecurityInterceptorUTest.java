package com.viadeo.kasper.core.interceptors;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.IdentityElementContextProvider;
import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.cqrs.RequestActorsChain;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.cache.impl.QueryCacheActorTest;
import com.viadeo.kasper.security.SecurityConfiguration;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SecurityInterceptorUTest {
    static private final String USER_ID = "UserIdSetByInterceptor";
    @XKasperUnregistered
    static private class DummyQuery implements Query { }

    @Test
    public void testIdentityElementProvider() throws Exception {
// Given

        final IdentityElementContextProvider provider = new IdentityElementContextProvider() {
            @Override
            public void provideIdentityElement(Context context) {
                context.setUserId(USER_ID);
            }
        };
        SecurityConfiguration securityConfiguration = new SecurityConfiguration() {
            @Override
            public List<IdentityElementContextProvider> getIdentityElementContextProvider() {
                return Collections.singletonList(provider);
            };
        };
        final SecurityInterceptor securityInterceptor = new SecurityInterceptor(securityConfiguration);
        Context context = new DefaultContext();
// When
        try {
            securityInterceptor.process(new DummyQuery(), context, RequestActorsChain.tail());
        } catch (IllegalStateException ignore) {
            // We may need a tail that do nothing...
        }

// Then
        assertEquals("Context's user Id has not been set correctly", USER_ID, context.getUserId());
    }
}
