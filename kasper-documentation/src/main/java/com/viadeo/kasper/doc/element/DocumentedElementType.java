// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.element;

import com.google.common.base.Optional;

public enum DocumentedElementType {

    DOMAIN("domain", "domains"),
    COMMAND("command", "commands"),
    COMMAND_HANDLER("commandHandler", "commandHandlers"),
    QUERY("query", "queries"),
    QUERY_RESULT("queryResult", "queryResults"),
    QUERY_HANDLER("queryHandler", "queryHandlers"),
    EVENT("event", "events"),
    DECLARED_EVENT("declaredEvent", "declaredEvents"),
    REFERENCED_EVENT("referencedEvent", "referencedEvents"),
    EVENT_LISTENER("eventListener", "eventListeners"),
    REPOSITORY("repository", "repositories"),
    CONCEPT("concept", "concepts"),
    RELATION("relation", "relations"),
    SAGA("saga", "sagas");

    protected final String type;
    protected final String pluralType;

    // ------------------------------------------------------------------------

    private DocumentedElementType(final String type, final String pluralType) {
        this.type = type;
        this.pluralType = pluralType;
    }

    // ------------------------------------------------------------------------

    public String getType() {
        return type;
    }

    public String getPluralType() {
        return pluralType;
    }

    public static Optional<DocumentedElementType> of(final String type) {
        for (final DocumentedElementType documentedElementType : DocumentedElementType.values()) {
            if (documentedElementType.type.equals(type) || documentedElementType.pluralType.equals(type)) {
                return Optional.of(documentedElementType);
            }
        }
        return Optional.absent();
    }

}
