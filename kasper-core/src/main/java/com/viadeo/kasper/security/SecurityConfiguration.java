package com.viadeo.kasper.security;

import java.util.List;

/**
 * This interface allows defining security configuration of the platform.
 */

public interface SecurityConfiguration {
    void addIdentityElementContextProvider(IdentityElementContextProvider provider);

    List<IdentityElementContextProvider> getIdentityElementContextProvider();
}
