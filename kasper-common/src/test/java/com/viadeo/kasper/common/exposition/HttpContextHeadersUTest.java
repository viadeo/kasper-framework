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
package com.viadeo.kasper.common.exposition;

import com.google.common.base.Optional;
import org.junit.Test;

import static org.junit.Assert.*;

public class HttpContextHeadersUTest {

    @Test
    public void toPropertyKey_withUnknownProperty_returnAbsent() {
        Optional<HttpContextHeaders> optional = HttpContextHeaders.fromPropertyKey("fake");
        assertNotNull(optional);
        assertFalse(optional.isPresent());
    }

    @Test
    public void toPropertyKey_withKnownPropertyInLowerCase_isOk() {
        Optional<HttpContextHeaders> optional = HttpContextHeaders.fromPropertyKey("userlang");
        assertNotNull(optional);
        assertTrue(optional.isPresent());
        assertEquals(HttpContextHeaders.HEADER_USER_LANG, optional.get());
    }

    @Test
    public void toPropertyKey_withKnownPropertyInUpperCase_isOk() {
        Optional<HttpContextHeaders> optional = HttpContextHeaders.fromPropertyKey("USERLANG");
        assertNotNull(optional);
        assertTrue(optional.isPresent());
        assertEquals(HttpContextHeaders.HEADER_USER_LANG, optional.get());
    }

    @Test
    public void toPropertyKey_withKnownProperty_isOk() {
        Optional<HttpContextHeaders> optional = HttpContextHeaders.fromPropertyKey("userLang");
        assertNotNull(optional);
        assertTrue(optional.isPresent());
        assertEquals(HttpContextHeaders.HEADER_USER_LANG, optional.get());
    }

    @Test
    public void toPropertyKey_withUnknownHeader_returnAbsent() {
        Optional<HttpContextHeaders> optional = HttpContextHeaders.fromHeader("X-KASPER-FAKE");
        assertNotNull(optional);
        assertFalse(optional.isPresent());
    }

    @Test
    public void toPropertyKey_withKnownHeaderInLowerCase_isOk() {
        Optional<HttpContextHeaders> optional = HttpContextHeaders.fromHeader("x-kasper-lang");
        assertNotNull(optional);
        assertTrue(optional.isPresent());
        assertEquals(HttpContextHeaders.HEADER_USER_LANG, optional.get());
    }

    @Test
    public void toPropertyKey_withKnownHeaderInUpperCase_isOk() {
        Optional<HttpContextHeaders> optional = HttpContextHeaders.fromHeader("X-KASPER-LANG");
        assertNotNull(optional);
        assertTrue(optional.isPresent());
        assertEquals(HttpContextHeaders.HEADER_USER_LANG, optional.get());
    }

    @Test
    public void toPropertyKey_withKnownHeader_isOk() {
        Optional<HttpContextHeaders> optional = HttpContextHeaders.fromHeader("X-KASPER-LANG");
        assertNotNull(optional);
        assertTrue(optional.isPresent());
        assertEquals(HttpContextHeaders.HEADER_USER_LANG, optional.get());
    }

}
