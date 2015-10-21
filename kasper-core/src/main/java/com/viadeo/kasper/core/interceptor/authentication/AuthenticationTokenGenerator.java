// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor.authentication;

import com.viadeo.kasper.api.id.ID;

import java.io.Serializable;
import java.util.Map;

public interface AuthenticationTokenGenerator<TOKEN extends Serializable> {

    TOKEN generate(final ID subjectID, final Map<String, Object> properties);

    void revoke(final TOKEN token);
}
