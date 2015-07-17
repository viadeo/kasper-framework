// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.id.spring;

import com.viadeo.kasper.api.id.IDBuilder;
import com.viadeo.kasper.core.id.Converter;
import com.viadeo.kasper.core.id.ConverterRegistry;
import com.viadeo.kasper.core.id.TestConverters;
import com.viadeo.kasper.core.id.TestFormats;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.*;

public class IDConfigurationITest {

    @Test
    public void refresh_an_application_context_with_id_configuration_and_without_converters() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(IDConfiguration.class);
        applicationContext.refresh();

        ConverterRegistry converterRegistry = applicationContext.getBean(ConverterRegistry.class);
        assertNotNull(converterRegistry);
        assertTrue(converterRegistry.getConvertersByFormats("viadeo").isEmpty());
    }

    @Test
    public void refresh_an_application_context_with_id_configuration_and_with_available_converters() {
        Converter uuidToIdConverter = TestConverters.mockConverter("viadeo", TestFormats.UUID, TestFormats.ID);

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(IDConfiguration.class);
        applicationContext.getBeanFactory().registerSingleton("uuidToIdConverter", uuidToIdConverter);
        applicationContext.refresh();

        ConverterRegistry converterRegistry = applicationContext.getBean(ConverterRegistry.class);
        assertNotNull(converterRegistry);
        assertFalse(converterRegistry.getConvertersByFormats("viadeo").isEmpty());
        assertTrue(converterRegistry.getConvertersByFormats("viadeo").containsValue(uuidToIdConverter));
    }

    @Test
    public void refresh_an_application_context_with_id_configuration_and_without_formats() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(IDConfiguration.class);
        applicationContext.refresh();

        IDBuilder idBuilder = applicationContext.getBean(IDBuilder.class);
        assertNotNull(idBuilder);
        assertTrue(idBuilder.getSupportedFormats().isEmpty());
    }

    @Test
    public void refresh_an_application_context_with_id_configuration_and_with_available_formats() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(IDConfiguration.class);
        applicationContext.getBeanFactory().registerSingleton("uuidFormat", TestFormats.UUID);
        applicationContext.getBeanFactory().registerSingleton("idFormat", TestFormats.ID);
        applicationContext.refresh();

        IDBuilder idBuilder = applicationContext.getBean(IDBuilder.class);
        assertNotNull(idBuilder);
        assertFalse(idBuilder.getSupportedFormats().isEmpty());
        assertTrue(idBuilder.getSupportedFormats().contains(TestFormats.UUID));
        assertTrue(idBuilder.getSupportedFormats().contains(TestFormats.ID));
    }
}
