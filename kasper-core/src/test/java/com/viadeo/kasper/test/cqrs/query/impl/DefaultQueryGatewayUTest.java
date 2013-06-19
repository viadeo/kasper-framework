package com.viadeo.kasper.test.cqrs.query.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.context.IContext;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.cqrs.query.IQuery;
import com.viadeo.kasper.cqrs.query.IQueryDTO;
import com.viadeo.kasper.cqrs.query.IQueryMessage;
import com.viadeo.kasper.cqrs.query.IQueryService;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.impl.DefaultQueryGateway;
import com.viadeo.kasper.locators.IQueryServicesLocator;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class DefaultQueryGatewayUTest {

    private class ServiceWhichRaiseExceptionQuery implements IQuery {
    }

    private class TestDTO implements IQueryDTO {
    }

    // ------------------------------------------------------------------------

    public class ServiceWhichRaiseException implements IQueryService<ServiceWhichRaiseExceptionQuery, TestDTO> {
        @Override
        public TestDTO retrieve(final IQueryMessage<ServiceWhichRaiseExceptionQuery> message) throws Exception {
            return null;
        }
    }

    private IContext defaultContext() {
        return new DefaultContextBuilder().buildDefault();
    }

    private DefaultQueryGateway getQueryGatewayForQueryAndService(final IQuery query, final IQueryService service) {

        // Associates Query and Service
        final IQueryServicesLocator locator = mock(IQueryServicesLocator.class);
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
                Matchers.<IQueryMessage<ServiceWhichRaiseExceptionQuery>> any());

        final IQuery query = new ServiceWhichRaiseExceptionQuery();
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
                .retrieve(Matchers.<IQueryMessage<ServiceWhichRaiseExceptionQuery>> any());

        final IQuery query = new ServiceWhichRaiseExceptionQuery();
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