// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor.authentication;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.id.ID;

import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public class InMemoryAuthentication implements Authenticator, AuthenticationTokenGenerator<java.lang.String>  {

    private final Map<String, ID> tokens;

    public InMemoryAuthentication() {
        this.tokens = Maps.newHashMap();
    }

    public void addToken(final String token, final ID subjectID) {
        this.tokens.put(token, subjectID);
    }

    public void removeToken(final String token) {
        this.tokens.remove(token);
    }

    @Override
    public boolean isAuthenticated(final Context context) {
        checkNotNull(context);
        if (context.getUserID().isPresent()) {
            return true;
        }
        if ( ! context.getAuthenticationToken().isPresent()) {
            return false;
        }

        final Optional<String> token = context.getAuthenticationToken();
        return (token.isPresent() && tokens.containsKey(token.get()));
    }

    @Override
    public Optional<ID> getSubject(final Context context) {
        if (context.getAuthenticationToken().isPresent()) {
            return Optional.fromNullable(tokens.get(context.getAuthenticationToken().get()));
        }
        return Optional.absent();
    }

    @Override
    public String generate(final ID subjectID, final Map<String, Object> properties) {
        checkNotNull(subjectID);

        final String token = UUID.randomUUID().toString();
        addToken(token, subjectID);

        return token;
    }

}
