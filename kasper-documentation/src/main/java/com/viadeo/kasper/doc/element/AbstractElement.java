// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.element;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.viadeo.kasper.doc.initializer.DocumentedElementVisitor;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractElement implements WithType {

    private final DocumentedElementType type;
    private final Class referenceClass;
    private final String name;

    private String label;
    private String description;

    // ------------------------------------------------------------------------

    public AbstractElement(final DocumentedElementType type, final Class referenceClass) {
        this.type = checkNotNull(type);
        this.referenceClass = checkNotNull(referenceClass);
        this.name = referenceClass.getSimpleName();
        this.label = referenceClass.getSimpleName();
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

    public abstract String getURL();

    @JsonIgnore
    public abstract LightDocumentedElement getLightDocumentedElement();

    @JsonIgnore
    public Class getReferenceClass() {
        return referenceClass;
    }

    public abstract void accept(final DocumentedElementVisitor visitor);

}
