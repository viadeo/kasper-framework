package com.viadeo.kasper.security;

import com.viadeo.kasper.context.IdentityElementContextProvider;

import java.util.List;

public interface SecurityConfiguration {
    List<IdentityElementContextProvider> getIdentityElementContextProvider();
}
