// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.core.boot;

import com.viadeo.kasper.core.boot.AnnotationProcessor;
import com.viadeo.kasper.core.boot.AnnotationRootProcessor;
import com.viadeo.kasper.exception.KasperException;
import junit.framework.TestCase;
import org.junit.Test;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.mockito.Mockito.*;

public class KasperAnnotationRootProcessorTest extends TestCase {

    public static interface TestInterface { }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface XTestAnnotation { }

    @XTestAnnotation
    public static class TestClass implements TestInterface {}

    @XTestAnnotation
    public static final class TestChildClass extends TestClass { }

	// ------------------------------------------------------------------------
	// TEST SIMPLE WORKING PROCESSOR/CLASS SCANNING
	// ------------------------------------------------------------------------
	
	public static class TestProcessor implements AnnotationProcessor<XTestAnnotation, TestInterface> {

        @Override
        public boolean isAnnotationMandatory() {
            return true;
        }

		@Override
		public void process(final Class<?> clazz) {
			// Do nothing, we are just interested by execution
		}
		
	}

    // Define a test root processor in order to increase the visibility of the method 'process' to 'public' because we
    // are not the same package!
    public static class TestRootProcessor extends AnnotationRootProcessor {
        @Override
        public void process() {
            super.process();
        }
    }
	
	// ------------------------------------------------------------------------
	
	@Test
	public void testShouldRunProcessOnNewRegisterProcessor() {
        // Given
		final AnnotationRootProcessor rootProcessor = new AnnotationRootProcessor();
		
		final TestProcessor realProcessor = new TestProcessor();
		final AnnotationProcessor<?,?> processor = spy(realProcessor);

        // When
		rootProcessor.registerProcessor(processor);
		rootProcessor.setDoNotScanDefaultPrefix(true);
		rootProcessor.addScanPrefix(this.getClass().getPackage().getName());

		rootProcessor.boot();

        // Then
		verify(processor).process(TestClass.class);
        verify(processor).process(TestChildClass.class);
	}

    @Test
    public void testShouldNotScanInPackageWithoutAnySetPrefix() {
        // Given
        final AnnotationRootProcessor rootProcessor = new AnnotationRootProcessor();

        final TestProcessor realProcessor = new TestProcessor();
        final AnnotationProcessor<?,?> processor = spy(realProcessor);

        // When
        rootProcessor.registerProcessor(processor);
        rootProcessor.setDoNotScanDefaultPrefix(true);

        rootProcessor.boot();

        // Then
        verify(processor,never()).process(TestClass.class);
    }

    @Test
    public void testShouldThrownKasperErrorProcessWithUnexpectedSpringCircularReference(){
        // Given
        TestProcessor processor = mock(TestProcessor.class);
        doThrow(new BeanCurrentlyInCreationException(TestClass.class.getSimpleName())).when(processor).process(any(Class.class));

        TestRootProcessor rootProcessor = new TestRootProcessor();
        rootProcessor.registerProcessor(processor);
        rootProcessor.setDoNotScanDefaultPrefix(true);
        rootProcessor.addScanPrefix(this.getClass().getPackage().getName());

        Exception exception = null;

        // When
        try {
            rootProcessor.boot();
        } catch (Exception e) {
            exception = e;
            e.printStackTrace();
        }

        // Then
        assertNotNull(exception);
        assertTrue(exception instanceof KasperException);

        KasperException kasperException = (KasperException) exception;
        assertTrue(kasperException.getCause() instanceof BeanCurrentlyInCreationException);
    }

    @Test
    public void testShouldContinueProcessWithUnexpectedException(){
        // Given
        TestProcessor processor = mock(TestProcessor.class);
        doThrow(new RuntimeException("bing!")).when(processor).process(any(Class.class));

        TestRootProcessor rootProcessor = new TestRootProcessor();
        rootProcessor.registerProcessor(processor);
        rootProcessor.setDoNotScanDefaultPrefix(true);
        rootProcessor.addScanPrefix(this.getClass().getPackage().getName());

        Exception exception = null;

        // When
        rootProcessor.boot();

        // Then should never throw an exception
    }
}
