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
package com.viadeo.kasper.core.component.query.interceptor;

import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.core.component.query.interceptor.validation.QueryValidationInterceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import org.axonframework.commandhandling.interceptors.JSR303ViolationException;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Locale;

import static org.junit.Assert.fail;

public class ValidationInterceptorTest {

    public static class QueryToValidate implements Query {
        private static final long serialVersionUID = -2017104008425866649L;

        @NotNull
        @Size(min = 36, max = 36)
        private String field;

        QueryToValidate() { }

        QueryToValidate(final String value) {
            field = value;
        }
    }

    // ------------------------------------------------------------------------

    @Test
    public void testNotNullValidation() throws Exception {
        // Given
        Locale.setDefault(Locale.US);
        final QueryValidationInterceptor<QueryToValidate, QueryResult> actor = new QueryValidationInterceptor<>(Validation.buildDefaultValidatorFactory());

        // When
        try {
            actor.process(
                    new QueryToValidate(),
                    Contexts.empty(),
                    InterceptorChain.<QueryToValidate, QueryResponse<QueryResult>>tail()
            );
            fail();
        } catch (final JSR303ViolationException e) {
            // Then should raise exception
        }
    }

    @Test
    public void testSizeValidation() throws Exception {
        // Given
        Locale.setDefault(Locale.US);
        final QueryValidationInterceptor<QueryToValidate, QueryResult> actor = new QueryValidationInterceptor<>(Validation.buildDefaultValidatorFactory());

        // When
        try {
            actor.process(
                new QueryToValidate("fr"),
                    Contexts.empty(),
                InterceptorChain.<QueryToValidate, QueryResponse<QueryResult>>tail()
            );
            fail();
        } catch (final JSR303ViolationException e) {
            // Then should raise exception
        }
    }

}
