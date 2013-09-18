package com.viadeo.kasper.query.exposition;

import java.util.HashSet;
import java.util.Set;

public class FeatureConfiguration {
    private final Set<Feature> features = new HashSet<Feature>();

    public FeatureConfiguration() {}

    public FeatureConfiguration(FeatureConfiguration configuration) {
        this.features.addAll(configuration.features);
    }

    public boolean has(Feature feature) {
        return features.contains(feature);
    }

    public FeatureConfiguration enable(Feature feature) {
        features.add(feature);
        return this;
    }

    public FeatureConfiguration disable(Feature feature) {
        features.remove(feature);
        return this;
    }
}
