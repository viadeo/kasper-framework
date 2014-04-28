package com.viadeo.kasper.security.callback;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.exception.KasperUnauthorizedException;

import java.util.List;

public interface AuthorizationValidator {

    //permission/role resolver

    void validate(Context context, Class<?> clazz)
            throws KasperUnauthorizedException;
}
