package com.viadeo.kasper.test.cqrs.query.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.context.IContext;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.cqrs.query.IQuery;
import com.viadeo.kasper.cqrs.query.IQueryDTO;
import com.viadeo.kasper.cqrs.query.IQueryMessage;
import com.viadeo.kasper.cqrs.query.IQueryService;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.impl.QueryGatewayBase;
import com.viadeo.kasper.locators.IQueryServicesLocator;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class QueryGatewayBaseUTest {

    private class ServiceWhichRaiseExceptionQuery implements IQuery {}
    private class TestDTO implements IQueryDTO {}

    public class ServiceWhichRaiseException implements IQueryService<ServiceWhichRaiseExceptionQuery, TestDTO> {
        @Override
        public TestDTO retrieve(final IQueryMessage<ServiceWhichRaiseExceptionQuery> message) throws Exception {
            return null;
        }
    }

    private IContext defaultContext() {
        return new DefaultContextBuilder().buildDefault();
    }

    private QueryGatewayBase getQueryGatewayForQueryAndService(IQuery query, IQueryService service) {

        // Associates Query and Service
        IQueryServicesLocator locator = mock(IQueryServicesLocator.class);
        when(locator.getServiceFromQueryClass(query.getClass())).thenReturn(Optional.of((IQueryService)service));

        // Create the queryGateway with mocked locator
        QueryGatewayBase queryGateway = new QueryGatewayBase();
        queryGateway.setQueryServicesLocator(locator);

        return queryGateway;
    }

    @Test
    public void retrieve_should_WrapAnyException_InKasperException() throws Exception {

        // Given - a_service_which_raise_exception;
        ServiceWhichRaiseException service = Mockito.spy(new ServiceWhichRaiseException());
        doThrow(new FileNotFoundException("Exception in the service implementation")).when(service).retrieve(Matchers.<IQueryMessage<ServiceWhichRaiseExceptionQuery>>any());

        IQuery query = new ServiceWhichRaiseExceptionQuery();
        QueryGatewayBase queryGateway = getQueryGatewayForQueryAndService(query, service);

        // When
        try {
            queryGateway.retrieve(defaultContext(), query);
            fail("Should raise a KasperQueryException");
        }
        // Then
        catch (Exception e) {
            if (e instanceof KasperQueryException) {
                // OK. Expected a KasperQueryException
                // Verify that root exception is correctly wrapped
                assertEquals(FileNotFoundException.class, e.getCause().getClass());
            }
            else {
                fail("Should only raise a KasperQueryException, not a " + e.getClass().getName());
            }
        }
    }

    @Test
    public void retrieve_shouldNot_ReWrapKasperException() throws Exception {

        // Given - a_service_which_raise_exception;
        ServiceWhichRaiseException service = Mockito.spy(new ServiceWhichRaiseException());
        doThrow(new KasperQueryException("a KasperQueryException in the service implementation")).when(service).retrieve(Matchers.<IQueryMessage<ServiceWhichRaiseExceptionQuery>>any());

        IQuery query = new ServiceWhichRaiseExceptionQuery();
        QueryGatewayBase queryGateway = getQueryGatewayForQueryAndService(query, service);

        // When
        try {
            queryGateway.retrieve(defaultContext(), query);
            fail("Should raise a KasperQueryException");
        }
        // Then
        catch (Exception e) {
            if (e instanceof KasperQueryException) {
                // OK. Expected a KasperQueryException
                // Verify that the root KasperQueryException is not rewrapped by the Framework
                assertEquals(null, e.getCause());
            }
            else {
                fail("Should only raise a KasperQueryException, not a " + e.getClass().getName());
            }
        }
    }

}

