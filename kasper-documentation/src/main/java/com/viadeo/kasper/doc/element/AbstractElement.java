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
