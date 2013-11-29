package com.viadeo.kasper.doc;

import com.viadeo.kasper.client.platform.Plugin;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptor;
import com.viadeo.kasper.client.platform.domain.descriptor.PlatformDescriptor;
import com.viadeo.kasper.doc.element.DocumentedPlatform;
import com.viadeo.kasper.doc.initializer.DefaultDocumentedElementInitializer;

import java.util.Map;


public class DocumentationPlugin implements Plugin {

    private final DocumentedPlatform documentedPlatform;

    public DocumentationPlugin() {
        this.documentedPlatform = new DocumentedPlatform();
    }

    @Override
    public void initialize(PlatformDescriptor platformDescriptor) {
        for (Map.Entry<String, DomainDescriptor> domainDescriptor : platformDescriptor.getDomainDescriptorByName().entrySet()) {
            documentedPlatform.registerDomain(domainDescriptor.getKey(), domainDescriptor.getValue());
        }
        documentedPlatform.accept(new DefaultDocumentedElementInitializer());
    }

    public DocumentedPlatform getDocumentedPlatform() {
        return documentedPlatform;
    }
}
