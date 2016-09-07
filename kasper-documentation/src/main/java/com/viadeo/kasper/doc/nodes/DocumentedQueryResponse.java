// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.viadeo.kasper.api.annotation.XKasperField;
import com.viadeo.kasper.api.component.query.CollectionQueryResult;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;

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
                    null,
                    false,
                    false,
                    false,
                    true,
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
