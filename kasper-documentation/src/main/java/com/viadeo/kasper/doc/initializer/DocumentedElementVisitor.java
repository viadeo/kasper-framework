package com.viadeo.kasper.doc.initializer;

import com.viadeo.kasper.doc.element.*;

import static com.viadeo.kasper.doc.element.DocumentedCommandHandler.DocumentedCommand;
import static com.viadeo.kasper.doc.element.DocumentedEventListener.DocumentedEvent;
import static com.viadeo.kasper.doc.element.DocumentedQueryHandler.*;
import static com.viadeo.kasper.doc.element.DocumentedRepository.*;

public interface DocumentedElementVisitor {
    void visit(DocumentedDomain domain);

    void visit(DocumentedCommand command);
    void visit(DocumentedCommandHandler commandHandler);

    void visit(DocumentedQuery query);
    void visit(DocumentedQueryResult queryResult);
    void visit(DocumentedQueryHandler queryHandler);

    void visit(DocumentedEvent event);
    void visit(DocumentedEventListener eventListener);

    void visit(DocumentedRepository repository);
    void visit(DocumentedConcept concept);
    void visit(DocumentedRelation relation);
}
