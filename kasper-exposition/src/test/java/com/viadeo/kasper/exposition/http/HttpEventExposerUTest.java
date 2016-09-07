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
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.event.listener.CommandEventListener;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.core.component.event.listener.QueryEventListener;
import com.viadeo.kasper.exposition.ExposureDescriptor;
import com.viadeo.kasper.platform.Meta;
import com.viadeo.kasper.platform.Platform;
import org.junit.Test;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpEventExposerUTest {

    public static class AEvent implements Event {
        private static final long serialVersionUID = -3423712338104227083L;
    }

    public static class EventListenerA extends CommandEventListener<AEvent> {
        @Override
        public EventResponse handle(Context context, AEvent event) {
            return EventResponse.success();
        }
    }

    public static class EventListenerB extends QueryEventListener<AEvent> {
        @Override
        public EventResponse handle(Context context, AEvent event) {
            return EventResponse.success();
        }
    }

    @Test
    public void init_withTwoEventListeners_listeningTheSameEvent_isOk() throws Exception {
        // Given
        final List<ExposureDescriptor<Event, EventListener>> descriptors = Lists.newArrayList();
        descriptors.add(new ExposureDescriptor<Event, EventListener>(AEvent.class, EventListenerA.class));
        descriptors.add(new ExposureDescriptor<Event, EventListener>(AEvent.class, EventListenerB.class));

        final ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getContextPath()).thenReturn("");

        final ServletConfig servletConfig = mock(ServletConfig.class);
        when(servletConfig.getServletContext()).thenReturn(servletContext);

        final Platform platform = mock(Platform.class);
        when(platform.getEventBus()).thenReturn(mock(KasperEventBus.class));
        when(platform.getMeta()).thenReturn(mock(Meta.class));

        final HttpEventExposer eventExposer = new HttpEventExposer(platform, descriptors);

        // When
        eventExposer.init(servletConfig);

        // Then is ok
    }
}
