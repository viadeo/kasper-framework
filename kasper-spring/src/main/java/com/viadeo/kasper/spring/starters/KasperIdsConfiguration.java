package com.viadeo.kasper.spring.starters;

import com.viadeo.kasper.api.IDBuilder;
import com.viadeo.kasper.api.IDTransformer;
import com.viadeo.kasper.api.TransformableIDBuilder;
import com.viadeo.kasper.core.ids.DefaultIDTransformer;
import com.viadeo.kasper.core.ids.KasperIDsConverterRegistry;
import com.viadeo.kasper.core.ids.UUIDFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KasperIdsConfiguration {

    @Bean
    public KasperIDsConverterRegistry idConverterRegistry() {
        KasperIDsConverterRegistry kasperIDsConverterRegistry = new KasperIDsConverterRegistry();
        return kasperIDsConverterRegistry;
    }

    @Bean
    public IDTransformer idTransformer(KasperIDsConverterRegistry kasperIDsConverterRegistry) {
        return new DefaultIDTransformer(kasperIDsConverterRegistry);
    }

    @Bean
    public UUIDFormat uuidFormat() {
        return new UUIDFormat();
    }

    @Bean
    public IDBuilder idBuilder(IDTransformer idTransformer, UUIDFormat uuidformat) {
        return new TransformableIDBuilder(
                idTransformer,
                uuidformat
        );
    }

}
