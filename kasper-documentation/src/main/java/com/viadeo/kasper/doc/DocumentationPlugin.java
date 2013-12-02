package com.viadeo.kasper.doc;

import com.viadeo.kasper.client.platform.NewPlatform;
import com.viadeo.kasper.client.platform.Plugin;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptor;
import com.viadeo.kasper.doc.element.DocumentedPlatform;
import com.viadeo.kasper.doc.initializer.DefaultDocumentedElementInitializer;


public class DocumentationPlugin implements Plugin {

    private final DocumentedPlatform documentedPlatform;

    public DocumentationPlugin() {
        this.documentedPlatform = new DocumentedPlatform();
    }

    @Override
    public void initialize(NewPlatform platform, DomainDescriptor... domainDescriptors) {
        for (DomainDescriptor domainDescriptor : domainDescriptors) {
            documentedPlatform.registerDomain(domainDescriptor.getName(), domainDescriptor);
        }

        documentedPlatform.accept(new DefaultDocumentedElementInitializer());
    }

    public DocumentedPlatform getDocumentedPlatform() {
        return documentedPlatform;
    }
}
