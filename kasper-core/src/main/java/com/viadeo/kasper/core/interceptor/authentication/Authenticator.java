package com.viadeo.kasper.core.interceptor.authentication;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.id.ID;

public interface Authenticator {

    boolean isAuthenticated(final Context context);

    Optional<ID> getSubject(final Context context);

}
