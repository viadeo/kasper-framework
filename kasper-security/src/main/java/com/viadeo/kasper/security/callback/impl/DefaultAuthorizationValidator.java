// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.security.callback.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.annotation.XKasperRequirePermissions;
import com.viadeo.kasper.security.annotation.XKasperRequireRoles;
import com.viadeo.kasper.security.authz.entities.actor.Actor;
import com.viadeo.kasper.security.authz.mgt.AuthorizationSecurityManager;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;
import com.viadeo.kasper.security.callback.AuthorizationValidator;
import com.viadeo.kasper.security.exception.KasperUnauthorizedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultAuthorizationValidator implements AuthorizationValidator {

    private AuthorizationSecurityManager authorizationSecurityManager;
    private AuthorizationStorage authorizationStorage;

    // ------------------------------------------------------------------------

    public DefaultAuthorizationValidator(final AuthorizationSecurityManager authorizationSecurityManager,
                                         final AuthorizationStorage authorizationStorage) {
        this.authorizationSecurityManager = checkNotNull(authorizationSecurityManager);
        this.authorizationStorage = checkNotNull(authorizationStorage);
    }

    // ------------------------------------------------------------------------

    @Override
    public void validate(final Context context, final Class<?> clazz) throws KasperUnauthorizedException {
        checkNotNull(context);
        checkNotNull(clazz);

        final Optional<Actor> actor = this.authorizationStorage.getActor(context);
        if(actor.isPresent()) {
            this.authorizationSecurityManager.checkRoles(extractRoles(clazz), actor.get());
            this.authorizationSecurityManager.checkPermissions(extractPermissions(clazz), actor.get());
        }
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
