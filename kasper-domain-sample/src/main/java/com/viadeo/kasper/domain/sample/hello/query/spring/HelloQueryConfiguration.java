// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.query.spring;

import com.viadeo.kasper.domain.sample.hello.query.handler.adapters.NormalizeBuddyQueryInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HelloQueryConfiguration {

    @Bean
    public NormalizeBuddyQueryInterceptor normalizeBuddyQueryInterceptor(){
        return new NormalizeBuddyQueryInterceptor();
    }

}
