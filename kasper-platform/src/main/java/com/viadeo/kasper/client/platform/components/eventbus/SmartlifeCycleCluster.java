package com.viadeo.kasper.client.platform.components.eventbus;

import org.axonframework.eventhandling.Cluster;
import org.springframework.context.SmartLifecycle;

public interface SmartlifeCycleCluster extends SmartLifecycle, Cluster {
}
