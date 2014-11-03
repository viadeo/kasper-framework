// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.viadeo.kasper.annotation.XKasperField;
import com.viadeo.kasper.cqrs.query.CollectionQueryResult;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class DocumentedQueryResponse extends DocumentedBean {

    private static final long serialVersionUID = 5209142319212434814L;

    // ------------------------------------------------------------------------

    private static class QueryResultExtractor extends BaseExtractor {

        private final Class queryResultClass;

        public QueryResultExtractor(final Extractor next, final Class queryResultClass) {
            super(next);
            this.queryResultClass = queryResultClass;
        }

        @Override
        public boolean accept(final Field field) {
            return QueryResult.class.isAssignableFrom(field.getType());
        }

        @Override
        public Optional<DocumentedProperty> doExtract(final Field field, final Class clazz, final int level) {
            final XKasperField annotation = field.getAnnotation(XKasperField.class);

            return Optional.of(
                new DocumentedProperty(
                    field.getName(),
                    annotation == null ? "" : annotation.description(),
                    queryResultClass.getSimpleName(),
                    null,
                    false,
                    false,
                    true,
                    Sets.<DocumentedConstraint>newHashSet(),
                    field.getType(),
                    level
                )
            );
        }
    }

    // ------------------------------------------------------------------------

    private static class CollectionQueryResultExtractor extends BaseExtractor {

        private final Class queryResultClass;

        public CollectionQueryResultExtractor(final Extractor next, final Class queryResultClass) {
            super(next);
            this.queryResultClass = queryResultClass;
        }

        @Override
        public boolean accept(final Field field) {
            return QueryResult.class.isAssignableFrom(field.getType())
                    && CollectionQueryResult.class.isAssignableFrom(queryResultClass);
        }

        @Override
        public Optional<DocumentedProperty> doExtract(final Field field, final Class clazz, final int level) {
            final XKasperField annotation = field.getAnnotation(XKasperField.class);
            final ParameterizedType genericSuperclass = (ParameterizedType) queryResultClass.getGenericSuperclass();
            final Type[] parameters = genericSuperclass.getActualTypeArguments();

            final DocumentedProperty documentedProperty = new DocumentedProperty(
                    field.getName(),
                    annotation == null ? "" : annotation.description(),
                    queryResultClass.getSimpleName(),
                    null, false, false, true,
                    Sets.<DocumentedConstraint>newHashSet(),
                    field.getType(),
                    level
            );
            documentedProperty.setElemType(((Class) parameters[0]).getSimpleName());

            return Optional.of(documentedProperty);
        }
    }

    // ------------------------------------------------------------------------

    public DocumentedQueryResponse(Class queryResultClass) {
        this(queryResultClass, 0);
    }

    public DocumentedQueryResponse(Class queryResultClass, int level) {
        super(QueryResponse.class,
            new NoThisDollarInFieldName(new NoTransient(new NoConstant(
                new CollectionExtractor(new MapExtractor(new LinkedConceptExtractor(new EnumExtractor(
                    new CollectionQueryResultExtractor(new QueryResultExtractor(new FieldExtractor(), queryResultClass), queryResultClass)
                ))))
            ))),
            level
        );
    }

}
