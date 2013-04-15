package com.viadeo.kasper.test.core.boot;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import junit.framework.TestCase;

import org.junit.Test;

import com.viadeo.kasper.core.boot.AnnotationRootProcessor;
import com.viadeo.kasper.core.boot.IAnnotationProcessor;

public class KasperAnnotationRootProcessorTest extends TestCase {
	
	// ------------------------------------------------------------------------
	// TEST SIMPLE WORKING PROCESSOR/CLASS SCANNING
	// ------------------------------------------------------------------------
	
	public static @interface XTestAnnotation { }
	
	public static interface TestInterface { }
	
	@XTestAnnotation
	public static final class TestClass implements TestInterface {	}	
	
	// -----
	
	public static class TestProcessor implements IAnnotationProcessor<XTestAnnotation, TestInterface> {

		@Override
		public void process(final Class<?> clazz) {
			// Do nothing, we are just interested by execution
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	@Test
	public void testUserProcessorCall() {
		final AnnotationRootProcessor rootProcessor = new AnnotationRootProcessor();
		
		final TestProcessor realProcessor = new TestProcessor();
		final IAnnotationProcessor<?,?> processor = spy(realProcessor);	
		
		rootProcessor.registerProcessor(processor);
		rootProcessor.setDoNotScanDefaultPrefix(true);
		
		rootProcessor.boot();
		
		verify(processor).process(TestClass.class);
	}
	
}
