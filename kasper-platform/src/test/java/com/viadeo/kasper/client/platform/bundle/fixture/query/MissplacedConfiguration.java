package com.viadeo.kasper.client.platform.bundle.fixture.query;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MissplacedConfiguration {

    @Bean
    public Object missplacedBean() {
        return new Object();
    }
}
