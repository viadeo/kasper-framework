package com.viadeo.kasper.platform.bundle.fixture.query;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MissplacedConfiguration {

    @Bean
    public Object missplacedBean() {
        return new Object();
    }
}
