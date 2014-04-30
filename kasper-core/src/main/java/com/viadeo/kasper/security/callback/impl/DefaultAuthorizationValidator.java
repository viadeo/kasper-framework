package com.viadeo.kasper.security.callback.impl;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.annotation.XKasperRequirePermissions;
import com.viadeo.kasper.security.annotation.XKasperRequireRoles;
import com.viadeo.kasper.security.authz.mgt.AuthorizationSecurityManager;
import com.viadeo.kasper.security.authz.subject.Subject;
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

    protected List<String> extractRoles(final Class<?> clazz) {
        List<String> roles = new ArrayList<String>();
        if (clazz.getAnnotation(XKasperRequireRoles.class) != null) {
            String[] neededRoles = clazz.getAnnotation(XKasperRequireRoles.class).value();
            if (neededRoles != null && neededRoles.length > 0) {
                roles.addAll(Arrays.asList(neededRoles));
            }
        }
        return roles;
    }

    protected List<String> extractPermissions(final Class<?> clazz) {
        List<String> permissions = new ArrayList<String>();
        if (clazz.getAnnotation(XKasperRequirePermissions.class) != null) {
            String[] neededPermissions = clazz.getAnnotation(XKasperRequirePermissions.class).value();
            if (neededPermissions != null && neededPermissions.length > 0) {
                permissions.addAll(Arrays.asList(neededPermissions));
            }
        }
        return permissions;
    }


}
