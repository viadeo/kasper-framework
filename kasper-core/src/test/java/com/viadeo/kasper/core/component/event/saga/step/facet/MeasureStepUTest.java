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
package com.viadeo.kasper.core.component.event.saga.step.facet;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.core.component.event.saga.SagaIdReconciler;
import com.viadeo.kasper.core.component.event.saga.TestFixture;
import com.viadeo.kasper.core.component.event.saga.step.Steps;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.core.resolvers.DomainResolver;
import com.viadeo.kasper.core.resolvers.ResolverFactory;
import com.viadeo.kasper.core.resolvers.SagaResolver;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class MeasureStepUTest {

    @Before
    public void setUp() throws Exception {
        SagaResolver sagaResolver = new SagaResolver();
        sagaResolver.setDomainResolver(new DomainResolver());

        ResolverFactory resolverFactory = new ResolverFactory();
        resolverFactory.setSagaResolver(sagaResolver);

        KasperMetrics.setResolverFactory(resolverFactory);
    }

    @Test
    public void getMetricName_fromMeasurableStartStep_isOk() throws Exception {
        // Given
        MetricRegistry metricRegistry = new MetricRegistry();
        Steps.StartStep startStep = new Steps.StartStep(TestFixture.getMethod(TestFixture.TestSagaA.class, "handle", TestFixture.TestEvent.class), "getId", mock(SagaIdReconciler.class));
        MeasureStep step = new MeasureStep(metricRegistry, startStep);

        // When
        String metricName = step.getMetricName();

        // Then
        assertNotNull(metricName);
        assertEquals("test.saga.testsagaa.start", metricName);
    }
}
