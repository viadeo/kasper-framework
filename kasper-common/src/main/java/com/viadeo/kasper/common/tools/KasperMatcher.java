// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.tools;

import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.api.id.StringKasperId;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.joda.time.DateTime;

import java.lang.reflect.Field;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class KasperMatcher extends BaseMatcher<Object> {

    private final Object expected;
    private Field failedField;
    private Object failedFieldExpectedValue;
    private Object failedFieldActualValue;

    // ------------------------------------------------------------------------

    public KasperMatcher(final Object expected) {
        this.expected = checkNotNull(expected);
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean matches(final Object item) {
        return this.matches(expected, item, new ArrayList<Long>());
    }

    // ------------------------------------------------------------------------

    private Long key(final Object expected, final Object actual) {
        return expected.hashCode() * 100L + actual.hashCode();
    }

    private boolean record(final List<Long> done, final Object expected, final Object actual, final boolean ret) {
        done.add(key(expected, actual));
        return ret;
    }

    private boolean recorded(final List<Long> done, final Object expected, final Object actual) {
        return done.contains(key(expected, actual));
    }

    // ------------------------------------------------------------------------

    private boolean matches(final Object expected, final Object actual, final List<Long> done) {
        boolean match;

        if (expected == anySecurityToken()) {
            return true;
        }

        if ((null == expected) && (null != actual)) {
            return false;
        }

        if ((null == actual) && (null != expected)) {
            return false;
        }

        if (null == actual) {
            return true;
        }

        if (recorded(done, expected, actual)) {
            return true;
        }

        if (expected.equals(actual)) {
            return record(done, expected, actual, true);
        }

        if (DateTime.class.isAssignableFrom(actual.getClass())
                && (expected == anyDate())) {
            return record(done, expected, actual, true);
        }

        if (KasperID.class.isAssignableFrom(actual.getClass())
                && (expected == anyKasperId())) {
            return record(done, expected, actual, true);
        }

        if (DateTime.class.isAssignableFrom(expected.getClass()) &&
                DateTime.class.isAssignableFrom(actual.getClass()) ) {
            return ((DateTime)expected).isEqual((DateTime) actual);
        }

        if (isBaseJavaClass(expected.getClass())) {
            return record(done, expected, actual, expected.equals(actual));
        }

        if (expected.getClass().isArray()) {
            match = deepArrayEquals(expected, actual, done);

        } else if (Collection.class.isAssignableFrom(expected.getClass())) {
            match = deepCollectionEquals(expected, actual, done);

        } else {
            match = expected.getClass().isInstance(actual)
                        && expected.getClass().equals(actual.getClass());
            if ( match) {
                match = fieldsMatch(expected.getClass(), expected, actual, done);
            }
        }

        return record(done, expected, actual, match);
    }

    // ------------------------------------------------------------------------

    private boolean fieldsMatch(final Class aClass, final Object expected, final Object actual, final List<Long> done) {

        boolean match = true;

        final Field[] fields = aClass.getDeclaredFields();
        for (final Field field : fields) {

            if (field.getName().contains("this$")) {
                continue;
            }

            field.setAccessible(true);

            try {
                final Object expectedFieldValue = field.get(expected);
                final Object actualFieldValue = field.get(actual);

                if ( ! matches(expectedFieldValue, actualFieldValue, done)) {
                    failedField = field;
                    failedFieldExpectedValue = expectedFieldValue;
                    failedFieldActualValue = actualFieldValue;
                    match = false;
                    break;
                }

            } catch (final IllegalAccessException e) {
                throw new RuntimeException("Could not confirm object equality due to an exception", e);
            }
        }
        
        if (match && (aClass.getSuperclass() != Object.class)) {
            match = fieldsMatch(aClass.getSuperclass(), expected, actual, done);
        }
        
        return match;
    }

    // ------------------------------------------------------------------------

    private boolean isBaseJavaClass(final Class clazz) {
        return
                Long.class.equals(clazz)
             || Integer.class.equals(clazz)
             || Short.class.equals(clazz)
             || Byte.class.equals(clazz)
             || Float.class.equals(clazz)
             || Double.class.equals(clazz)
             || Character.class.equals(clazz)
             || Boolean.class.equals(clazz)
             || String.class.equals(clazz);
    }

    // ------------------------------------------------------------------------

    private boolean deepArrayEquals(final Object expectedFieldValue, final Object actualFieldValue, final List<Long> done) {
        if ( ! actualFieldValue.getClass().isArray() ) {
            return false;
        }

        final Object[] expectedArray = (Object[]) expectedFieldValue;
        final Object[] actualArray = (Object[]) actualFieldValue;

        if (Arrays.deepEquals(expectedArray, actualArray)) {
            return true;
        }

        if (expectedArray.length != actualArray.length) {
            return false;
        }

        for (int i = 0 ; i < expectedArray.length ; i ++) {
            if ( ! matches(expectedArray[i], actualArray[i], done)) {
                return false;
            }
        }

        return true;
    }

    // ------------------------------------------------------------------------

    private boolean deepCollectionEquals(final Object expectedFieldValue, final Object actualFieldValue, List<Long> done) {

        if ( ! Collection.class.isAssignableFrom(actualFieldValue.getClass()) ) {
            return false;
        }

        final Collection expectedCollection = (Collection) expectedFieldValue;
        final Collection actualCollection = (Collection) actualFieldValue;

        if (expectedCollection.equals(actualCollection)) {
            return true;
        }

        if (expectedCollection.size() != actualCollection.size()) {
            return false;
        }

        final Iterator itExpected = expectedCollection.iterator();
        final Iterator itActual = actualCollection.iterator();
        while (itExpected.hasNext()) {
            if ( ! matches(itExpected.next(), itActual.next(), done) ) {
                return false;
            }
        }

        return true;
    }

    // ------------------------------------------------------------------------

    /**
     * Returns the field that failed comparison, if any. This value is only populated after {@link #matches(Object)} is
     * called and a mismatch has been detected.
     *
     * @return the field that failed comparison, if any
     */
    public Field getFailedField() {
        return failedField;
    }

    /**
     * Returns the expected value of a failed field comparison, if any. This value is only populated after {@link
     * #matches(Object)} is called and a mismatch has been detected.
     *
     * @return the expected value of the field that failed comparison, if any
     */
    public Object getFailedFieldExpectedValue() {
        return failedFieldExpectedValue;
    }

    /**
     * Returns the actual value of a failed field comparison, if any. This value is only populated after {@link
     * #matches(Object)} is called and a mismatch has been detected.
     *
     * @return the actual value of the field that failed comparison, if any
     */
    public Object getFailedFieldActualValue() {
        return failedFieldActualValue;
    }

    // ------------------------------------------------------------------------

    @Override
    public void describeTo(Description description) {
        description.appendText(expected.getClass().getName());
        if (failedField != null) {
            description.appendText(" (failed on field '")
                       .appendText(failedField.getName())
                       .appendText("')");
        }
    }
    
    // ------------------------------------------------------------------------

    private static final DateTime ANYDATE = new DateTime();
    
    /**
     * @return a date wildcard, faster than a matcher
     */
    public static DateTime anyDate() {
    	return ANYDATE;
    }


    private static final String SECURITY_TOKEN = "";

    /**
     * Any security token or null
     * @return empty String ("")
     */
    public static String anySecurityToken() {
        return SECURITY_TOKEN;
    }

    private static final KasperID KASPER_ID = new StringKasperId("wildcard");

    /**
     * @return a Kasper id wildcard
     */
    public static KasperID anyKasperId() {
        return KASPER_ID;
    }

    // ------------------------------------------------------------------------

    @Factory
    public static KasperMatcher equalTo(final Object expected) {
        return new KasperMatcher(expected);
    }

}
