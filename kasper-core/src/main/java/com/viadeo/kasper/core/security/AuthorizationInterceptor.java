// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.security;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.api.annotation.XKasperAuthz.TargetId;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.InterceptorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.component.annotation.XKasperAuthz.RequiresPermissions;
import static com.viadeo.kasper.core.component.annotation.XKasperAuthz.RequiresRoles;

public class AuthorizationInterceptor<I,O> implements Interceptor<I,O> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationInterceptor.class);

    private final TypeToken type;
    private final Class<? extends AuthorizationManager> defaultAuthorizationManagerClass;
    private final ApplicationContext applicationContext;

    public AuthorizationInterceptor(
            final TypeToken type,
            final Class<? extends AuthorizationManager> defaultAuthorizationManagerClass,
            final ApplicationContext applicationContext
    ) {
        this.type = checkNotNull(type);
        this.defaultAuthorizationManagerClass = checkNotNull(defaultAuthorizationManagerClass);
        this.applicationContext = checkNotNull(applicationContext);
    }

    @Override
    public O process(I c, Context context, InterceptorChain<I, O> chain) throws Exception {
        checkNotNull(context);
        final Class<?> clazz = type.getRawType();

        if (null != context.getUserID() && context.getUserID().isPresent()) {
            if (clazz.isAnnotationPresent(RequiresRoles.class)) {
                final RequiresRoles requiresRoles = clazz.getAnnotation(RequiresRoles.class);
                final AuthorizationManager authorizationManager = getAuthorizationManager(requiresRoles.manager());

                if (null != authorizationManager && !authorizationManager.hasRole(requiresRoles.value(), requiresRoles.combinesWith(), context.getUserID().get(), getTargetedId(c))) {
                    throw new KasperInvalidAuthorizationException(
                            String.format("[AuthorizationInterceptor] Member %s has no role (%s) for target %s", context.getUserID().get(), Arrays.toString(requiresRoles.value()), getTargetedId(c)),
                            CoreReasonCode.REQUIRE_AUTHORIZATION
                    );
                }
            }
            if (clazz.isAnnotationPresent(RequiresPermissions.class)) {
                final RequiresPermissions requiresPermissions = clazz.getAnnotation(RequiresPermissions.class);
                final AuthorizationManager authorizationManager = getAuthorizationManager(requiresPermissions.manager());

                if (null != authorizationManager && !authorizationManager.isPermitted(requiresPermissions.value(), requiresPermissions.combinesWith(), context.getUserID().get(), getTargetedId(c))) {
                    throw new KasperInvalidAuthorizationException(
                            String.format("[AuthorizationInterceptor] Member %s has no permission (%s) for target %s", context.getUserID().get(), Arrays.toString(requiresPermissions.value()), getTargetedId(c)),
                            CoreReasonCode.REQUIRE_AUTHORIZATION
                    );
                }
            }
        }
        return chain.next(c, context);
    }

    private AuthorizationManager getAuthorizationManager(Class<? extends AuthorizationManager> managerClass) {
        if (managerClass.equals(AuthorizationManager.class)) {
            managerClass = defaultAuthorizationManagerClass;
        }

        AuthorizationManager authorizationManager = null;
        try {
            authorizationManager = applicationContext.getBean(managerClass);
        } catch (Exception e) {
            LOGGER.error("Unable to instantiate an AuthorizationManager : ", e);
        }
        return authorizationManager;
    }

    protected Optional<Object> getTargetedId(I c) throws KasperInvalidAuthorizationException, IllegalAccessException {
        Optional<Object> targetId = Optional.absent();
        for (Field field : c.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(TargetId.class)) {
                if (targetId.isPresent()) {
                    throw new KasperInvalidAuthorizationException("Only one targetId is Authorized", CoreReasonCode.REQUIRE_AUTHORIZATION);
                } else {
                    field.setAccessible(true);
                    targetId = Optional.of(field.get(c));
                }
            }
        }
        return targetId;
    }

    // ========================================================================

    public static class Factory<I,O> implements InterceptorFactory<I,O> {

        private final Class<? extends AuthorizationManager> defaultAuthoriztionManagerClass;
        private final ApplicationContext applicationContext;

        public Factory(
                final Class<? extends AuthorizationManager> defaultAuthorizationManagerClass,
                final ApplicationContext applicationContext
        ) {
            this.defaultAuthoriztionManagerClass = checkNotNull(defaultAuthorizationManagerClass);
            this.applicationContext = checkNotNull(applicationContext);
        }

        @Override
        public Optional<InterceptorChain<I,O>> create(TypeToken type) {
            checkNotNull(type);
            return Optional.of(
                    InterceptorChain.makeChain(
                            new AuthorizationInterceptor<I,O>(type, defaultAuthoriztionManagerClass, applicationContext)
                    )
            );
        }
    }
}
