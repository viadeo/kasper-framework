// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
package com.viadeo.kasper.exposition.http;

import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.gateway.QueryGateway;
import com.viadeo.kasper.exposition.ExposureDescriptor;
import com.viadeo.kasper.platform.Meta;
import com.viadeo.kasper.platform.Platform;
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
