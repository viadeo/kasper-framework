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
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.test.platform.validator.KasperFixtureCommandResultValidator;
import com.viadeo.kasper.test.platform.validator.base.ExceptionValidator;
import com.viadeo.kasper.test.platform.validator.base.FieldValidator;
import org.axonframework.commandhandling.interceptors.JSR303ViolationException;
import org.axonframework.test.AxonAssertionError;
import org.axonframework.test.ResultValidator;
import org.hamcrest.Matcher;

import javax.validation.ConstraintViolation;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.test.platform.KasperMatcher.anySecurityToken;
import static com.viadeo.kasper.test.platform.KasperMatcher.equalTo;
import static org.axonframework.test.matchers.Matchers.*;

public class KasperAggregateResultValidator implements
        ExceptionValidator<KasperAggregateResultValidator>,
        FieldValidator<KasperAggregateResultValidator>,
        KasperFixtureCommandResultValidator {

    private final ResultValidator validator;
    private final JSR303ViolationException validationException;

    // ------------------------------------------------------------------------

    KasperAggregateResultValidator(final ResultValidator validator) {
        this.validator = checkNotNull(validator);
        this.validationException = null;
    }

    KasperAggregateResultValidator(final JSR303ViolationException validationException) {
        this.validationException = validationException;
        this.validator = null;
    }

    // ------------------------------------------------------------------------

    @Override
    public KasperAggregateResultValidator expectSequenceOfEvents(final Event... events) {
        checkValidation();

        final Matcher[] matchers = new Matcher[events.length];

        for (int i = 0 ; i < events.length ; i++) {
            matchers[i] = equalTo(events[i]);
        }

        validator.expectEventsMatching(payloadsMatching(exactSequenceOf(matchers)));

        return this;
    }

    @Override
    public KasperAggregateResultValidator expectExactSequenceOfEvents(final Event... events) {
        checkValidation();

        final Matcher[] matchers = new Matcher[events.length + 1];

        int i;
        for (i = 0 ; i < events.length ; i++) {
            matchers[i] = equalTo(events[i]);
        }

        matchers[i] = andNoMore();

        validator.expectEventsMatching(payloadsMatching(exactSequenceOf(matchers)));

        return this;
    }

    // ------------------------------------------------------------------------

    @Override
    public KasperAggregateResultValidator expectReturnResponse(final CommandResponse commandResponse) {
        checkValidation();
        validator.expectReturnValue(commandResponse);
        return this;
    }

    @Override
    public KasperAggregateResultValidator expectReturnOK() {
        return expectReturnOK(anySecurityToken());
    }

    public KasperAggregateResultValidator expectReturnOK(final String securityToken) {
        checkNotNull(securityToken);
        checkValidation();
        validator.expectReturnValue(
                CommandResponse.ok()
                        .withSecurityToken(securityToken)
        );
        return this;
    }

    @Override
    public KasperAggregateResultValidator expectReturnError(final KasperReason reason) {
        checkValidation();
        validator.expectReturnValue(CommandResponse.error(reason));
        return this;
    }

    @Override
    public KasperAggregateResultValidator expectReturnRefused(final KasperReason reason) {
        checkValidation();
        validator.expectReturnValue(CommandResponse.refused(reason));
        return this;
    }

    @Override
    public KasperAggregateResultValidator expectException(final Class<? extends Throwable> expectedException) {
        checkValidation();
        validator.expectException(expectedException);
        return this;
    }

    @Override
    public KasperAggregateResultValidator expectException(final Matcher<?> matcher) {
        checkValidation();
        validator.expectException(matcher);
        return this;
    }

    @Override
    public KasperAggregateResultValidator expectValidationErrorOnField(final String field) {

        if (null == validationException) {
            throw new AxonAssertionError(String.format(
                    "The expected validation error on field %s not occured",
                    field
            ));
        }

        boolean found = false;

        for (final ConstraintViolation violation : validationException.getViolations()) {
            if (violation.getPropertyPath().toString().contentEquals(field)) {
                found = true;
            }
        }

        if ( ! found) {
            throw new AxonAssertionError(String.format(
                    "The expected validation error on field %s not occured",
                    field
            ));
        }

        return this;
    }

    // ------------------------------------------------------------------------

    private void checkValidation() {
        if (null != validationException) {
            throw new AxonAssertionError("Error on validation : " + validationException.toString());
        }
    }

}
