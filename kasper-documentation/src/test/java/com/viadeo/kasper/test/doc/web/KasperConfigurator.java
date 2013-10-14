// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.doc.web;

import com.viadeo.kasper.client.platform.configuration.PlatformConfiguration;
import com.viadeo.kasper.client.platform.configuration.PlatformFactory;
import com.viadeo.kasper.core.boot.AnnotationRootProcessor;
import com.viadeo.kasper.doc.KasperLibrary;
import com.viadeo.kasper.doc.configuration.DefaultAutoDocumentationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KasperConfigurator {

    private static final Logger LOGGER = LoggerFactory.getLogger(KasperConfigurator.class);

	/**
	 * The KasperLibrary instance to be used
	 */
	private KasperLibrary kasperLibrary;

	// ------------------------------------------------------------------------

	/**
	 * Bootstrap the Jersey configuration
	 * Boot the Kasper root processor with only Kasper documentation processors
	 *
	 * @throws Exception
	 */
	public KasperConfigurator() {

        final AnnotationRootProcessor rootProcessor = new AnnotationRootProcessor();

        final PlatformFactory platformFactory = new PlatformFactory().configure();
        final PlatformConfiguration pf = platformFactory.getPlatformConfiguration();

        final DefaultAutoDocumentationConfiguration docConf =
                new DefaultAutoDocumentationConfiguration();

        docConf.registerToRootProcessor(rootProcessor, pf.resolverFactory());
        this.kasperLibrary = docConf.getKasperLibrary(pf.resolverFactory());

        rootProcessor.addScanPrefix("com.viadeo.kasper.test"); // Scan test classes (test use case)
        rootProcessor.setDoNotScanDefaultPrefix(true); // Do not use default boot processors

        rootProcessor.boot();
    }

    // ------------------------------------------------------------------------

    public KasperLibrary getKasperLibrary() {
        return this.kasperLibrary;
    }

}
