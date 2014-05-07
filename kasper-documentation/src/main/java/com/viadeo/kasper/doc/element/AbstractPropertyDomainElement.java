// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.element;

import com.google.common.collect.Lists;
import com.viadeo.kasper.annotation.XKasperAlias;
import com.viadeo.kasper.doc.nodes.DocumentedBean;
import com.viadeo.kasper.security.annotation.XKasperPublic;
import com.viadeo.kasper.security.annotation.XKasperRequirePermissions;
import com.viadeo.kasper.security.annotation.XKasperRequireRoles;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractPropertyDomainElement extends AbstractDomainElement {

    private final DocumentedBean properties;
    private final boolean publicAccess;
    private final List<String> aliases;
    private final List<String> roles;
    private final List<String> permissions;

    // ------------------------------------------------------------------------

    public AbstractPropertyDomainElement(final DocumentedDomain domain,
                                         final DocumentedElementType type,
                                         final Class referenceClass) {
        super(checkNotNull(domain), checkNotNull(type), checkNotNull(referenceClass));

        this.properties = new DocumentedBean(referenceClass);
        this.publicAccess = referenceClass.getAnnotation(XKasperPublic.class) != null;


        if (referenceClass.getAnnotation(XKasperRequireRoles.class) != null) {
            final XKasperRequireRoles requireRoles = (XKasperRequireRoles) referenceClass.getAnnotation(XKasperRequireRoles.class);
            if(requireRoles.value() != null){
                this.roles = Lists.newArrayList(requireRoles.value());
            }else{
                this.roles = Lists.newArrayList();
            }
        } else {
            this.roles = Lists.newArrayList();
        }

        if (referenceClass.getAnnotation(XKasperRequirePermissions.class) != null) {
            final XKasperRequirePermissions requirePermissions = (XKasperRequirePermissions) referenceClass.getAnnotation(XKasperRequirePermissions.class);
            if(requirePermissions.value() != null){
                this.permissions =  Lists.newArrayList(requirePermissions.value());
            }else{
                this.permissions = Lists.newArrayList();
            }
        } else {
            this.permissions = Lists.newArrayList();
        }

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

    public boolean isPublicAccess() {
        return publicAccess;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}
