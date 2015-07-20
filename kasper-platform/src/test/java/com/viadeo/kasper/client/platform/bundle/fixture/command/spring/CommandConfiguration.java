package com.viadeo.kasper.client.platform.bundle.fixture.command.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandConfiguration {

    @Bean
    public Object commandBean() {
        return new Object();
    }

}
