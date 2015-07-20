package com.viadeo.kasper.exposition.http;

import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.Meta;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.core.component.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.core.component.query.gateway.QueryGateway;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.exposition.ExposureDescriptor;
import org.joda.time.DateTime;
import org.junit.Before;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpQueryExposerITest {

    private HttpQueryExposer exposer;

    @Before
    public void setUp() throws Exception {
        CommandGateway commandGateway = mock(CommandGateway.class);
        QueryGateway queryGateway = mock(QueryGateway.class);
        KasperEventBus eventBus = mock(KasperEventBus.class);
        Meta meta = new Meta("nc", DateTime.now(), DateTime.now());

        Platform platform = mock(Platform.class);
        when(platform.getCommandGateway()).thenReturn(commandGateway);
        when(platform.getQueryGateway()).thenReturn(queryGateway);
        when(platform.getEventBus()).thenReturn(eventBus);
        when(platform.getMeta()).thenReturn(meta);

        exposer = new HttpQueryExposer(platform, Lists.<ExposureDescriptor<Query,QueryHandler>>newArrayList());
    }


}
