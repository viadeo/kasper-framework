// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.element;

import com.google.common.collect.Lists;
import com.viadeo.kasper.core.component.annotation.XKasperPublic;
import com.viadeo.kasper.core.interceptor.authorization.AuthorizationManager;

import java.lang.annotation.Annotation;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.component.annotation.XKasperAuthz.RequiresPermissions;
import static com.viadeo.kasper.core.component.annotation.XKasperAuthz.RequiresRoles;

public abstract class AbstractDomainElement extends AbstractElement {

    private final boolean publicAccess;
    protected DocumentedDomain domain;
    private DocumentedAuthorization authorization = null;

    // ------------------------------------------------------------------------

    public AbstractDomainElement(final DocumentedDomain domain, final DocumentedElementType type, final Class referenceClass) {
        super(checkNotNull(type), checkNotNull(referenceClass));
        this.domain = checkNotNull(domain);

        @SuppressWarnings("unchecked")
        Annotation annotation = referenceClass.getAnnotation(XKasperPublic.class);
        this.publicAccess = annotation != null;

        @SuppressWarnings("unchecked")
        final RequiresRoles requireRoles = (RequiresRoles)
                referenceClass.getAnnotation(RequiresRoles.class);

        if (null != requireRoles) {
            if(null == this.authorization){
                this.authorization = new DocumentedAuthorization();
            }

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

        @SuppressWarnings("unchecked")
        final RequiresPermissions requirePermissions = (RequiresPermissions)
                referenceClass.getAnnotation(RequiresPermissions.class);

        if (null != requirePermissions) {
            if(null == this.authorization){
                this.authorization = new DocumentedAuthorization();
            }

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
