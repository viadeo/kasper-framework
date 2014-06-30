package com.viadeo.kasper.client.platform.domain;

import com.google.common.collect.Lists;
import com.viadeo.kasper.security.authz.Authorization;
import com.viadeo.kasper.security.authz.runtime.AuthorizationCommandConfiguration;
import com.viadeo.kasper.security.authz.runtime.AuthorizationQueryConfiguration;
import com.viadeo.kasper.security.authz.runtime.AuthorizationRepositoryConfiguration;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

public class AuthorizationBundle extends SpringDomainBundle {

    public AuthorizationBundle(AuthorizationStorage authorizationStorage) {
        super(new Authorization()
                , Lists.<Class>newArrayList(
                AuthorizationQueryConfiguration.class,
                AuthorizationCommandConfiguration.class,
                AuthorizationRepositoryConfiguration.class
        )
                , new BeanDescriptor(authorizationStorage)
        );
    }
}
