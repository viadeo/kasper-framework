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
