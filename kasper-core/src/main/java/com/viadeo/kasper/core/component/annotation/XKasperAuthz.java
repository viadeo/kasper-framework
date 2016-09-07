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
package com.viadeo.kasper.core.component.annotation;

import com.viadeo.kasper.core.interceptor.authorization.AuthorizationManager;
import com.viadeo.kasper.core.interceptor.authorization.CombinesWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Indicate that the handler needs authorization to be executed
 *
 */
public interface XKasperAuthz {

    /**
     * Requires a user in context to imply a particular permission in order to execute the annotated Handler.
     * Value contains the list of permissions to be tested.
     * Manager specifies the {@link com.viadeo.kasper.core.interceptor.authorization.AuthorizationManager} to use in order to check given authorizations.
     * CombinesWith indicates the operation to do between authorizations.
     *
     * <p>For example:</p>
     * <code>
     * &#64;XKasperRequiresPermissions( value = "read",  manager = DefaultAuthorizationSecurityManager.class , combinesWith = CombinesWith.OR )
     * public class GetExampleQueryHandler extends QueryHandler
     * </code>
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface RequiresPermissions {

        /**
         * the permissions which be passed to the check method.
         * @return permissions
         */
        String[] value() default {};

        Class<? extends AuthorizationManager> manager() default AuthorizationManager.class;

        CombinesWith combinesWith() default CombinesWith.AND;

    }

    // ------------------------------------------------------------------------

    /**
     * Requires a user in context to imply a particular permission in order to execute the annotated Handler.
     * Value contains the list of roles to be tested.
     * Manager specifies the {@link AuthorizationManager} to use in order to check given authorizations.
     * CombinesWith indicates the operation to do between authorizations.
     *
     * <p>For example: </p>
     * <code>
     * &#64;XKasperRequiresRoles( value = "admin",  manager = DefaultAuthorizationSecurityManager.class , combinesWith = CombinesWith.OR )
     * public class GetExampleQueryHandler extends QueryHandler
     * </code>
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface RequiresRoles {

        /**
         * the roles which be passed to the check method.
         * @return roles
         */
        String[] value() default {};

        Class<? extends AuthorizationManager> manager() default AuthorizationManager.class;

        CombinesWith combinesWith() default CombinesWith.AND;

    }
}
