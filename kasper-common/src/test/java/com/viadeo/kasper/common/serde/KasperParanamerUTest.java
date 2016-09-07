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
package com.viadeo.kasper.common.serde;

import org.junit.Test;

import java.util.Date;

import static com.viadeo.kasper.common.serde.ImmutabilityModule.KasperParanamer;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class KasperParanamerUTest {

    @Test
    public void isImmutable_witString_returnTrue() {
        // Given
        final KasperParanamer kasperParanamer = new KasperParanamer();

        // When
        final boolean acceptable = kasperParanamer.isImmutable(String.class);

        // Then
        assertTrue(acceptable);
    }

    @Test
    public void isImmutable_withDate_returnFalse() {
        // Given
        final KasperParanamer kasperParanamer = new KasperParanamer();

        // When
        final boolean acceptable = kasperParanamer.isImmutable(Date.class);

        // Then
        assertFalse(acceptable);
    }

    @Test(expected = NullPointerException.class)
    public void isImmutable_witNull_throwException() {
        // Given
        final KasperParanamer kasperParanamer = new KasperParanamer();

        // When
        kasperParanamer.isImmutable(null);

        // Then throws an exception
    }

}
