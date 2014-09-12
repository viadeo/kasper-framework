// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.security.callback.impl;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.InterceptorFactory;
import com.viadeo.kasper.security.annotation.XKasperRequirePermissions;
import com.viadeo.kasper.security.annotation.XKasperRequireRoles;
import com.viadeo.kasper.security.authz.entities.actor.Actor;
import com.viadeo.kasper.security.authz.mgt.AuthorizationSecurityManager;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultAuthorizationInterceptor<C extends Object> implements Interceptor<C, Object> {

    private AuthorizationSecurityManager authorizationSecurityManager;
    private AuthorizationStorage authorizationStorage;
    private TypeToken type;

    public DefaultAuthorizationInterceptor(final TypeToken type, final AuthorizationSecurityManager authorizationSecurityManager, final AuthorizationStorage authorizationStorage) {
        this.type = type;
        this.authorizationSecurityManager = authorizationSecurityManager;
        this.authorizationStorage = authorizationStorage;
    }


    public static class Factory implements InterceptorFactory {

        private AuthorizationSecurityManager authorizationSecurityManager;
        private AuthorizationStorage authorizationStorage;

        public Factory(final AuthorizationSecurityManager authorizationSecurityManager, final AuthorizationStorage authorizationStorage) {
            this.authorizationSecurityManager = authorizationSecurityManager;
            this.authorizationStorage = authorizationStorage;
        }

        @Override
        public Optional<InterceptorChain> create(TypeToken type) {
            checkNotNull(type);
            return Optional.of(InterceptorChain.makeChain(new DefaultAuthorizationInterceptor(type, authorizationSecurityManager, authorizationStorage)));
        }

    }

    @Override
    public Object process(C c, Context context, InterceptorChain<C, Object> chain) throws Exception {
        checkNotNull(context);
        final Optional<? extends Actor> actor = this.authorizationStorage.getActor(context);
        if(actor.isPresent()) {
            this.authorizationSecurityManager.checkRoles(extractRoles(type.getRawType()), actor.get());
            this.authorizationSecurityManager.checkPermissions(extractPermissions(type.getRawType()), actor.get());
        }

        return chain.next(c, context);
    }

    protected List<String> extractRoles(final Class<?> clazz) {
        final List<String> roles = new ArrayList<>();

        if (null != clazz.getAnnotation(XKasperRequireRoles.class)) {
            final String[] neededRoles = clazz.getAnnotation(XKasperRequireRoles.class).value();
            if ((null != neededRoles) && (neededRoles.length > 0)) {
                roles.addAll(Arrays.asList(neededRoles));
            }
        }
        return roles;
    }

    protected List<String> extractPermissions(final Class<?> clazz) {
        final List<String> permissions = new ArrayList<>();
        if (null != clazz.getAnnotation(XKasperRequirePermissions.class)) {
            final String[] neededPermissions = clazz.getAnnotation(XKasperRequirePermissions.class).value();
            if ((null != neededPermissions) && (neededPermissions.length > 0)) {
                permissions.addAll(Arrays.asList(neededPermissions));
            }
        }
        return permissions;
    }

}
