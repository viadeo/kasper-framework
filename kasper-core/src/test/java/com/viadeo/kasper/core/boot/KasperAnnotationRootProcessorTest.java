// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.viadeo.kasper.core.test.TestProcessor;
import junit.framework.TestCase;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class KasperAnnotationRootProcessorTest extends TestCase {

	@Test
	public void testShouldRunProcessOnNewRegisterProcessor() {
        // Given
		final AnnotationRootProcessor rootProcessor = new AnnotationRootProcessor();
		
		final TestProcessor realProcessor = new TestProcessor();
		final AnnotationProcessor processor = spy(realProcessor);

        // When
		rootProcessor.registerProcessor(processor);
		rootProcessor.setDoNotScanDefaultPrefix(true);
		rootProcessor.addScanPrefix(realProcessor.getClass().getPackage().getName());

		rootProcessor.boot();

        // Then
		verify(processor).process(TestProcessor.TestClass.class);
        verify(processor).process(TestProcessor.TestChildClass.class);
	}

    @Test
    public void testShouldNotScanInPackageWithoutAnySetPrefix() {
        // Given
        final AnnotationRootProcessor rootProcessor = new AnnotationRootProcessor();

        final TestProcessor realProcessor = new TestProcessor();
        final AnnotationProcessor processor = spy(realProcessor);

        // When
        rootProcessor.registerProcessor(processor);
        rootProcessor.setDoNotScanDefaultPrefix(true);

        rootProcessor.boot();

        // Then
        verify(processor,never()).process(TestProcessor.TestClass.class);
    }
	
}
