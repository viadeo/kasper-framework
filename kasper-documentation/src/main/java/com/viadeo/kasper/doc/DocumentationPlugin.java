// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc;

import com.google.common.collect.Lists;
import com.viadeo.kasper.doc.element.DocumentedPlatform;
import com.viadeo.kasper.doc.initializer.DefaultDocumentedElementInitializer;
import com.viadeo.kasper.platform.Platform;
import com.viadeo.kasper.platform.bundle.descriptor.DomainDescriptor;
import com.viadeo.kasper.platform.plugin.PluginAdapter;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class DocumentationPlugin extends PluginAdapter {

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

    public DocumentedPlatform getDocumentedPlatform() {
        checkState(initialized, "The documentation plugin must be initialized");
        return documentedPlatform;
    }

    protected boolean isInitialized() {
        return initialized;
    }

    @Override
    public void platformStarted(Platform platform) {
        documentedPlatform.accept(new DefaultDocumentedElementInitializer(documentedPlatform));
        initialized = true;
    }

    @Override
    public void domainRegistered(DomainDescriptor domainDescriptor) {
        documentedPlatform.registerDomain(domainDescriptor.getName(), domainDescriptor);
    }

    @Override
    public <E> List<E> get(Class<E> clazz) {
        if (DocumentedPlatform.class.isAssignableFrom(clazz)) {
            return (List<E>) Lists.newArrayList(documentedPlatform);
        }
        return super.get(clazz);
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE - 1;
    }
}
