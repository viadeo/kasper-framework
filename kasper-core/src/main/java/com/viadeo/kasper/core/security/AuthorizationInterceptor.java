// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.security;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.api.annotation.XKasperAuthz.TargetId;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.InterceptorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.component.annotation.XKasperAuthz.RequiresPermissions;
import static com.viadeo.kasper.core.component.annotation.XKasperAuthz.RequiresRoles;

public class AuthorizationInterceptor<I,O> implements Interceptor<I,O> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationInterceptor.class);

    private final TypeToken type;
    private final Class<? extends AuthorizationManager> defaultAuthorizationManagerClass;
    private final Map<Class<? extends AuthorizationManager>, AuthorizationManager> authorizationManagers = Maps.newHashMap();

    public AuthorizationInterceptor(
            final TypeToken type,
            final Class<? extends AuthorizationManager> defaultAuthorizationManagerClass,
            final AuthorizationManager authorizationManager
    ) {
        checkNotNull(authorizationManager);
        this.type = checkNotNull(type);
        this.defaultAuthorizationManagerClass = checkNotNull(defaultAuthorizationManagerClass);
        this.authorizationManagers.put(authorizationManager.getClass(), authorizationManager);
    }

    public AuthorizationInterceptor(
            final TypeToken type,
            final Class<? extends AuthorizationManager> defaultAuthorizationManagerClass,
            final List<AuthorizationManager> authorizationManagers
    ) {
        this.type = checkNotNull(type);
        this.defaultAuthorizationManagerClass = checkNotNull(defaultAuthorizationManagerClass);
        this.authorizationManagers.putAll(Maps.<Class<? extends AuthorizationManager>, AuthorizationManager>uniqueIndex(
                authorizationManagers,
                new Function<AuthorizationManager, Class<? extends AuthorizationManager>>() {
                    @Nullable
                    @Override
                    public Class<? extends AuthorizationManager> apply(@Nullable final AuthorizationManager authorizationManager) {
                        return authorizationManager.getClass();
                    }
                }
        ));
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
        } else if (! this.authorizationManagers.containsKey(managerClass)) {
            LOGGER.error("Unable to instantiate an AuthorizationManager (not declared) : ", managerClass);
            managerClass = defaultAuthorizationManagerClass;
        }

        return this.authorizationManagers.get(managerClass);
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
        final List<AuthorizationManager> authorizationManagers;

        public Factory(
                final Class<? extends AuthorizationManager> defaultAuthorizationManagerClass,
                final List<AuthorizationManager> authorizationManagers
        ) {
            this.defaultAuthoriztionManagerClass = checkNotNull(defaultAuthorizationManagerClass);
            this.authorizationManagers = checkNotNull(authorizationManagers);
        }

        @Override
        public Optional<InterceptorChain<I,O>> create(TypeToken type) {
            checkNotNull(type);
            return Optional.of(
                    InterceptorChain.makeChain(
                            new AuthorizationInterceptor<I,O>(type, defaultAuthoriztionManagerClass, authorizationManagers)
                    )
            );
        }
    }
}
