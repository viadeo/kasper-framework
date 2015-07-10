// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.element;

import com.google.common.collect.Lists;
import com.viadeo.kasper.api.domain.annotation.XKasperPublic;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.api.domain.annotation.XKasperAuthz.*;

public abstract class AbstractDomainElement extends AbstractElement {

    private final boolean publicAccess;
    protected DocumentedDomain domain;
    private DocumentedAuthorization authorization = null;

    // ------------------------------------------------------------------------

    public AbstractDomainElement(final DocumentedDomain domain, final DocumentedElementType type, final Class referenceClass) {
        super(checkNotNull(type), checkNotNull(referenceClass));
        this.domain = checkNotNull(domain);

        this.publicAccess = referenceClass.getAnnotation(XKasperPublic.class) != null;

        // @XKasperRequireRoles
        if (null != referenceClass.getAnnotation(RequiresRoles.class)) {
            if(null == this.authorization){
                this.authorization = new DocumentedAuthorization();
            }

            final RequiresRoles requireRoles = (RequiresRoles)
                    referenceClass.getAnnotation(RequiresRoles.class);

            AuthorizationElement authorizationElement = new AuthorizationElement();
            if (null != requireRoles.value()) {
                authorizationElement.setValue(Lists.newArrayList(requireRoles.value()));
            }
            if(null != requireRoles.manager() && !AuthorizationManager.class.equals(requireRoles.manager())){
                authorizationElement.setManager(requireRoles.manager().getSimpleName());
            } else {
                authorizationElement.setManager("default");
            }

            if(null != requireRoles.combinesWith()){
                authorizationElement.setCombinesWith(requireRoles.combinesWith());
            }
            this.authorization.setRoles(authorizationElement);
        }

        // @XKasperRequirePermissions
        if (null != referenceClass.getAnnotation(RequiresPermissions.class)) {
            if(null == this.authorization){
                this.authorization = new DocumentedAuthorization();
            }

            final RequiresPermissions requirePermissions = (RequiresPermissions)
                    referenceClass.getAnnotation(RequiresPermissions.class);

            AuthorizationElement authorizationElement = new AuthorizationElement();
            if (null != requirePermissions.value()) {
                authorizationElement.setValue(Lists.newArrayList(requirePermissions.value()));
            }
            if(null != requirePermissions.manager() && !AuthorizationManager.class.equals(requirePermissions.manager())){
                authorizationElement.setManager(requirePermissions.manager().getSimpleName());
            } else {
                authorizationElement.setManager("default");
            }
            if(null != requirePermissions.combinesWith()){
                authorizationElement.setCombinesWith(requirePermissions.combinesWith());
            }
            this.authorization.setPermissions(authorizationElement);
        }

    }

    // ------------------------------------------------------------------------

    public LightDocumentedElement<DocumentedDomain> getDomain() {
        return domain.getLightDocumentedElement();
    }

    public boolean isPublicAccess() {
        return publicAccess;
    }

    public DocumentedAuthorization getAuthorization() {
        return authorization;
    }

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
