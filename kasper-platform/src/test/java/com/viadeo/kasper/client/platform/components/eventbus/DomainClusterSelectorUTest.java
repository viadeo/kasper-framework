// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus;

import com.google.common.base.Function;
import com.viadeo.kasper.client.platform.components.eventbus.fixture.domainA.DomainA;
import com.viadeo.kasper.client.platform.components.eventbus.fixture.domainB.DomainB;
import org.axonframework.eventhandling.Cluster;
import org.axonframework.eventhandling.SimpleCluster;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;


public class DomainClusterSelectorUTest {

    public static final Function<String,Cluster> CLUSTER_FUNCTION = new Function<String, Cluster>() {
        @Override
        public Cluster apply(String name) {
            return new SimpleCluster(name);
        }
    };

    private DomainClusterSelector clusterSelector;

    @Before
    public void setUp() throws Exception {
        clusterSelector = new DomainClusterSelector(
                "com.viadeo.kasper.client.platform.components.eventbus.fixture",
                CLUSTER_FUNCTION);
    }

    @Test(expected = NullPointerException.class)
    public void init_withNullAsGivenPrefix_throwException(){
        // Given nothing

        // When
        new DomainClusterSelector(null, CLUSTER_FUNCTION);

        // Then throws an exception
    }

    @Test
    public void init_withPrefix_withFunction_isOk(){
        // Given nothing

        // When
        new DomainClusterSelector("", CLUSTER_FUNCTION);

        // Then throws an exception
    }

    @Test(expected = NullPointerException.class)
    public void init_withNullAsGivenFunction_throwException(){
        // Given nothing

        // When
        new DomainClusterSelector("", null);

        // Then throws an exception
    }

    @Test(expected = NullPointerException.class)
    public void selectCluster_withNullAsGivenEventListener_throwException() throws Exception {
        // Given nothing

        // When
        clusterSelector.selectCluster(null);

        // Then throws an exception
    }

    @Test
    public void selectCluster_withEventListenerB_returnClusterNamedAfterPackageOfListener() throws Exception {
        // Given
        final DomainB.EventListenerB eventListenerB = new DomainB.EventListenerB();

        // When
        final Cluster cluster = clusterSelector.selectCluster(eventListenerB);

        // Then
        assertNotNull(cluster);
        assertEquals("domainB", cluster.getName());
    }

    @Test
    public void selectCluster_withEventListenerA_returnClusterNamedAfterPackageOfListener() throws Exception {
        // Given
        final DomainA.EventListenerA eventListenerA = new DomainA.EventListenerA();

        // When
        final Cluster cluster = clusterSelector.selectCluster(eventListenerA);

        // Then
        assertNotNull(cluster);
        assertEquals("domainA", cluster.getName());
    }

    @Test
    public void selectCluster_withTwoEventListenersOfTheSameDomain_returnTheSameInstance() throws Exception {
        // Given
        final DomainB.EventListenerB eventListenerB = new DomainB.EventListenerB();
        final DomainB.EventListenerA eventListenerA = new DomainB.EventListenerA();

        // When
        final Cluster cluster1 = clusterSelector.selectCluster(eventListenerB);
        final Cluster cluster2 = clusterSelector.selectCluster(eventListenerA);

        // Then
        assertNotNull(cluster1);
        assertNotNull(cluster2);
        assertSame(cluster1, cluster2);
    }

}
