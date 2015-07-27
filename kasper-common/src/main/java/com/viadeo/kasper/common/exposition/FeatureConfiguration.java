// ============================================================================
// KASPER - Kasper is the treasure keeper
// www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
// Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.exposition;

import com.google.common.collect.Sets;

import java.util.Set;

public class FeatureConfiguration {

    private final Set<Feature> features = Sets.newHashSet();

    // ------------------------------------------------------------------------

    public FeatureConfiguration() { }

    //-------------------------------------------------------------------------

    public FeatureConfiguration(final FeatureConfiguration configuration) {
        this.features.addAll(configuration.features);
    }

    public boolean has(final Feature feature) {
        return features.contains(feature);
    }

    public FeatureConfiguration enable(final Feature feature) {
        features.add(feature);
        return this;
    }

    public FeatureConfiguration disable(final Feature feature) {
        features.remove(feature);
        return this;
    }

}
