package com.viadeo.kasper.test.core.boot;


import static org.mockito.Mockito.*;
import junit.framework.TestCase;

import com.viadeo.kasper.core.boot.IAnnotationProcessor;
import com.viadeo.kasper.core.boot.AnnotationRootProcessor;

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
