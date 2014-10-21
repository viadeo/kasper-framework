// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.element;

import com.google.common.collect.Lists;
import com.viadeo.kasper.security.annotation.XKasperPublic;
import com.viadeo.kasper.security.annotation.XKasperRequirePermissions;
import com.viadeo.kasper.security.annotation.XKasperRequireRoles;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractDomainElement extends AbstractElement {

    private final DocumentedDomain domain;
    private final boolean publicAccess;
    private final List<String> roles;
    private final List<String> permissions;

    // ------------------------------------------------------------------------

    public AbstractDomainElement(final DocumentedDomain domain, final DocumentedElementType type, final Class referenceClass) {
        super(checkNotNull(type), checkNotNull(referenceClass));
        this.domain = checkNotNull(domain);

        this.publicAccess = referenceClass.getAnnotation(XKasperPublic.class) != null;

        // @XKasperRequireRoles
        if (null != referenceClass.getAnnotation(XKasperRequireRoles.class)) {
            final XKasperRequireRoles requireRoles = (XKasperRequireRoles)
                    referenceClass.getAnnotation(XKasperRequireRoles.class);

            if (null != requireRoles.value()) {
                this.roles = Lists.newArrayList(requireRoles.value());
            } else {
                this.roles = Lists.newArrayList();
            }
        } else {
            this.roles = Lists.newArrayList();
        }

        // @XKasperRequirePermissions
        if (null != referenceClass.getAnnotation(XKasperRequirePermissions.class)) {
            final XKasperRequirePermissions requirePermissions = (XKasperRequirePermissions)
                    referenceClass.getAnnotation(XKasperRequirePermissions.class);

            if (null != requirePermissions.value()) {
                this.permissions = Lists.newArrayList(requirePermissions.value());
            } else {
                this.permissions = Lists.newArrayList();
            }
        } else {
            this.permissions = Lists.newArrayList();
        }

    }

    // ------------------------------------------------------------------------

    public LightDocumentedElement<DocumentedDomain> getDomain() {
        return domain.getLightDocumentedElement();
    }

    public boolean isPublicAccess() {
        return publicAccess;
    }

    public List<String> getRoles() { return roles; }

    public List<String> getPermissions() { return permissions; }

    @Override
    public String getURL() {
        return String.format(
                "/%s/%s/%s/%s",
                domain.getType(),
                domain.getLabel(),
                getType(),
                getName()
        );
    }

}
