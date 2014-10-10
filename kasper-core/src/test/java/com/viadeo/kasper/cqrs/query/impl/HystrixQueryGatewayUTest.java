package com.viadeo.kasper.cqrs.query.impl;

import com.codahale.metrics.MetricRegistry;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import org.axonframework.commandhandling.interceptors.JSR303ViolationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class HystrixQueryGatewayUTest {

    private QueryGateway queryGateway;
    private HystrixQueryGateway hystrixQueryGateway;
    private Query query;
    private Context context;

    @Before
    public void init() {
        queryGateway = mock(QueryGateway.class);
        hystrixQueryGateway = new HystrixQueryGateway(queryGateway, new MetricRegistry());
        query = mock(Query.class);
        context = mock(Context.class);
    }

    @Test(timeout = 2000)
    public void retrieve_should_fallback_on_timeout() throws Exception {

        // Given
        doAnswer(new SlowAnswer(2500)).when(queryGateway).retrieve(any(Query.class), any(Context.class));

        // When
        try {
            long initialFallbackCount = hystrixQueryGateway.getFallbackCount();
            hystrixQueryGateway.retrieve(query, context);

            // Then
            assertTrue(initialFallbackCount < hystrixQueryGateway.getFallbackCount());
        } catch (Exception e) {
            fail();
        }

    }


    @Test
    public void any_should_not_enter_fallback_on_interceptor_exceptions() throws Exception {
        //Given (exception throws by interceptor)
        doThrow(new JSR303ViolationException("error in validation", Collections.EMPTY_SET)).when(queryGateway).retrieve(any(Query.class), any(Context.class));



        // When
        try {
            long fallbackCount = hystrixQueryGateway.getFallbackCount();
            hystrixQueryGateway.retrieve(query, context);
            // Then
            // fallback count should not be incremented --> fallback method should not be invoked
            assertEquals(fallbackCount, hystrixQueryGateway.getFallbackCount());
        } catch (HystrixBadRequestException e) {
            // c'est normal, on rebalance l'exception a la couche appelante
        }

    }

    //-------------- test classes and methods

    @XKasperUnregistered
    private static final class TestQueryResult implements QueryResult { }


    // aie j'ai mal aux yeux

    private class SlowAnswer implements Answer<QueryResponse<TestQueryResult>> {

        private int sleepInMs = 10000; // default

        public SlowAnswer(int sleepInMs) {
            this.sleepInMs = sleepInMs;
        }

        @Override
        public QueryResponse<TestQueryResult> answer(InvocationOnMock invocation) {
            if (sleepInMs > 0) {
                try {
                    Thread.sleep(sleepInMs);
                } catch (InterruptedException e) {
                    // interrupted
                }
            }
            return QueryResponse.of(new TestQueryResult());
        }

    }
}