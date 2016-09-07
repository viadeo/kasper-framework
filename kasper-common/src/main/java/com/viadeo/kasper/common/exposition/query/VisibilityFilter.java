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
package com.viadeo.kasper.common.exposition.query;

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
