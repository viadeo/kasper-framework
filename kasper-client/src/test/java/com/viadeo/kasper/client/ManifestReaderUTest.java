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
package com.viadeo.kasper.client;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.*;

public class ManifestReaderUTest {

    @Test(expected = NullPointerException.class)
    public void toPath_withNull_throwException() {
        // Given nothing

        // When
        ManifestReader.toPath(null);

        // Then throw exception
    }

    @Test
    public void toPath_withClass_isOk() {
        // Given nothing

        // When
        final String path = ManifestReader.toPath(KasperClient.class);

        // Then
        assertNotNull(path);
        assertEquals(KasperClient.class.getName().replaceAll("\\.", "/") + ".class", path);
    }

    @Test(expected = NullPointerException.class)
    public void toUrl_withNull_throwException() {
        // Given nothing

        // When
        ManifestReader.toUrl(null);

        // Then throw exception
    }

    @Test
    public void toUrl_withValidPath_isOk() {
        // Given
        final String path = "org/junit/Test.class";

        // When
        final URL url = ManifestReader.toUrl(path);

        // Then
        assertNotNull(url);
    }

    @Test
    public void read_fromURL_isOk() throws MalformedURLException {
        // Given
        final URL url = Thread.currentThread().getContextClassLoader().getResource(ManifestReader.toPath(com.viadeo.test.manifest.Test.class));
        final ManifestReader manifestReader = new ManifestReader(url);

        // When
        manifestReader.read();

        // Then
        assertTrue(manifestReader.getManifest().isPresent());
        assertTrue(manifestReader.getKasperVersion().isPresent());
        assertEquals("42", manifestReader.getKasperVersion().get());
    }

    @Test
    public void read_fromClass_isOk() throws MalformedURLException {
        // Given
        final ManifestReader manifestReader = new ManifestReader(com.viadeo.test.manifest.Test.class);

        // When
        manifestReader.read();

        // Then
        assertTrue(manifestReader.getManifest().isPresent());
        assertTrue(manifestReader.getKasperVersion().isPresent());
        assertEquals("42", manifestReader.getKasperVersion().get());
    }

}
