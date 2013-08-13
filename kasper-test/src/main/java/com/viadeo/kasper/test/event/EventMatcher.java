// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.event;

import com.viadeo.kasper.event.Event;
import org.axonframework.test.matchers.MatcherExecutionException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.joda.time.DateTime;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Matcher that will match an Object if all the fields on that Object contain values equal to the same field in the
 * expected instance.
 *
 * @param <E> The type of object
 * @author Allard Buijze
 * @since 1.1
 */
public class EventMatcher<E extends Event> extends BaseMatcher<E> {

    private final E expected;
    private Field failedField;
    private Object failedFieldExpectedValue;
    private Object failedFieldActualValue;

    /**
     * Initializes an EqualFieldsMatcher that will match an object with equal properties as the given
     * <code>expected</code> object.
     *
     * @param expected The expected object
     */
    public EventMatcher(final E expected) {
        this.expected = expected;
    }

    @Override
    public boolean matches(final Object item) {
        return expected.getClass().isInstance(item) && matchesSafely(item);
    }

    private boolean matchesSafely(final Object actual) {
        return expected.getClass().equals(actual.getClass())
                && fieldsMatch(expected.getClass(), expected, actual);
    }

    private boolean fieldsMatch(final Class<?> aClass, final Object expectedValue, final Object actual) {
        boolean match = true;
        
        for (Field field : aClass.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                final Object expectedFieldValue = field.get(expectedValue);
                final Object actualFieldValue = field.get(actual);
                if (expectedFieldValue != null && actualFieldValue != null && expectedFieldValue.getClass().isArray()) {
                    if (!Arrays.deepEquals(new Object[]{expectedFieldValue}, new Object[]{actualFieldValue})) {
                        failedField = field;
                        failedFieldExpectedValue = expectedFieldValue;
                        failedFieldActualValue = actualFieldValue;
                        match = false;
                    }
                } else if ((expectedFieldValue != null && !expectedFieldValue.equals(actualFieldValue))
                        || (expectedFieldValue == null && actualFieldValue != null)) {
                    failedField = field;
                    failedFieldExpectedValue = expectedFieldValue;
                    failedFieldActualValue = actualFieldValue;
                    match = false;
                }
                if (!match && (DateTime.class.isAssignableFrom(actualFieldValue.getClass())) && (expectedFieldValue == anyDate())) {
                	match = true;
                }
            } catch (IllegalAccessException e) {
                throw new MatcherExecutionException("Could not confirm object equality due to an exception", e);
            }
        }
        
        if (match && (aClass.getSuperclass() != Object.class)) {
            match = fieldsMatch(aClass.getSuperclass(), expectedValue, actual);
        }
        
        return match;
    }

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
     * @return a date wildcard
     */
    public static DateTime anyDate() {
    	return ANYDATE;
    }
    
    // ------------------------------------------------------------------------
    
    @Factory
    public static <E extends Event> EventMatcher<E> equalToEvent(E expected) {
        return new EventMatcher<>(expected);
    }
    
}
