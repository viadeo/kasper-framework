// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring.starters;

import com.viadeo.kasper.api.id.IDBuilder;
import com.viadeo.kasper.api.id.IDTransformer;
import com.viadeo.kasper.api.id.TransformableIDBuilder;
import com.viadeo.kasper.core.ids.DefaultIDTransformer;
import com.viadeo.kasper.core.ids.KasperIDsConverterRegistry;
import com.viadeo.kasper.core.ids.UUIDFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KasperIdsConfiguration {

    @Bean
    public KasperIDsConverterRegistry idConverterRegistry() {
        return new KasperIDsConverterRegistry();
    }

    @Bean
    public IDTransformer idTransformer(final KasperIDsConverterRegistry kasperIDsConverterRegistry) {
        return new DefaultIDTransformer(kasperIDsConverterRegistry);
    }

    @Bean
    public UUIDFormat uuidFormat() {
        return new UUIDFormat();
    }

    @Bean
    public IDBuilder idBuilder(final IDTransformer idTransformer, final UUIDFormat uuidformat) {
        return new TransformableIDBuilder(
            idTransformer,
            uuidformat
        );
    }

}
