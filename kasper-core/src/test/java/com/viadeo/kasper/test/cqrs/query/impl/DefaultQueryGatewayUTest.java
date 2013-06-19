package com.viadeo.kasper.test.cqrs.query.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryDTO;
import com.viadeo.kasper.cqrs.query.QueryMessage;
import com.viadeo.kasper.cqrs.query.QueryService;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.impl.DefaultQueryGateway;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class DefaultQueryGatewayUTest {

    private class ServiceWhichRaiseExceptionQuery implements Query {
    }

    private class TestDTO implements QueryDTO {
    }

    // ------------------------------------------------------------------------

    public class ServiceWhichRaiseException implements QueryService<ServiceWhichRaiseExceptionQuery, TestDTO> {
        @Override
        public TestDTO retrieve(final QueryMessage<ServiceWhichRaiseExceptionQuery> message) throws Exception {
            return null;
        }
    }

    private Context defaultContext() {
        return new DefaultContextBuilder().build();
    }

    private DefaultQueryGateway getQueryGatewayForQueryAndService(final Query query, final QueryService service) {

        // Associates Query and Service
        final QueryServicesLocator locator = mock(QueryServicesLocator.class);
        when(locator.getServiceFromQueryClass(query.getClass())).thenReturn(Optional.of(service));

        // Create the queryGateway with mocked locator
        final DefaultQueryGateway queryGateway = new DefaultQueryGateway();
        queryGateway.setQueryServicesLocator(locator);

        return queryGateway;
    }

    // ------------------------------------------------------------------------

    @Test
    @Ignore
    /* TODO does everyone agree on that kasper should not wrap exceptions unless 
    * it can add something useful to it?
    */
    public void retrieve_should_WrapAnyException_InKasperException() throws Exception {

        // Given - a_service_which_raise_exception;
        final ServiceWhichRaiseException service = Mockito.spy(new ServiceWhichRaiseException());
        doThrow(new FileNotFoundException("Exception in the service implementation")).when(service).retrieve(
                Matchers.<QueryMessage<ServiceWhichRaiseExceptionQuery>> any());

        final Query query = new ServiceWhichRaiseExceptionQuery();
        final DefaultQueryGateway queryGateway = getQueryGatewayForQueryAndService(query, service);

        // When
        try {
            queryGateway.retrieve(query, defaultContext());
            fail("Should raise a KasperQueryException");
        }

        // Then
        catch (final Exception e) {
            if (e instanceof KasperQueryException) {
                // OK. Expected a KasperQueryException
                // Verify that root exception is correctly wrapped
                assertEquals(FileNotFoundException.class, e.getCause().getClass());
            } else {
                fail("Should only raise a KasperQueryException, not a " + e.getClass().getName());
            }
        }

    }

    @Test
    public void retrieve_shouldNot_ReWrapKasperException() throws Exception {

        // Given - a_service_which_raise_exception;
        final ServiceWhichRaiseException service = Mockito.spy(new ServiceWhichRaiseException());
        doThrow(new KasperQueryException("a KasperQueryException in the service implementation")).when(service)
                .retrieve(Matchers.<QueryMessage<ServiceWhichRaiseExceptionQuery>> any());

        final Query query = new ServiceWhichRaiseExceptionQuery();
        DefaultQueryGateway queryGateway = getQueryGatewayForQueryAndService(query, service);

        // When
        try {
            queryGateway.retrieve(query, defaultContext());
            fail("Should raise a KasperQueryException");
        }

        // Then
        catch (final Exception e) {
            if (e instanceof KasperQueryException) {
                // OK. Expected a KasperQueryException
                // Verify that the root KasperQueryException is not rewrapped by the Framework
                assertEquals(null, e.getCause());
            } else {
                fail("Should only raise a KasperQueryException, not a " + e.getClass().getName());
            }
        }
    }

}
