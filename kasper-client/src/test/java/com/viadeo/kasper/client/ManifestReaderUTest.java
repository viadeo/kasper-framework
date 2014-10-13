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
