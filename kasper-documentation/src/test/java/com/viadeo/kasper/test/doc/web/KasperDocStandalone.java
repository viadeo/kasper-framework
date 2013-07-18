package com.viadeo.kasper.test.doc.web;

import com.sun.jersey.api.container.grizzly2.servlet.GrizzlyWebContainerFactory;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class KasperDocStandalone extends KasperConfigurator {

    public static void main(String [] args) throws IOException, InterruptedException {
        final String baseUri = "http://localhost:9998/";
        final Map<String, String> initParams = new HashMap<>();

        initParams.put("com.sun.jersey.config.property.packages", "com.viadeo.kasper.test.doc.web");

        System.out.println("Starting grizzly...");

        final HttpServer server = GrizzlyWebContainerFactory.create(baseUri, initParams);

        server.getServerConfiguration().addHttpHandler(new StaticHttpHandler("src/main/resources/META-INF/resources/doc/"),"/doc");

        System.out.println(String.format("Try out %skasper/doc/domains \nAccess UI at %sdoc/index.htm", baseUri, baseUri, baseUri));

        System.in.read();

        server.stop();
        System.exit(0);
    }

}
