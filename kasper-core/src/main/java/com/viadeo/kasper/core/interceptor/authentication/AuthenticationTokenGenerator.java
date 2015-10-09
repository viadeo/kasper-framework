package com.viadeo.kasper.core.interceptor.authentication;

import com.viadeo.kasper.api.id.ID;

import java.io.Serializable;
import java.util.Map;

public interface AuthenticationTokenGenerator<TOKEN extends Serializable> {

    TOKEN generate(final ID subjectID, final Map<String, Object> properties);
}
