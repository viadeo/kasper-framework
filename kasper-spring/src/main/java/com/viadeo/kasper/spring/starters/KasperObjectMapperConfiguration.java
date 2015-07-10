package com.viadeo.kasper.spring.starters;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.viadeo.kasper.api.IDBuilder;
import com.viadeo.kasper.common.serde.IDModule;
import com.viadeo.kasper.common.serde.MetaDataDeserializer;
import com.viadeo.kasper.common.serde.TrimDeserializer;
import com.viadeo.kasper.context.ContextHelper;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import org.axonframework.domain.MetaData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class KasperObjectMapperConfiguration {

    @Bean
    @SuppressWarnings("unused")
    public Module metaDataModule(ContextHelper contextHelper) {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(MetaData.class, new MetaDataDeserializer(contextHelper));
        return module;
    }

    @Bean
    @SuppressWarnings("unused")
    public Module sanitizeModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new TrimDeserializer(new StringDeserializer()));

        return module;
    }


    @Bean
    public Module idModule(IDBuilder idBuilder) {
        return new IDModule(idBuilder);
    }

    /**
     * The object mapper instance used for global ser-de
     *
     * @return object mapper
     */
    @Bean
    public ObjectMapper objectMapper(List<Module> modules) {
        ObjectMapper objectMapper = ObjectMapperProvider.INSTANCE.mapper();
        objectMapper.registerModules(modules);

        return objectMapper;
    }

}
