// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.security.authc;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.viadeo.kasper.api.context.Context;

import java.io.Serializable;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class InMemoryAuthentication<TOKEN extends Serializable> implements Authenticator  {

    private final Set<TOKEN> tokens;

    public InMemoryAuthentication() {
        this.tokens = Sets.newHashSet();
    }

    public void addToken(final TOKEN token){
        this.tokens.add(token);
    }

    public void removeToken(final TOKEN token){
        this.tokens.remove(token);
    }

    @Override
    public boolean isAuthenticated(final Context context) {
        final Optional<TOKEN> token = checkNotNull(context).getAuthenticationToken();
        return (token.isPresent() && tokens.contains(token.get()));
    }
}
