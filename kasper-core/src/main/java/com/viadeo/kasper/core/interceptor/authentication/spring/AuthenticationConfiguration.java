// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor.authentication.spring;

import com.viadeo.kasper.core.interceptor.authentication.InMemoryAuthentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthenticationConfiguration {

    @Bean
    public InMemoryAuthentication inMemoryAuthentication(){
        return new InMemoryAuthentication();
    }

}
