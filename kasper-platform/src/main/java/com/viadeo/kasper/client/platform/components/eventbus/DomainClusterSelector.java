// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus;

import com.google.common.base.Function;
import org.axonframework.eventhandling.AbstractClusterSelector;
import org.axonframework.eventhandling.Cluster;
import org.axonframework.eventhandling.EventListener;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class DomainClusterSelector extends AbstractClusterSelector {

    private final Pattern pattern;
    private final HashMap<String, Cluster> cache;
    private final Function<String, Cluster> clusterFunction;

    public DomainClusterSelector(final String prefix, final Function<String,Cluster> clusterFunction) {
        final String escapedPrefix = checkNotNull(prefix).replace(".", "\\.");
        final String format = String.format("%s\\.([^\\.]*).*", escapedPrefix);
        this.pattern = Pattern.compile(format);
        this.cache = new HashMap<>();
        this.clusterFunction = checkNotNull(clusterFunction);
    }

    @Override
    protected Cluster doSelectCluster(EventListener eventListener, Class<?> listenerType) {

        final String packageName = listenerType.getPackage().getName();
        final Matcher matcher = pattern.matcher(packageName);
        checkArgument(matcher.matches(), "Unable to match domain name from package <%s> using regex <%s>", packageName, pattern);

        final String group = matcher.group(1);
        Cluster cluster = cache.get(group);

        if (null == cluster) {
            cluster = clusterFunction.apply(group);
            cache.put(group, cluster);
        }

        return cluster;
    }
}
