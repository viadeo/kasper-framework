// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring.core;

import com.typesafe.config.Config;
import com.viadeo.kasper.api.context.ContextHelper;
import com.viadeo.kasper.api.context.Version;
import com.viadeo.kasper.api.id.IDBuilder;
import com.viadeo.kasper.core.context.DefaultContextHelper;
import com.viadeo.kasper.core.context.DefaultVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KasperContextConfiguration {

    @Bean
    public Version contextVersion(final Config config) {
        int currentApplicationVersion = config.getInt("runtime.context.application.version");
        int currentClientVersion = config.getInt("runtime.context.client.version");
        return new DefaultVersion(currentApplicationVersion, currentClientVersion);
    }

    @Bean
    public ContextHelper contextHelper(final Version version, final IDBuilder idBuilder) {
        return new DefaultContextHelper(version, idBuilder);
    }
}
