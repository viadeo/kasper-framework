package com.viadeo.kasper.security.callback.impl;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.annotation.XKasperRequirePermissions;
import com.viadeo.kasper.security.annotation.XKasperRequireRoles;
import com.viadeo.kasper.security.authz.AuthorizationSecurityManager;
import com.viadeo.kasper.security.authz.Subject;
import com.viadeo.kasper.security.callback.AuthorizationValidator;
import com.viadeo.kasper.security.exception.KasperUnauthorizedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultAuthorizationValidator implements AuthorizationValidator {

    private AuthorizationSecurityManager authorizationSecurityManager;

    public DefaultAuthorizationValidator(AuthorizationSecurityManager authorizationSecurityManager) {
        this.authorizationSecurityManager = authorizationSecurityManager;
    }

    @Override
    public void validate(final Context context, final Class<?> clazz) throws KasperUnauthorizedException {
        final Subject subject = this.authorizationSecurityManager.getSubject(context);
        this.authorizationSecurityManager.checkRoles(extractRoles(clazz), subject);
        this.authorizationSecurityManager.checkPermissions(extractPermissions(clazz), subject);
    }

    private List<String> extractRoles(final Class<?> clazz) {
        String[] neededRoles = clazz.getAnnotation(XKasperRequireRoles.class).value();
        List<String> roles = new ArrayList<String>();
        if (neededRoles != null && neededRoles.length > 0) {
            roles.addAll(Arrays.asList(neededRoles));
        }
        return roles;
    }

    private List<String> extractPermissions(final Class<?> clazz) {
        String[] neededPermissions = clazz.getAnnotation(XKasperRequirePermissions.class).value();
        List<String> permissions = new ArrayList<String>();
        if (neededPermissions != null && neededPermissions.length > 0) {
            permissions.addAll(Arrays.asList(neededPermissions));
        }
        return permissions;
    }


}
