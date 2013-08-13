// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.doc.web;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.viadeo.kasper.doc.web.KasperDocResource;
import com.viadeo.kasper.doc.web.ObjectMapperKasperResolver;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;

import java.io.IOException;

public class KasperDocStandalone extends KasperConfigurator {

    public static void main(String [] args) throws IOException, InterruptedException {
        final String baseUri = "http://localhost:9998/";

        final KasperConfigurator kasperConfigurator = new KasperConfigurator();

        final KasperDocResource res = new KasperDocResource();
        res.setKasperLibrary(kasperConfigurator.getKasperLibrary());

        final ResourceConfig rc = new PackagesResourceConfig("com.viadeo.kasper.test.doc.web");
        rc.getSingletons().add(res);
        rc.getProviderClasses().add(ObjectMapperKasperResolver.class);

        System.out.println("Starting grizzly...");

        final HttpServer server = GrizzlyServerFactory.createHttpServer(baseUri, rc);

        server.getServerConfiguration().addHttpHandler(new StaticHttpHandler("src/main/resources/META-INF/resources/doc/"),"/doc");

        System.out.println(String.format("Try out %skasper/doc/domains \nAccess UI at %sdoc/index.htm", baseUri, baseUri, baseUri));

        System.in.read();

        server.stop();
        System.exit(0);
    }

}
