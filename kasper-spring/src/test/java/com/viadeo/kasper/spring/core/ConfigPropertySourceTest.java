// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Kasper Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring.core;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.ConfigFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.MutablePropertySources;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class ConfigPropertySourceTest {

    @Configuration
    public static class PropertyConfiguration {

        @Bean
        public PropertySourcesPlaceholderConfigurer properties() {
            return new PropertySourcesPlaceholderConfigurer();
        }
    }

    static class MyBean {

        List<String> foo;
        Integer number;

        @Inject
        MyBean(@Value("${foo}") List<String> foo, @Value("${number}") Integer number) {
            this.foo = foo;
            this.number = number;
        }
    }

    static class BeanReferingToUnknownProperty {

        List<String> foo;

        @Inject
        BeanReferingToUnknownProperty(@Value("${unknown}") List<String> foo) {
            this.foo = foo;
        }
    }

    private AnnotationConfigApplicationContext ctx;

    @Before
    public void setUp() throws Exception {
        ctx = new AnnotationConfigApplicationContext();
        final MutablePropertySources sources = ctx.getEnvironment().getPropertySources();
        sources.addLast(new ConfigPropertySource(ConfigFactory.parseMap(ImmutableMap.of("foo", "bar", "number", 1)), "boo"));
    }

    @Test
    public void getRequiredProperty_resolve_stringProperty() throws Exception {

        ctx.refresh();
        String value = ctx.getEnvironment().getProperty("foo");

        assertEquals("bar", value);
        assertEquals(Integer.valueOf(1), ctx.getEnvironment().getProperty("number", Integer.class));
    }

    @Test
    public void getRequiredProperty_resolve_complexProperty() throws Exception {
        ctx.register(MyBean.class, PropertyConfiguration.class);
        ctx.refresh();

        assertEquals(Collections.singletonList("bar"), ctx.getBean(MyBean.class).foo);
        assertEquals(Integer.valueOf(1), ctx.getBean(MyBean.class).number);

    }


    @Test(expected = IllegalArgumentException.class)
    public void getRequiredProperty_ThrowIllegalArgumentException_WithUnknownProperty() {

        ctx.register(BeanReferingToUnknownProperty.class, PropertyConfiguration.class);
        ctx.refresh();

        assertEquals(Arrays.asList("bar"), ctx.getBean(MyBean.class).foo);
    }
}