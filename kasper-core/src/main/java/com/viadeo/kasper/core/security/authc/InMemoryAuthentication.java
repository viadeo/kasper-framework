// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.security.authc;

import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.id.ID;

import java.io.Serializable;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class InMemoryAuthentication<TOKEN extends Serializable> implements Authenticator  {

    private final List<TOKEN> tokens;
    private final AuthenticationTokenGenerator<TOKEN> authenticationTokenGenerator;

    public InMemoryAuthentication(final AuthenticationTokenGenerator<TOKEN> authenticationTokenGenerator) {
        this.tokens = Lists.newArrayList();
        this.authenticationTokenGenerator = checkNotNull(authenticationTokenGenerator);
    }

    public void addToken(final TOKEN token){
        this.tokens.add(token);
    }

    public void removeToken(final TOKEN token){
        this.tokens.remove(token);
    }

    @Override
    public boolean isAuthenticated(final Context context) {
        checkNotNull(context);
        if(context.getUserID().isPresent()){
            return true;
        }
        if(!context.getAuthenticationToken().isPresent()){
            return false;
        }

        final Optional<TOKEN> token = context.getAuthenticationToken();
        return (token.isPresent() && tokens.contains(token.get()));
    }

    @Override
    public TOKEN createAuthenticationToken(final Context context) {
        checkNotNull(context);

        final TOKEN token = authenticationTokenGenerator.generate(context);
        addToken(token);

        return token;
    }
}
