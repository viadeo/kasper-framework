package com.viadeo.kasper.test.doc.web;

import com.viadeo.kasper.core.boot.*;
import com.viadeo.kasper.doc.KasperLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KasperConfigurator {

    private static final Logger LOGGER = LoggerFactory.getLogger(KasperConfigurator.class);

	/**
	 * The KasperLibrary instance to be used
	 */
	private KasperLibrary kasperLibrary;

	/**
	 * The processors to register
	 */
	static final Class<?>[] PROCESSORS = {
		CommandsDocumentationProcessor.class,
		ConceptsDocumentationProcessor.class,
		DomainsDocumentationProcessor.class,
		EventsDocumentationProcessor.class,
		HandlersDocumentationProcessor.class,
		ListenersDocumentationProcessor.class,
		RelationsDocumentationProcessor.class,
		RepositoriesDocumentationProcessor.class,
		QueryServicesDocumentationProcessor.class
	};

	// ------------------------------------------------------------------------

	/**
	 * Bootstrap the Jersey configuration
	 * Boot the Kasper root processor with only Kasper documentation processors
	 *
	 * @throws Exception
	 */
	public KasperConfigurator() {

        kasperLibrary = new KasperLibrary(); // Assign the static instance

        final AnnotationRootProcessor rootProcessor = new AnnotationRootProcessor();

        for (final Class<?> processorClazz : PROCESSORS) {
            final DocumentationProcessor<?,?> processor;
            try {
                processor = (DocumentationProcessor<?, ?>) processorClazz.newInstance();
                processor.setKasperLibrary(kasperLibrary);
                rootProcessor.registerProcessor(processor);
            } catch (final InstantiationException e) {
                e.printStackTrace();
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        rootProcessor.addScanPrefix("com.viadeo.kasper.test"); // Scan test classes (test use case)
        rootProcessor.setDoNotScanDefaultPrefix(true); // Do not use default boot processors

        rootProcessor.boot();
    }

    // ------------------------------------------------------------------------

    public KasperLibrary getKasperLibrary() {
        return this.kasperLibrary;
    }

}
