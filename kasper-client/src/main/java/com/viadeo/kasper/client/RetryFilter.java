package com.viadeo.kasper.client;

import com.google.common.base.Throwables;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;


public class RetryFilter extends ClientFilter {
    private final Logger LOGGER = LoggerFactory.getLogger(RetryFilter.class);
    private int numberOfRetries;

    public RetryFilter(int numberOfRetries) {
        this.numberOfRetries = numberOfRetries;
    }

    @Override
    public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
        int i = 0;
        while (i < numberOfRetries) {
            i++;
            try {
                return getNext().handle(cr);
            } catch (ClientHandlerException e) {
                Throwable cause = e.getCause();
                if(cause instanceof ConnectException) {
                    LOGGER.info("exception <{}> ({}), retry {}  more time(s)", e.getMessage(), cr.getURI(), numberOfRetries - i);
                } else {
                    throw Throwables.propagate(e);
                }
            }
        }
        throw new ClientHandlerException("Connection retries limit exceeded.");
    }
}
