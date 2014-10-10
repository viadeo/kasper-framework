// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.google.common.collect.Lists;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.query.CollectionQueryResult;
import com.viadeo.kasper.cqrs.query.IndexedEntity;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.impl.DefaultKasperId;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.tools.KasperMatcher.*;
import static org.junit.Assert.*;

public class KasperMatcherTest {

    public class HelloMessageResult extends IndexedEntity implements QueryResult {

        public static final String ENTITY_NAME = "Hello";
        private final String message;

        // ------------------------------------------------------------------------

        public HelloMessageResult(final KasperID id,
                                  final Long version,
                                  final DateTime lastModificationDate,
                                  final String message) {
            super(id, ENTITY_NAME, version, lastModificationDate);
            this.message = checkNotNull(message);
        }

        // ------------------------------------------------------------------------

        public String getMessage() {
            return this.message;
        }

    }

    public class HelloMessagesResult extends CollectionQueryResult<HelloMessageResult> {
        public HelloMessagesResult() {
            this(Lists.<HelloMessageResult>newArrayList());
        }

        public HelloMessagesResult(Collection<HelloMessageResult> list) {
            super(list);
        }
    }

    private static final KasperID HELLO_ID = DefaultKasperId.random();
    private static final String MESSAGE = "hello world";

    // ------------------------------------------------------------------------

     final Object hello_1 = new HelloMessagesResult(
            new ArrayList<HelloMessageResult>() {{
                this.add(new HelloMessageResult(
                    HELLO_ID,
                    0L,
                    DateTime.now(),
                    MESSAGE
                ));
            }}
    );

    final Object hello_2 = new HelloMessagesResult(
            new ArrayList<HelloMessageResult>() {{
                this.add(new HelloMessageResult(
                    HELLO_ID,
                    0L,
                    anyDate(),
                    MESSAGE
                ));
            }}
    );

    final Object hello_3 = new HelloMessagesResult(
            new ArrayList<HelloMessageResult>() {{
                this.add(new HelloMessageResult(
                    HELLO_ID,
                    1L,
                    anyDate(),
                    MESSAGE
                ));
            }}
    );

    final Object hello_4 = new HelloMessagesResult(
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
        if ( ! matcher.matches(hello_1)) {
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
        if ( ! matcher.matches(hello_1)) {
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
        if ( ! matcher.matches(hello_1)) {
            // Then
            System.out.println(description);
        } else {
            fail();
        }
    }

    @Test
    public void testEmptyList() {

        // Given,
        final Matcher matcher = equalTo(new HelloMessagesResult());
        final StringDescription description = new StringDescription();
        matcher.describeTo(description);

        // When
        if ( ! matcher.matches(new HelloMessagesResult())) {
            // Then
            System.out.println(description);
            fail();
        }
    }

    @Test
    public void testDateTime() {
        // Given
        final DateTime now = DateTime.now();
        DateTime a = new DateTime(now.getMillis(), DateTimeZone.forOffsetHours(1));
        DateTime b = new DateTime(now.getMillis(), DateTimeZone.UTC);

        // When
        final boolean matches = KasperMatcher.equalTo(a).matches(b);

        // Then
        assertTrue(matches);
        assertFalse(a.equals(b));
    }

    @Test
    public void testAnySecurityToken() {
        // Given
        final String value = "foobar";

        // When
        final boolean matches = equalTo(anySecurityToken()).matches(value);

        // Then
        assertTrue(matches);
        assertFalse(anySecurityToken().equals(value));
    }

    @Test
    public void testAnyKasperId() {
        // Given
        final KasperID id = DefaultKasperId.random();

        // When
        final boolean matches = equalTo(anyKasperId()).matches(id);

        // Then
        assertTrue(matches);
        assertFalse(anyKasperId().equals(id));
    }

}
