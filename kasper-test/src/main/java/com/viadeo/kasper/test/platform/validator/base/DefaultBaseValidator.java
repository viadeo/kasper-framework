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
package com.viadeo.kasper.test.platform.validator.base;

import org.axonframework.commandhandling.interceptors.JSR303ViolationException;
import org.axonframework.test.AxonAssertionError;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

import javax.validation.ConstraintViolation;

import static com.viadeo.kasper.test.platform.KasperMatcher.equalTo;
import static com.viadeo.kasper.test.platform.KasperPlatformFixture.RecordingPlatform;

public class DefaultBaseValidator extends BaseValidator
        implements ExceptionValidator<DefaultBaseValidator>, FieldValidator<DefaultBaseValidator> {

    public DefaultBaseValidator(final RecordingPlatform platform, final Object response, final Exception exception) {
        super(platform, response, exception);
    }

    // ------------------------------------------------------------------------

    @Override
    public DefaultBaseValidator expectException(final Class<? extends Throwable> expectedException) {
        return this.expectException(equalTo(expectedException));
    }

    @Override
    public DefaultBaseValidator expectException(final Matcher<?> matcher) {
        final StringDescription description = new StringDescription();
        matcher.describeTo(description);
        if ( ! hasException()) {
            reporter().reportUnexpectedReturnValue(this.response(), description);
        }
        if ( ! matcher.matches(this.exception())) {
            reporter().reportWrongException(this.exception(), description);
        }
        return this;
    }

    @Override
    public DefaultBaseValidator expectValidationErrorOnField(final String field) {

        if ( ( ! hasException() ) || ( ! JSR303ViolationException.class.equals(this.exception().getClass()))) {
            throw new AxonAssertionError(String.format(
                    "The expected validation error on field %s not occurred",
                    field
            ));
        }

        boolean found = false;

        final JSR303ViolationException jsrException = (JSR303ViolationException) this.exception();

        for (final ConstraintViolation violation : jsrException.getViolations()) {
            if (violation.getPropertyPath().toString().contentEquals(field)) {
                found = true;
            }
        }

        if ( ! found) {
            throw new AxonAssertionError(String.format(
                "The expected validation error on field %s not occurred",
                field
            ));
        }

        return this;
    }

    protected void expectReturnValue(final Matcher<?> matcher) {
        final StringDescription description = new StringDescription();
        matcher.describeTo(description);

        if (null != this.exception()) {
            reporter().reportUnexpectedException(this.exception(), description);
        } else if ( ! matcher.matches(this.response())) {
            reporter().reportWrongResult(this.response(), description);
        }
    }

}
