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
package com.viadeo.kasper.core.interceptor;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

public class InterceptorChainRegistry<INPUT, OUTPUT> {

    private final List<InterceptorFactory<INPUT, OUTPUT>> interceptorFactories;
    private final ConcurrentMap<Class, InterceptorChain<INPUT, OUTPUT>> chains;

    // ------------------------------------------------------------------------

    public InterceptorChainRegistry() {
        this(Lists.<InterceptorFactory<INPUT, OUTPUT>>newArrayList());
    }

    public InterceptorChainRegistry(final List<InterceptorFactory<INPUT, OUTPUT>> interceptorFactories) {
        this.interceptorFactories = Lists.newArrayList(checkNotNull(interceptorFactories));
        this.chains = Maps.newConcurrentMap();
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Optional<InterceptorChain<INPUT, OUTPUT>> create(final Class key,
                                                            final InterceptorFactory<INPUT, OUTPUT> tailFactory) {
        checkNotNull(key);
        checkNotNull(tailFactory);

        Optional<InterceptorChain<INPUT, OUTPUT>> optionalInterceptorChain = get(key);

        if( ! optionalInterceptorChain.isPresent()){
            final List<InterceptorFactory<INPUT, OUTPUT>> factories = Lists.newArrayList();
            factories.addAll(interceptorFactories);
            factories.add(tailFactory);

            optionalInterceptorChain = new CompositeInterceptorFactory<>(factories).create(TypeToken.of(key));
            chains.putIfAbsent(key, optionalInterceptorChain.get());
        }

        return optionalInterceptorChain;
    }

    // ------------------------------------------------------------------------

    public Optional<InterceptorChain<INPUT, OUTPUT>> get(final Class key) {
        return Optional.fromNullable(chains.get(checkNotNull(key)));
    }

    // ------------------------------------------------------------------------

    public void register(InterceptorFactory<INPUT, OUTPUT> interceptorFactory) {
        this.interceptorFactories.add(checkNotNull(interceptorFactory));
    }

}
