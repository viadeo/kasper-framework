// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.matchers;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.cqrs.query.CollectionQueryResult;
import com.viadeo.kasper.cqrs.query.IndexedEntity;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.impl.DefaultKasperId;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.mongodb.util.MyAsserts.assertFalse;
import static com.mongodb.util.MyAsserts.assertTrue;
import static com.viadeo.kasper.test.matchers.KasperMatcher.anyDate;
import static com.viadeo.kasper.test.matchers.KasperMatcher.equalTo;
import static org.junit.Assert.fail;

public class KasperMatcherTest {

    @XKasperUnregistered
    public class HelloMessageResult extends IndexedEntity implements QueryResult {

        public static final String ENTITY_NAME = "Hello";

        private final String message;

        // ------------------------------------------------------------------------

        public HelloMessageResult(final KasperID id,
                                  final Long version, final DateTime lastModificationDate,
                                  final String message) {
            super(id, ENTITY_NAME, version, lastModificationDate);
            this.message = checkNotNull(message);
        }

        // ------------------------------------------------------------------------

        public String getMessage() {
            return this.message;
        }

    }

    @XKasperUnregistered
    public class HelloMessagesResult extends CollectionQueryResult<HelloMessageResult> { }

    private static final KasperID HELLO_ID = DefaultKasperId.random();
    private static final String MESSAGE = "hello world";

    // ------------------------------------------------------------------------

     final Object hello_1 = new HelloMessagesResult().<HelloMessagesResult>withList(
            new ArrayList<HelloMessageResult>() {{
                this.add(new HelloMessageResult(
                        HELLO_ID,
                        0L,
                        DateTime.now(),
                        MESSAGE
                ));
            }}
    );

    final Object hello_2 = new HelloMessagesResult().<HelloMessagesResult>withList(
            new ArrayList<HelloMessageResult>() {{
                this.add(new HelloMessageResult(
                        HELLO_ID,
                        0L,
                        anyDate(),
                        MESSAGE
                ));
            }}
    );

    final Object hello_3 = new HelloMessagesResult().<HelloMessagesResult>withList(
            new ArrayList<HelloMessageResult>() {{
                this.add(new HelloMessageResult(
                        HELLO_ID,
                        1L,
                        anyDate(),
                        MESSAGE
                ));
            }}
    );

    final Object hello_4 = new HelloMessagesResult().<HelloMessagesResult>withList(
            new ArrayList<HelloMessageResult>()
    );

    // ------------------------------------------------------------------------

    @Test
    public void testPrimitives() {

        assertTrue(equalTo(42L).matches(42L));
        assertFalse(equalTo(42L).matches(14L));
        assertFalse(equalTo(42L).matches(42));

        assertTrue(equalTo(42).matches(42));

        assertTrue(equalTo(true).matches(true));
        assertFalse(equalTo(false).matches(true));

        assertTrue(equalTo("foo").matches("foo"));
        assertFalse(equalTo("foo").matches("bar"));

        final DateTime date_1 = DateTime.now();
        final DateTime date_2 = DateTime.now().plus(14);
        assertTrue(equalTo(date_1).matches(date_1));
        assertFalse(equalTo(date_1).matches(date_2));

        class Test {
            private Object object = null;
            Test(Object o) { object = o; }
            void set(Object o) { object = o; }
        }
        assertTrue(equalTo(new Test(null)).matches(new Test(null)));
        assertFalse(equalTo(new Test(null)).matches(new Test(42)));
        final Test t = new Test(0);
        t.set(t);
        assertTrue(equalTo(t).matches(t));

    }

    // ------------------------------------------------------------------------

    @Test
    public void testDeepEqualsOK() {

        // Given,
        final Matcher matcher = equalTo(hello_2);
        final StringDescription description = new StringDescription();
        matcher.describeTo(description);

        // When
        if (! matcher.matches(hello_1)) {
            // Then
            System.out.println(description);
            fail();
        }
    }

    @Test
    public void testDeepEqualsBAD_1() {

        // Given,
        final Matcher matcher = equalTo(hello_3);
        final StringDescription description = new StringDescription();
        matcher.describeTo(description);

        // When
        if (! matcher.matches(hello_1)) {
            // Then
            System.out.println(description);
        } else {
            fail();
        }
    }

    @Test
    public void testDeepEqualsBAD_2() {

        // Given,
        final Matcher matcher = equalTo(hello_4);
        final StringDescription description = new StringDescription();
        matcher.describeTo(description);

        // When
        if (! matcher.matches(hello_1)) {
            // Then
            System.out.println(description);
        } else {
            fail();
        }
    }

}