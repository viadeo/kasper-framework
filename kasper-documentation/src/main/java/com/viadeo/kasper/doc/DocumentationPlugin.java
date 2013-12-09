package com.viadeo.kasper.doc;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.Plugin;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptor;
import com.viadeo.kasper.doc.element.DocumentedPlatform;
import com.viadeo.kasper.doc.initializer.DefaultDocumentedElementInitializer;
import com.viadeo.kasper.doc.web.KasperDocResource;

public class DocumentationPlugin implements Plugin {

    private final DocumentedPlatform documentedPlatform;
    private boolean initialized;

    public DocumentationPlugin() {
        this(new DocumentedPlatform());

    }

    protected DocumentationPlugin(DocumentedPlatform documentedPlatform) {
        this.documentedPlatform = documentedPlatform;
        this.initialized = false;
    }

    @Override
    public void initialize(Platform platform, MetricRegistry metricRegistry, DomainDescriptor... domainDescriptors) {
        Preconditions.checkNotNull(domainDescriptors);

        for (DomainDescriptor domainDescriptor : domainDescriptors) {
            documentedPlatform.registerDomain(domainDescriptor.getName(), domainDescriptor);
        }

        documentedPlatform.accept(new DefaultDocumentedElementInitializer(documentedPlatform));
        initialized = true;
    }

    public KasperDocResource getKasperDocResource() {
        Preconditions.checkState(initialized, "The documentation plugin must be initialized");
        return new KasperDocResource(documentedPlatform);
    }

    protected boolean isInitialized() {
        return initialized;
    }
}
