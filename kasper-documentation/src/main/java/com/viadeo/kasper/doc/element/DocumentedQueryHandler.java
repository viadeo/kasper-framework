package com.viadeo.kasper.doc.element;

import com.viadeo.kasper.client.platform.domain.descriptor.QueryHandlerDescriptor;
import com.viadeo.kasper.doc.initializer.DocumentedElementVisitor;

public class DocumentedQueryHandler extends AbstractDomainElement {

    private final DocumentedQuery documentedQuery;
    private final DocumentedQueryResult documentedQueryResult;

    public DocumentedQueryHandler(DocumentedDomain documentedDomain, QueryHandlerDescriptor queryHandlerDescriptor) {
        super(documentedDomain, DocumentedElementType.QUERY_HANDLER, queryHandlerDescriptor.getReferenceClass());
        this.documentedQuery = new DocumentedQuery(documentedDomain, this, queryHandlerDescriptor.getQueryClass());
        this.documentedQueryResult = new DocumentedQueryResult(documentedDomain, this, queryHandlerDescriptor.getQueryResultClass());
    }

    @Override
    public void accept(DocumentedElementVisitor visitor) {
        documentedQuery.accept(visitor);
        documentedQueryResult.accept(visitor);
        visitor.visit(this);
    }

    public LightDocumentedElement<DocumentedQuery> getQuery() {
        return documentedQuery.getLightDocumentedElement();
    }

    public LightDocumentedElement<DocumentedQueryResult> getQueryResult() {
        return documentedQueryResult.getLightDocumentedElement();
    }

    @Override
    public LightDocumentedElement<DocumentedQueryHandler> getLightDocumentedElement() {
        return new LightDocumentedElement<DocumentedQueryHandler>(this) {

            public String getQueryName() {
                return documentedElement.getQuery().getName();
            }

            public String getQueryResultName() {
                return documentedElement.getQueryResult().getName();
            }
        };
    }

    public static class DocumentedQuery extends AbstractPropertyDomainElement {

        private final DocumentedQueryHandler queryHandler;

        public DocumentedQuery(DocumentedDomain domain, DocumentedQueryHandler queryHandler, Class queryClass) {
            super(domain, DocumentedElementType.QUERY, queryClass);
            this.queryHandler = queryHandler;
        }

        public LightDocumentedElement<DocumentedQueryHandler> getQueryHandler() {
            return queryHandler.getLightDocumentedElement();
        }

        @Override
        public LightDocumentedElement<DocumentedQuery> getLightDocumentedElement() {
            return new LightDocumentedElement<>(this);
        }

        @Override
        public void accept(DocumentedElementVisitor visitor) {
            visitor.visit(this);
        }
    }

    public static class DocumentedQueryResult extends AbstractPropertyDomainElement {

        private final DocumentedQueryHandler queryHandler;

        public DocumentedQueryResult(DocumentedDomain domain, DocumentedQueryHandler queryHandler, Class queryResultClass) {
            super(domain, DocumentedElementType.QUERY_RESULT, queryResultClass);
            this.queryHandler = queryHandler;
        }

        public LightDocumentedElement<DocumentedQueryHandler> getQueryHandler() {
            return queryHandler.getLightDocumentedElement();
        }

        @Override
        public LightDocumentedElement<DocumentedQueryResult> getLightDocumentedElement() {
            return new LightDocumentedElement<>(this);
        }

        @Override
        public void accept(DocumentedElementVisitor visitor) {
            visitor.visit(this);
        }
    }

}
