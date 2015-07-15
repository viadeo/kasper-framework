// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition.query;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

/**
 * This class is used as filter for properties discovery. It uses java modifiers to check if a
 * property (can be a method, field, constructor or any class that implements Member interface) is
 * visible.
 * 
 * The filter acts by excluding the properties with specified modifiers. Here are some examples :
 * 
 * <pre>
 * // will filter nothing :
 * new VisibilityFilter();
 * 
 * // exclude only private and transient:
 * new VisibilityFilter(Modifier.TRANSIENT, Modifier.PRIVATE);
 * 
 * // exclude only public!! and allow all the rest
 * new VisibilityFilter(Modifier.public);
 * </pre>
 * 
 * So the idea is to pass to the constructor all the Modifier.XXX modifiers that you want to be
 * filtered.
 * 
 */
public final class VisibilityFilter {
    
    /**
     * Modifier.TRANSIENT Modifier.NATIVE
     */
    public static final VisibilityFilter DEFAULT = new VisibilityFilter(Modifier.TRANSIENT,
            Modifier.NATIVE, Modifier.STATIC, Modifier.ABSTRACT);
    
    public static final VisibilityFilter ALL = new VisibilityFilter();
    
    public static final VisibilityFilter PROTECTED = new VisibilityFilter(Modifier.TRANSIENT,
            Modifier.NATIVE, Modifier.STATIC, Modifier.PRIVATE, Modifier.ABSTRACT);
    
    public static final VisibilityFilter PACKAGE_PUBLIC = new VisibilityFilter(Modifier.TRANSIENT,
            Modifier.NATIVE, Modifier.STATIC, Modifier.PRIVATE, Modifier.PROTECTED, Modifier.ABSTRACT);

    private static final int JAVA_MODIFIERS = Modifier.PUBLIC | Modifier.PROTECTED
            | Modifier.PRIVATE | Modifier.ABSTRACT | Modifier.STATIC | Modifier.FINAL
            | Modifier.TRANSIENT | Modifier.VOLATILE | Modifier.SYNCHRONIZED | Modifier.NATIVE
            | Modifier.STRICT | Modifier.INTERFACE;

    // ------------------------------------------------------------------------
    
    private int filter;
    
    // ------------------------------------------------------------------------

    /**
     * Creates a new VisibilityFilter with specified modifiers. You must use existing values from
     * Modifier class otherwise an exception will be thrown.
     * 
     * @param modifier all the modifiers you want to exclude.
     */
    public VisibilityFilter(final int... modifier) {
        filter = 0;
        
        for (final int m : modifier) {

            if ((m & JAVA_MODIFIERS) == 0) {
                throw new IllegalArgumentException(
                        "One of the modifiers is not a standard java modifier : "
                        + Modifier.toString(m)
                );
            }
            
            filter = filter | m;
        }
    }

    // ------------------------------------------------------------------------
    
    /**
     * Checks whether this member is visible or not according to this filter.
     * @param member a <code>Member</code>
     * @return true if the member is visible, false otherwise
     */
    public boolean isVisible(final Member member) {
        return ((member.getModifiers() & filter) == 0);
    }
    
}
