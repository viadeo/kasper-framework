// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
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

    public void revoke(final String token) {
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
