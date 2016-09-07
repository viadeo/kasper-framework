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
package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;


public class CompositeCluster implements Cluster {

    private final List<Cluster> clusters;
    private final DefaultClusterMetaData metadata;

    public CompositeCluster(List<Cluster> clusters) {
        this.clusters = checkNotNull(clusters);
        this.metadata = new DefaultClusterMetaData();
    }

    @Override
    public String getName() {
        List<String> names = Lists.transform(clusters, new Function<Cluster, String>() {
            @Override
            public String apply(Cluster cluster) {
                return cluster.getName();
            }
        });
        return Joiner.on("_").join(names);
    }

    @Override
    public void publish(EventMessage... events) {
        for (Cluster cluster : clusters) {
            cluster.publish(events);
        }
    }

    @Override
    public void subscribe(EventListener eventListener) {
        for (Cluster cluster : clusters) {
            cluster.subscribe(eventListener);
        }
    }

    @Override
    public void unsubscribe(EventListener eventListener) {
        for (Cluster cluster : clusters) {
            cluster.unsubscribe(eventListener);
        }
    }

    @Override
    public Set<EventListener> getMembers() {

        Set<EventListener> all = new HashSet<>();
        for (Cluster cluster : clusters) {
            all.addAll(cluster.getMembers());
        }

        return all;
    }

    @Override
    public ClusterMetaData getMetaData() {
        return metadata;
    }

    @Override
    public void subscribeEventProcessingMonitor(EventProcessingMonitor monitor) {
        for (Cluster cluster : clusters) {
            cluster.subscribeEventProcessingMonitor(monitor);
        }
    }

    @Override
    public void unsubscribeEventProcessingMonitor(EventProcessingMonitor monitor) {
        for (Cluster cluster : clusters) {
            cluster.unsubscribeEventProcessingMonitor(monitor);
        }
    }
}
