package com.viadeo.kasper.core.security.authc;

import com.viadeo.kasper.api.context.Context;

import java.io.Serializable;

public interface Authenticator<TOKEN extends Serializable> {

    boolean isAuthenticated(final Context context);

    Serializable createAuthenticationToken(final Context context);

}
