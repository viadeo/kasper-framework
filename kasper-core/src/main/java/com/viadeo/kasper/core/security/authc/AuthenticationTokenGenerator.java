package com.viadeo.kasper.core.security.authc;

import com.viadeo.kasper.api.context.Context;

import java.io.Serializable;

public interface AuthenticationTokenGenerator<TOKEN extends Serializable> {

    TOKEN generate(final Context context);
}
