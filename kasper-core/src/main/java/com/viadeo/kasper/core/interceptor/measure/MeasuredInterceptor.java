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
    public KasperResponse process(INPUT input, Context context, InterceptorChain<INPUT, KasperResponse> chain) throws Exception {
        metricRegistry.meter(metricNames.requests).mark();
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

        @Override
        public Optional<InterceptorChain> create(TypeToken type) {
            checkNotNull(type);
            return Optional.of(InterceptorChain.makeChain(new MeasuredInterceptor(componentClass, metricRegistry)));
        }
    }
}
