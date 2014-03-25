// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes.validation;

import com.viadeo.kasper.doc.nodes.DocumentedProperty;

import javax.validation.constraints.*;
import java.lang.annotation.Annotation;

public interface PropertyValidator {

    void validate(final NotNull annotation, final DocumentedProperty property);
    void validate(final Null annotation, final DocumentedProperty property);
    void validate(final Min annotation, final DocumentedProperty property);
    void validate(final Max annotation, final DocumentedProperty property);
    void validate(final Size annotation, final DocumentedProperty property);
    void validate(final Past annotation, final DocumentedProperty property);
    void validate(final Future annotation, final DocumentedProperty property);
    void validate(final Pattern annotation, final DocumentedProperty property);
    void validate(final AssertFalse annotation, final DocumentedProperty property);
    void validate(final AssertTrue annotation, final DocumentedProperty property);
    void validate(final DecimalMax annotation, final DocumentedProperty property);
    void validate(final DecimalMin annotation, final DocumentedProperty property);
    void validate(final Digits annotation, final DocumentedProperty property);
    void validate(Annotation annotation, DocumentedProperty property);
}
