// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptor;
import com.viadeo.kasper.client.platform.plugin.Plugin;
import com.viadeo.kasper.doc.element.DocumentedPlatform;
import com.viadeo.kasper.doc.initializer.DefaultDocumentedElementInitializer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class DocumentationPlugin implements Plugin {

    private final DocumentedPlatform documentedPlatform;
    private boolean initialized;

    // ------------------------------------------------------------------------

    public DocumentationPlugin() {
        this(new DocumentedPlatform());

    }

    protected DocumentationPlugin(final DocumentedPlatform documentedPlatform) {
        this.documentedPlatform = checkNotNull(documentedPlatform);
        this.initialized = false;
    }

    // ------------------------------------------------------------------------

    @Override
    public void initialize(final Platform platform, final MetricRegistry metricRegistry,
                           final DomainDescriptor... domainDescriptors) {

        for (final DomainDescriptor domainDescriptor : checkNotNull(domainDescriptors)) {
            documentedPlatform.registerDomain(domainDescriptor.getName(), domainDescriptor);
        }

        documentedPlatform.accept(new DefaultDocumentedElementInitializer(documentedPlatform));
        initialized = true;
    }

    public DocumentedPlatform getDocumentedPlatform() {
        checkState(initialized, "The documentation plugin must be initialized");
        return documentedPlatform;
    }

    protected boolean isInitialized() {
        return initialized;
    }

}
