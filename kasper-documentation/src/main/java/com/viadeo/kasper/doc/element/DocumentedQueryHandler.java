// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.element;

import com.viadeo.kasper.client.platform.domain.descriptor.QueryHandlerDescriptor;
import com.viadeo.kasper.doc.initializer.DocumentedElementVisitor;
import com.viadeo.kasper.doc.nodes.DocumentedBean;
import com.viadeo.kasper.doc.nodes.DocumentedQueryResponse;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class DocumentedQueryHandler extends AbstractDomainElement {

    private final DocumentedQuery documentedQuery;
    private final DocumentedQueryResult documentedQueryResult;

    public static class DocumentedQuery extends AbstractPropertyDomainElement {

        private final DocumentedQueryHandler queryHandler;
        private final DocumentedBean response;

        public DocumentedQuery(final DocumentedDomain domain,
                               final DocumentedQueryHandler queryHandler,
                               final Class queryClass) {
            super(domain, DocumentedElementType.QUERY, queryClass);
            this.queryHandler = queryHandler;
            this.response = new DocumentedQueryResponse(queryHandler.documentedQueryResult.getReferenceClass());
        }

        public LightDocumentedElement<DocumentedQueryHandler> getQueryHandler() {
            return queryHandler.getLightDocumentedElement();
        }

        public DocumentedBean getResponse() {
            return response;
        }

        @Override
        public LightDocumentedElement<DocumentedQuery> getLightDocumentedElement() {
            return new LightDocumentedElement<>(this);
        }

        @Override
        public void accept(final DocumentedElementVisitor visitor) {
            visitor.visit(this);
        }
    }

    public static class DocumentedQueryResult extends AbstractPropertyDomainElement {

        private static final LinkedMultiValueMap<Class, LightDocumentedElement> HANDLERS_BY_QUERY_RESULTS = new LinkedMultiValueMap<>();

        private DocumentedQueryResult element;

        public DocumentedQueryResult(final DocumentedDomain domain,
                                     final DocumentedQueryHandler queryHandler,
                                     final Class queryResultClass) {
            super(domain, DocumentedElementType.QUERY_RESULT, queryResultClass);

            if(null != queryHandler) {
                HANDLERS_BY_QUERY_RESULTS.add(queryResultClass, queryHandler.getLightDocumentedElement());
            }
        }

        public List<LightDocumentedElement> getQueryHandlers() {
            final List<LightDocumentedElement> queryHandlers = HANDLERS_BY_QUERY_RESULTS.get(getReferenceClass());
            return queryHandlers != null ? queryHandlers : null;
        }

        @Override
        public LightDocumentedElement<DocumentedQueryResult> getLightDocumentedElement() {
            return new LightDocumentedElement<>(this);
        }

        @Override
        public void accept(DocumentedElementVisitor visitor) {
            visitor.visit(this);
        }

        public LightDocumentedElement<DocumentedQueryResult> getElement() {
            if(null == element) {
                return null;
            }
            return element.getLightDocumentedElement();
        }

        public void setElement(DocumentedQueryResult element) {
            this.element = element;
        }
    }

    // ------------------------------------------------------------------------

    public DocumentedQueryHandler(final DocumentedDomain documentedDomain,
                                  final QueryHandlerDescriptor queryHandlerDescriptor) {
        super(
                checkNotNull(documentedDomain),
                DocumentedElementType.QUERY_HANDLER,
                queryHandlerDescriptor.getReferenceClass()
        );

        this.documentedQueryResult = new DocumentedQueryResult(
                documentedDomain,
                this,
                queryHandlerDescriptor.getQueryResultClass()
        );

        this.documentedQuery = new DocumentedQuery(
                documentedDomain,
                this,
                queryHandlerDescriptor.getQueryClass()
        );
    }

    // ------------------------------------------------------------------------

    @Override
    public void accept(final DocumentedElementVisitor visitor) {
        documentedQuery.accept(visitor);
        documentedQueryResult.accept(visitor);
        visitor.visit(this);
    }

    @Override
    public LightDocumentedElement<DocumentedQueryHandler> getLightDocumentedElement() {
        return new LightDocumentedElement<DocumentedQueryHandler>(this) {

            @SuppressWarnings("unused")
            public String getQueryName() {
                return documentedElement.getQuery().getName();
            }

            @SuppressWarnings("unused")
            public String getQueryResultName() {
                return documentedElement.getQueryResult().getName();
            }
        };
    }

    // ------------------------------------------------------------------------

    public LightDocumentedElement<DocumentedQuery> getQuery() {
        return documentedQuery.getLightDocumentedElement();
    }

    public LightDocumentedElement<DocumentedQueryResult> getQueryResult() {
        return documentedQueryResult.getLightDocumentedElement();
    }

}
