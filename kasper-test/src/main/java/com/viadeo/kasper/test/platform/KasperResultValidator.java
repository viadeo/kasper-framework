// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.tools.KasperMatcher;
import org.axonframework.test.ResultValidator;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

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
    public ResultValidator expectEventsMatching(final Matcher<List<?>> matcher) {
        return delegate.expectEventsMatching(matcher);
    }

    @Override
    public ResultValidator expectPublishedEvents(final Object... expectedEvents) {
        return delegate.expectPublishedEvents(expectedEvents);
    }

    @Override
    public ResultValidator expectPublishedEventsMatching(final Matcher<List<?>> matcher) {
        return delegate.expectPublishedEventsMatching(matcher);
    }

    @Override
    public ResultValidator expectStoredEvents(final Object... expectedEvents) {
        return delegate.expectStoredEvents(expectedEvents);
    }

    @Override
    public ResultValidator expectStoredEventsMatching(final Matcher<List<?>> matcher) {
        return delegate.expectStoredEventsMatching(matcher);
    }

    @Override
    public ResultValidator expectReturnValue(final Object expectedReturnValue) {
        if (expectedReturnValue == null) {
            return expectReturnValue(CoreMatchers.nullValue());
        }
        return expectReturnValue(KasperMatcher.equalTo(expectedReturnValue));
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
