package com.viadeo.kasper.client.platform.bundle.fixture.query.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryConfiguration {

    @Bean
    public Object queryBean() {
        return new Object();
    }
}
