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
package com.viadeo.kasper.core.interceptor.measure;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.response.KasperResponse;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.InterceptorFactory;
import com.viadeo.kasper.core.metrics.MetricNames;

import static com.google.common.base.Preconditions.checkNotNull;

public class MeasuredInterceptor<INPUT> implements Interceptor<INPUT, KasperResponse> {

    private final MetricRegistry metricRegistry;
    private final MetricNames metricNames;

    public MeasuredInterceptor(final Class<?> componentClass, final MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
        this.metricNames = MetricNames.of(componentClass);
    }

    @Override
    public KasperResponse process(INPUT input, Context context, InterceptorChain<INPUT, KasperResponse> chain) {
        Timer.Context timer = metricRegistry.timer(metricNames.requestsTime).time();

        KasperResponse response;

        try {
            response = chain.next(input, context);
        } finally {
            timer.stop();
        }

        switch (response.getStatus()) {
            case OK:
            case SUCCESS:
            case ACCEPTED:
            case REFUSED:
                // nothing
                break;

            case ERROR:
            case FAILURE:
                metricRegistry.meter(metricNames.errors).mark();
                break;
        }

        return response;
    }


    public static class Factory implements InterceptorFactory {

        private final Class<?> componentClass;
        private final MetricRegistry metricRegistry;

        public Factory(final Class<?> componentClass, final MetricRegistry metricRegistry) {
            this.componentClass = Preconditions.checkNotNull(componentClass);
            this.metricRegistry = Preconditions.checkNotNull(metricRegistry);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Optional<InterceptorChain> create(TypeToken type) {
            checkNotNull(type);
            return Optional.of(InterceptorChain.makeChain(new MeasuredInterceptor(componentClass, metricRegistry)));
        }
    }
}
