package com.viadeo.kasper.core.component.eventbus;

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
