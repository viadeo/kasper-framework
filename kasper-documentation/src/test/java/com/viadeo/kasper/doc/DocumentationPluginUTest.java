// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptor;
import com.viadeo.kasper.doc.element.DocumentedPlatform;
import com.viadeo.kasper.doc.initializer.DocumentedElementVisitor;
import com.viadeo.kasper.doc.web.KasperDocResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
        final DocumentationPlugin documentationPlugin = new DocumentationPlugin(documentedPlatform);

        // When
        documentationPlugin.getKasperDocResource();

        // Then throws an exception
    }

    @Test
    public void getKasperDocResource_fromInitializedPlugin_shouldReturnDocumentation(){
        // Given
        final DocumentationPlugin documentationPlugin = new DocumentationPlugin(documentedPlatform);
        documentationPlugin.initialize(mock(Platform.class), mock(MetricRegistry.class));

        // When
        final KasperDocResource kasperDocResource = documentationPlugin.getKasperDocResource();

        // Then
        assertTrue(documentationPlugin.isInitialized());
        Assert.assertNotNull(kasperDocResource);
    }

    @Test
    public void initialize_withNoDomainBundle_shouldBeOk(){
        // Given
        final DocumentationPlugin documentationPlugin = new DocumentationPlugin(documentedPlatform);
        final DomainDescriptor[] descriptors = {};

        // When
        documentationPlugin.initialize(mock(Platform.class), mock(MetricRegistry.class), descriptors);

        // Then
        assertTrue(documentationPlugin.isInitialized());
        verify(documentedPlatform).accept(any(DocumentedElementVisitor.class));
        verifyNoMoreInteractions(documentedPlatform);
    }

    @Test
    public void initialize_withDomainBundle_shouldBeOk(){
        // Given
        final DocumentationPlugin documentationPlugin = new DocumentationPlugin(documentedPlatform);

        final String domainName = "Foobar";

        final DomainDescriptor descriptor = mock(DomainDescriptor.class);
        when(descriptor.getName()).thenReturn(domainName);
        final DomainDescriptor[] descriptors = {descriptor};

        // When
        documentationPlugin.initialize(mock(Platform.class), mock(MetricRegistry.class), descriptors);

        // Then
        assertTrue(documentationPlugin.isInitialized());
        verify(documentedPlatform).registerDomain(refEq(domainName), refEq(descriptor));
        verify(documentedPlatform).accept(any(DocumentedElementVisitor.class));
    }

    @Test(expected = NullPointerException.class)
    public void initialize_withNullAsDomainDescriptors_shouldThrowException(){
        // Given
        final DocumentationPlugin documentationPlugin = new DocumentationPlugin(documentedPlatform);
        final DomainDescriptor[] descriptors = null;

        // When
        documentationPlugin.initialize(mock(Platform.class), mock(MetricRegistry.class), descriptors);

        // Then throws an exception
    }

}
