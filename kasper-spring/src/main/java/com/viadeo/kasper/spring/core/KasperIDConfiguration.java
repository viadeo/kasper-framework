// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring.core;

import com.viadeo.kasper.api.id.Format;
import com.viadeo.kasper.api.id.IDBuilder;
import com.viadeo.kasper.api.id.IDTransformer;
import com.viadeo.kasper.api.id.TransformableIDBuilder;
import com.viadeo.kasper.core.id.Converter;
import com.viadeo.kasper.core.id.ConverterRegistry;
import com.viadeo.kasper.core.id.DefaultIDTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class KasperIDConfiguration {

    @Autowired(required = false)
    List<Format> formats;

    @Autowired(required = false)
    List<Converter> converters;

    @Bean
    public ConverterRegistry idConverterRegistry() {
        ConverterRegistry converterRegistry = new ConverterRegistry();
        if (converters != null) {
            for (Converter converter : converters) {
                converterRegistry.register(converter);
            }
        }
        return converterRegistry;
    }

    @Bean
    public IDTransformer idTransformer(ConverterRegistry converterRegistry) {
        return new DefaultIDTransformer(converterRegistry);
    }

    @Bean
    public IDBuilder idBuilder(IDTransformer idTransformer) {
        return new TransformableIDBuilder(
                idTransformer,
                formats != null ? formats.toArray(new Format[formats.size()]) : new Format[] {}
        );
    }
}
