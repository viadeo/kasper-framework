package com.viadeo.kasper.client.platform.components.eventbus;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.Cluster;
import org.axonframework.eventhandling.ClusterMetaData;
import org.axonframework.eventhandling.DefaultClusterMetaData;
import org.axonframework.eventhandling.EventListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;


public class CompositeCluster implements SmartlifeCycleCluster {

    private final List<SmartlifeCycleCluster> clusters;
    private final DefaultClusterMetaData metadata;

    public CompositeCluster(List<SmartlifeCycleCluster> clusters) {
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
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        for (SmartlifeCycleCluster cluster : clusters) {
            cluster.stop();
        }
        callback.run();
    }

    @Override
    public void start() {
        for (SmartlifeCycleCluster cluster : clusters) {
            cluster.start();
        }
    }

    @Override
    public void stop() {
        for (SmartlifeCycleCluster cluster : clusters) {
            cluster.stop();
        }
    }

    @Override
    public boolean isRunning() {
        for (SmartlifeCycleCluster cluster : clusters) {
            if (cluster.isRunning()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
