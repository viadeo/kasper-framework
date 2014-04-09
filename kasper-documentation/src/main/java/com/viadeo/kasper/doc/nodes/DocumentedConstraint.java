// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import static com.google.common.base.Preconditions.checkNotNull;

public class DocumentedConstraint {

    private final String type;
    private final String message;

    // ------------------------------------------------------------------------

    public DocumentedConstraint(final String type, final String message) {
        this.type = checkNotNull(type);
        this.message = checkNotNull(message);
    }

    // ------------------------------------------------------------------------

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }

}
