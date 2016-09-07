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

import com.google.common.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;

public class BeanProperty {

    private final String name;
    private final Class declaringClass;
    private final Annotation[] annotations;
    private final TypeToken typeToken;

    // ------------------------------------------------------------------------

    public BeanProperty(final String name, final Class declaringClass,
                        final Annotation[] annotations, final TypeToken typeToken) {
        super();

        this.name = checkNotNull(name);
        this.declaringClass = checkNotNull(declaringClass);
        this.annotations = Arrays.copyOf(checkNotNull(annotations), annotations.length);
        this.typeToken = checkNotNull(typeToken);
    }

    // ------------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public Class getDeclaringClass() {
        return declaringClass;
    }

    public Annotation[] getAnnotations() {
        return Arrays.copyOf(annotations, annotations.length);
    }

    public TypeToken getTypeToken() {
        return typeToken;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }
        if (null == obj) {
            return false;
        }
        if ( ! getClass().equals(obj.getClass())) {
            return false;
        }

        final BeanProperty other = (BeanProperty) obj;
        if (null == name) {
            if (null != other.name) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }

        return true;
    }

}
