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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import com.viadeo.kasper.doc.initializer.DocumentedElementVisitor;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractElement implements WithType {

    private final LightDocumentedElement lightDocumentedElement;
    private final DocumentedElementType type;
    private final Class referenceClass;
    private final String name;
    private final boolean deprecated;

    private String label;
    private String description;

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public AbstractElement(final DocumentedElementType type, final Class referenceClass) {
        this.type = checkNotNull(type);
        this.referenceClass = checkNotNull(referenceClass);
        this.deprecated = (null != referenceClass.getAnnotation(Deprecated.class));
        this.name = referenceClass.getSimpleName();
        this.label = referenceClass.getSimpleName();
        this.lightDocumentedElement = new LightDocumentedElement(this);
    }

    // ------------------------------------------------------------------------

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getType() {
        return type.getType();
    }

    public String getTypePlural() {
        return type.getPluralType();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLabel() {
        return label;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public abstract String getURL();

    @JsonIgnore
    public LightDocumentedElement getLightDocumentedElement() {
        return lightDocumentedElement;
    }

    @JsonIgnore
    public Class getReferenceClass() {
        return referenceClass;
    }

    public abstract void accept(final DocumentedElementVisitor visitor);

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", name)
                .add("label", label)
                .add("referenceClass", referenceClass)
                .add("type", type)
                .add("description", description)
                .add("deprecated", deprecated)
                .toString();
    }
}
