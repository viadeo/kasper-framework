// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http.spring;

import com.viadeo.kasper.api.context.ContextHelper;
import com.viadeo.kasper.doc.spring.KasperDocumentationConfiguration;
import com.viadeo.kasper.exposition.http.HttpContextDeserializer;
import com.viadeo.kasper.exposition.http.HttpContextWithVersionDeserializer;
import com.viadeo.kasper.exposition.http.HttpExposurePlugin;
import com.viadeo.kasper.platform.plugin.Plugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(KasperDocumentationConfiguration.class)
public class KasperHttpConfiguration {

    @Bean
    public HttpContextDeserializer httpContextDeserializer(final ContextHelper contextHelper) {
        return new HttpContextWithVersionDeserializer(contextHelper);
    }

    @Bean
    public Plugin httpExposurePlugin(final HttpContextDeserializer contextDeserializer) {
        return new HttpExposurePlugin(contextDeserializer);
    }
}
