// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor.resilience;

import com.google.common.collect.Sets;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.viadeo.kasper.api.exception.KasperCommandException;
import com.viadeo.kasper.api.exception.KasperEventException;
import com.viadeo.kasper.api.exception.KasperQueryException;
import com.viadeo.kasper.api.exception.KasperSecurityException;
import com.viadeo.kasper.api.response.CoreReasonCode;
import org.axonframework.commandhandling.interceptors.JSR303ViolationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.validation.ConstraintViolation;

public class ResilientPolicyUTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private ResilientPolicy policy;

    @Before
    public void setUp() throws Exception {
        policy = new ResilientPolicy();
    }

    @Test
    public void manage_a_kasper_query_exception_is_ok() {
        policy.manage(new KasperQueryException(new RuntimeException()));
    }

    @Test
    public void manage_a_kasper_command_exception_is_ok() {
        policy.manage(new KasperCommandException(new RuntimeException()));
    }

    @Test
    public void manage_a_kasper_event_exception_is_ok() {
        policy.manage(new KasperEventException(new RuntimeException()));
    }

    @Test
    public void manage_a_kasper_security_exception_throws_a_hystrix_bad_request_exception() {
        exception.expect(HystrixBadRequestException.class);
        policy.manage(new KasperSecurityException("fake", CoreReasonCode.UNKNOWN_REASON));
    }

    @Test
    public void manage_a_jsr303_voalation_exception_throws_a_hystrix_bad_request_exception() {
        exception.expect(HystrixBadRequestException.class);
        policy.manage(new JSR303ViolationException("fake", Sets.<ConstraintViolation<Object>>newHashSet()));
    }
}
