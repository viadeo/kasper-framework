// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.element;

import com.google.common.collect.Lists;
import com.viadeo.kasper.api.annotation.XKasperAlias;
import com.viadeo.kasper.doc.nodes.DocumentedBean;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractPropertyDomainElement extends AbstractDomainElement {

    private final DocumentedBean properties;
    private final List<String> aliases;

    // ------------------------------------------------------------------------

    public AbstractPropertyDomainElement(final DocumentedDomain domain,
                                         final DocumentedElementType type,
                                         final Class referenceClass) {
        super(checkNotNull(domain), checkNotNull(type), checkNotNull(referenceClass));

        this.properties = new DocumentedBean(referenceClass);

        final XKasperAlias annotation = (XKasperAlias) referenceClass.getAnnotation(XKasperAlias.class);
        if (null != annotation) {
            this.aliases = Lists.newArrayList(annotation.values());
        } else {
            this.aliases = Lists.newArrayList();
        }
    }

    // ------------------------------------------------------------------------

    public DocumentedBean getProperties() {
        return properties;
    }

    public List<String> getAliases() {
        return aliases;
    }

}
