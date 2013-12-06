package com.viadeo.kasper.doc.element;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class LightDocumentedElement<E extends AbstractElement> implements WithType {

    protected final E documentedElement;

    public LightDocumentedElement(E documentedElement) {
        this.documentedElement = documentedElement;
    }

    public String getType() {
        return documentedElement.getType();
    }

    public String getTypePlural() {
        return documentedElement.getTypePlural();
    }

    public String getName() {
        return documentedElement.getName();
    }

    public String getLabel(){
        return documentedElement.getLabel();
    }

    public String getDescription() {
        return documentedElement.getDescription();
    }

    public String getURL() {
        return documentedElement.getURL();
    }

    @JsonIgnore
    public E getFullDocumentedElement() {
        return documentedElement;
    }

    @JsonIgnore
    public Class getReferenceClass() {
        return documentedElement.getReferenceClass();
    }
}
