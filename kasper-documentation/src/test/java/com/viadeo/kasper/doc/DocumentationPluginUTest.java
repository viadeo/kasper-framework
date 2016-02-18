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
