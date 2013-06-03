package com.viadeo.kasper.test.core.boot;

import com.viadeo.kasper.core.boot.AnnotationRootProcessor;
import com.viadeo.kasper.core.boot.IAnnotationProcessor;
import junit.framework.TestCase;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class KasperAnnotationRootProcessorTest extends TestCase {
	
	// ------------------------------------------------------------------------
	// TEST SIMPLE WORKING PROCESSOR/CLASS SCANNING
	// ------------------------------------------------------------------------
	
	public static @interface XTestAnnotation { }
	
	public interface TestInterface { }
	
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
	public void testShouldRunProcessOnNewRegisterProcessor() {
		final AnnotationRootProcessor rootProcessor = new AnnotationRootProcessor();
		
		final TestProcessor realProcessor = new TestProcessor();
		final IAnnotationProcessor<?,?> processor = spy(realProcessor);	
		
		rootProcessor.registerProcessor(processor);
		rootProcessor.setDoNotScanDefaultPrefix(true);
		rootProcessor.addScanPrefix(this.getClass().getPackage().getName());

		rootProcessor.boot();
		
		verify(processor).process(TestClass.class);
	}

    @Test
    public void testShouldNotScanInPackageWithoutAnySetPrefix() {
        final AnnotationRootProcessor rootProcessor = new AnnotationRootProcessor();

        final TestProcessor realProcessor = new TestProcessor();
        final IAnnotationProcessor<?,?> processor = spy(realProcessor);

        rootProcessor.registerProcessor(processor);
        rootProcessor.setDoNotScanDefaultPrefix(true);

        rootProcessor.boot();

        verify(processor,never()).process(TestClass.class);
    }
	
}
