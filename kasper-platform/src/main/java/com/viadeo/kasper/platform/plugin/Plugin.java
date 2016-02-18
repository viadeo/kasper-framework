// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.plugin;

import com.viadeo.kasper.platform.PlatformAware;
import com.viadeo.kasper.platform.builder.PlatformContext;

import java.util.Comparator;
import java.util.List;

/**
 * The Plugin interface represents an extension way to the platform useful to add new functionality like : documentation,
 * exposition, etc...
 *
 * Note that the plugin will be initialized after the components is wired during the construction of the platform.
 */
public interface Plugin extends PlatformAware {

    Comparator<Plugin> COMPARATOR = new Comparator<Plugin>() {
        @Override
        public int compare(final Plugin o1, final Plugin o2) {
            return Integer.compare(o1.getPhase(), o2.getPhase());
        }
    };

    Comparator<Plugin> REVERSED_COMPARATOR = new Comparator<Plugin>() {
        @Override
        public int compare(final Plugin o1, final Plugin o2) {
            return -1 * Integer.compare(o1.getPhase(), o2.getPhase());
        }
    };

    // ------------------------------------------------------------------------

    /**
     * Initialize the plugin
     *
     * @param platform the platform context
     */
    void initialize(PlatformContext platform);

    /**
     * @return the name of the plugin
     */
    String getName();

    /**
     * @return the phase value of this object.
     */
    int getPhase();

    /**
     * Return the bean instances that match the given object type
     * @param clazz the class or interface to match
     * @param <E> the object type
     * @return a list with the matching beans
     */
    <E> List<E> get(Class<E> clazz);

}
