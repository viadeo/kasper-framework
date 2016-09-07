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
package com.viadeo.kasper.doc;

import com.viadeo.kasper.doc.element.DocumentedPlatform;
import com.viadeo.kasper.doc.initializer.DocumentedElementVisitor;
import com.viadeo.kasper.platform.Platform;
import com.viadeo.kasper.platform.builder.PlatformContext;
import com.viadeo.kasper.platform.bundle.descriptor.DomainDescriptor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class DocumentationPluginUTest {

    private DocumentedPlatform documentedPlatform;

    // ------------------------------------------------------------------------

    public DocumentationPluginUTest() {
        documentedPlatform = mock(DocumentedPlatform.class);
    }

    @Before
    public void setUp() {
        reset(documentedPlatform);
    }

    // ------------------------------------------------------------------------

    @Test(expected = IllegalStateException.class)
    public void getKasperDocResource_fromNonInitializedPlugin_shouldThrowException(){
        // Given
        final DocumentationPlugin documentationPlugin = new DocumentationPlugin();

        // When
        documentationPlugin.getDocumentedPlatform();

        // Then throws an exception
    }

    @Test
    public void getKasperDocResource_fromInitializedPlugin_shouldReturnDocumentation(){
        // Given
        final DocumentationPlugin documentationPlugin = new DocumentationPlugin();
        documentationPlugin.initialize(mock(PlatformContext.class));
        documentationPlugin.onPlatformStarted(mock(Platform.class));

        // When
        final DocumentedPlatform documentedPlatform = documentationPlugin.getDocumentedPlatform();

        // Then
        assertTrue(documentationPlugin.isInitialized());
        assertNotNull(documentedPlatform);
    }

    @Test
    public void initialize_withNoDomainBundle_shouldBeOk(){
        // Given
        final DocumentationPlugin documentationPlugin = new DocumentationPlugin(documentedPlatform);

        // When
        documentationPlugin.initialize(mock(PlatformContext.class));
        documentationPlugin.onPlatformStarted(mock(Platform.class));

        // Then
        assertTrue(documentationPlugin.isInitialized());
        verify(documentedPlatform).accept(any(DocumentedElementVisitor.class));
    }

    @Test
    public void initialize_withDomainBundle_shouldBeOk(){
        // Given
        final DocumentationPlugin documentationPlugin = new DocumentationPlugin(documentedPlatform);

        final String domainName = "Foobar";

        final DomainDescriptor descriptor = mock(DomainDescriptor.class);
        when(descriptor.getName()).thenReturn(domainName);

        // When
        documentationPlugin.initialize(mock(PlatformContext.class));
        documentationPlugin.onDomainRegistered(descriptor);
        documentationPlugin.onPlatformStarted(mock(Platform.class));

        // Then
        assertTrue(documentationPlugin.isInitialized());
        verify(documentedPlatform).registerDomain(refEq(domainName), refEq(descriptor));
        verify(documentedPlatform).accept(any(DocumentedElementVisitor.class));
    }

    @Test(expected = NullPointerException.class)
    public void initialize_withNullAsDomainDescriptors_shouldThrowException(){
        // Given
        final DocumentationPlugin documentationPlugin = new DocumentationPlugin(documentedPlatform);

        // When
        documentationPlugin.initialize(mock(PlatformContext.class));
        documentationPlugin.onDomainRegistered(null);

        // Then throws an exception
    }

    @Test
    public void get_documented_platform_is_ok() {
        // Given
        final DocumentationPlugin documentationPlugin = new DocumentationPlugin(documentedPlatform);
        documentationPlugin.initialize(mock(PlatformContext.class));
        documentationPlugin.onPlatformStarted(mock(Platform.class));

        // When
        List<DocumentedPlatform> documentedPlatforms = documentationPlugin.get(DocumentedPlatform.class);

        // Then
        assertNotNull(documentedPlatforms);
        assertTrue(documentedPlatforms.size() == 1);
    }

}
