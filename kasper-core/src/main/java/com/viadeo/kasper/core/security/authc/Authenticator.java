package com.viadeo.kasper.core.security.authc;

import com.viadeo.kasper.api.context.Context;

public interface Authenticator {

    boolean isAuthenticated(final Context context);

}
