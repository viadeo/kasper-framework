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

    void validate(NotNull annotation, DocumentedProperty property);
    void validate(Null annotation, DocumentedProperty property);
    void validate(Min annotation, DocumentedProperty property);
    void validate(Max annotation, DocumentedProperty property);
    void validate(Size annotation, DocumentedProperty property);
    void validate(Past annotation, DocumentedProperty property);
    void validate(Future annotation, DocumentedProperty property);
    void validate(Pattern annotation, DocumentedProperty property);
    void validate(AssertFalse annotation, DocumentedProperty property);
    void validate(AssertTrue annotation, DocumentedProperty property);
    void validate(DecimalMax annotation, DocumentedProperty property);
    void validate(DecimalMin annotation, DocumentedProperty property);
    void validate(Digits annotation, DocumentedProperty property);
    void validate(Annotation annotation, DocumentedProperty property);

}
