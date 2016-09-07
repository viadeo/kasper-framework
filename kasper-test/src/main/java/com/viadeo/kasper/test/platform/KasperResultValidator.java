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

import org.axonframework.test.ResultValidator;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.test.platform.KasperMatcher.equalTo;

public class KasperResultValidator implements ResultValidator {

    private final ResultValidator delegate;

    public KasperResultValidator(final ResultValidator delegate) {
        this.delegate = checkNotNull(delegate);
    }

    @Override
    public ResultValidator expectEvents(final Object... expectedEvents) {
        return delegate.expectEvents(expectedEvents);
    }

    @Override
    public ResultValidator expectEventsMatching(final Matcher<? extends Iterable<?>> matcher) {
        return delegate.expectEventsMatching(matcher);
    }

    @Override
    public ResultValidator expectPublishedEvents(final Object... expectedEvents) {
        return delegate.expectPublishedEvents(expectedEvents);
    }

    @Override
    public ResultValidator expectPublishedEventsMatching(final Matcher<? extends Iterable<?>> matcher) {
        return delegate.expectPublishedEventsMatching(matcher);
    }

    @Override
    public ResultValidator expectStoredEvents(final Object... expectedEvents) {
        return delegate.expectStoredEvents(expectedEvents);
    }

    @Override
    public ResultValidator expectStoredEventsMatching(final Matcher<? extends Iterable<?>> matcher) {
        return delegate.expectStoredEventsMatching(matcher);
    }

    @Override
    public ResultValidator expectReturnValue(final Object expectedReturnValue) {
        if (null == expectedReturnValue) {
            return expectReturnValue(CoreMatchers.nullValue());
        }
        return expectReturnValue(equalTo(expectedReturnValue));
    }

    @Override
    public ResultValidator expectReturnValue(final Matcher<?> matcher) {
        return delegate.expectReturnValue(matcher);
    }

    @Override
    public ResultValidator expectException(final Class<? extends Throwable> expectedException) {
        return delegate.expectException(expectedException);
    }

    @Override
    public ResultValidator expectException(final Matcher<?> matcher) {
        return delegate.expectException(matcher);
    }

    @Override
    public ResultValidator expectVoidReturnType() {
        return delegate.expectVoidReturnType();
    }
}
