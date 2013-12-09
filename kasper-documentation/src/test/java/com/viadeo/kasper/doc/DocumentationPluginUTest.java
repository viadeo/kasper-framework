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

import static org.mockito.Mockito.*;

public class DocumentationPluginUTest {

    private DocumentedPlatform documentedPlatform;

    public DocumentationPluginUTest(){
        documentedPlatform = mock(DocumentedPlatform.class);
    }

    @Before
    public void setUp(){
        reset(documentedPlatform);
    }

    @Test(expected = IllegalStateException.class)
    public void getKasperDocResource_fromNonInitializedPlugin_shouldThrowException(){
        // Given
        DocumentationPlugin documentationPlugin = new DocumentationPlugin(documentedPlatform);

        // When
        documentationPlugin.getKasperDocResource();

        // Then throws an exception
    }

    @Test
    public void getKasperDocResource_fromInitializedPlugin_shouldReturnDocumentation(){
        // Given
        DocumentationPlugin documentationPlugin = new DocumentationPlugin(documentedPlatform);
        documentationPlugin.initialize(mock(Platform.class), mock(MetricRegistry.class));

        // When
        KasperDocResource kasperDocResource = documentationPlugin.getKasperDocResource();

        // Then
        Assert.assertTrue(documentationPlugin.isInitialized());
        Assert.assertNotNull(kasperDocResource);
    }

    @Test
    public void initialize_withNoDomainBundle_shouldBeOk(){
        // Given
        DocumentationPlugin documentationPlugin = new DocumentationPlugin(documentedPlatform);
        DomainDescriptor[] descriptors = {};

        // When
        documentationPlugin.initialize(mock(Platform.class), mock(MetricRegistry.class), descriptors);

        // Then
        Assert.assertTrue(documentationPlugin.isInitialized());
        verify(documentedPlatform).accept(any(DocumentedElementVisitor.class));
        verifyNoMoreInteractions(documentedPlatform);
    }

    @Test
    public void initialize_withDomainBundle_shouldBeOk(){
        // Given
        DocumentationPlugin documentationPlugin = new DocumentationPlugin(documentedPlatform);

        String domainName = "Foobar";

        DomainDescriptor descriptor = mock(DomainDescriptor.class);
        when(descriptor.getName()).thenReturn(domainName);
        DomainDescriptor[] descriptors = {descriptor};

        // When
        documentationPlugin.initialize(mock(Platform.class), mock(MetricRegistry.class), descriptors);

        // Then
        Assert.assertTrue(documentationPlugin.isInitialized());
        verify(documentedPlatform).registerDomain(refEq(domainName), refEq(descriptor));
        verify(documentedPlatform).accept(any(DocumentedElementVisitor.class));
    }

    @Test(expected = NullPointerException.class)
    public void initialize_withNullAsDomainDescriptors_shouldThrowException(){
        // Given
        DocumentationPlugin documentationPlugin = new DocumentationPlugin(documentedPlatform);
        DomainDescriptor[] descriptors = null;

        // When
        documentationPlugin.initialize(mock(Platform.class), mock(MetricRegistry.class), descriptors);

        // Then throws an exception
    }
}
