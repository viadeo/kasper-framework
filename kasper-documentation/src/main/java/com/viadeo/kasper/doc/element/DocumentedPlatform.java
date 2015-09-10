// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.element;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.viadeo.kasper.doc.initializer.DocumentedElementVisitor;
import com.viadeo.kasper.platform.bundle.descriptor.DomainDescriptor;

import java.util.List;
import java.util.Map;

public class DocumentedPlatform {

    private final Map<String, DocumentedDomain> documentedDomainByDomainName;
    private final Map<Class, DocumentedDomain> documentedDomainByDomainClass;

    // ------------------------------------------------------------------------

    public DocumentedPlatform() {
        this.documentedDomainByDomainName = Maps.newHashMap();
        this.documentedDomainByDomainClass = Maps.newHashMap();
    }

    // ------------------------------------------------------------------------

    public void registerDomain(final String domainName, final DomainDescriptor descriptor) {
        final DocumentedDomain documentedDomain = new DocumentedDomain(descriptor);
        documentedDomainByDomainName.put(domainName, documentedDomain);
        documentedDomainByDomainClass.put(documentedDomain.getReferenceClass(), documentedDomain);
    }

    public void accept(final DocumentedElementVisitor visitor) {
        for (final DocumentedDomain documentedDomain : documentedDomainByDomainName.values()) {
            documentedDomain.accept(visitor);
        }
    }

    public List<DocumentedDomain> getDomains() {
        return Lists.newArrayList(documentedDomainByDomainName.values());
    }

    public Optional<DocumentedDomain> getDomain(final String domainName) {
        return Optional.fromNullable(documentedDomainByDomainName.get(domainName));
    }

    public Optional<DocumentedDomain> getDomain(final Class domainClass) {
        return Optional.fromNullable(documentedDomainByDomainClass.get(domainClass));
    }

}
