// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring.starters;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.viadeo.kasper.api.id.IDBuilder;
import com.viadeo.kasper.common.serde.IDModule;
import com.viadeo.kasper.common.serde.MetaDataDeserializer;
import com.viadeo.kasper.common.serde.TrimDeserializer;
import com.viadeo.kasper.api.context.ContextHelper;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import org.axonframework.domain.MetaData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class KasperObjectMapperConfiguration {

    @Bean
    @SuppressWarnings("unused")
    public Module metaDataModule(final ContextHelper contextHelper) {
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(MetaData.class, new MetaDataDeserializer(contextHelper));
        return module;
    }

    @Bean
    @SuppressWarnings("unused")
    public Module sanitizeModule() {
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new TrimDeserializer(new StringDeserializer()));

        return module;
    }


    @Bean
    public Module idModule(final IDBuilder idBuilder) {
        return new IDModule(idBuilder);
    }

    /**
     * The object mapper instance used for global ser-de
     *
     * @param modules list of modules
     * @return object mapper
     */
    @Bean
    public ObjectMapper objectMapper(final List<Module> modules) {
        final ObjectMapper objectMapper = ObjectMapperProvider.INSTANCE.mapper();
        objectMapper.registerModules(modules);

        return objectMapper;
    }

}
