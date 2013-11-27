package com.viadeo.kasper.test.doc.web;

import com.google.common.collect.ImmutableList;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.viadeo.kasper.client.platform.domain.descriptor.*;
import com.viadeo.kasper.doc.initializer.DefaultDocumentedElementInitializer;
import com.viadeo.kasper.doc.element.DocumentedPlatform;
import com.viadeo.kasper.doc.web.KasperDocResource2;
import com.viadeo.kasper.doc.web.ObjectMapperKasperResolver;
import com.viadeo.kasper.test.root.Facebook;
import com.viadeo.kasper.test.root.commands.AddConnectionToMemberCommand;
import com.viadeo.kasper.test.root.entities.Member;
import com.viadeo.kasper.test.root.entities.Member_connectedTo_Member;
import com.viadeo.kasper.test.root.events.MemberCreatedEvent;
import com.viadeo.kasper.test.root.handlers.AddConnectionToMemberHandler;
import com.viadeo.kasper.test.root.listeners.MemberCreatedEventListener;
import com.viadeo.kasper.test.root.queries.GetMembersQueryHandler;
import com.viadeo.kasper.test.root.repositories.MemberConnectionsRepository;
import com.viadeo.kasper.test.root.repositories.MemberRepository;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;

import java.io.IOException;

public class KasperDocStandalone2 {
    public static void main(String [] args) throws IOException, InterruptedException {
        final String baseUri = "http://localhost:9988/";

        DomainDescriptor domainDescriptor = new DomainDescriptor(
                Facebook.class
                , ImmutableList.<QueryHandlerDescriptor>of(new QueryHandlerDescriptor(GetMembersQueryHandler.class, GetMembersQueryHandler.GetMembersQuery.class, GetMembersQueryHandler.MembersResult.class))
                , ImmutableList.<CommandHandlerDescriptor>of(new CommandHandlerDescriptor(AddConnectionToMemberHandler.class, AddConnectionToMemberCommand.class))
                , ImmutableList.<RepositoryDescriptor>of(
                      new RepositoryDescriptor(MemberRepository.class, DomainDescriptorFactory.retrieveAggregateDescriptor(Member.class))
                    , new RepositoryDescriptor(MemberConnectionsRepository.class, DomainDescriptorFactory.retrieveAggregateDescriptor(Member_connectedTo_Member.class))
                )
                , ImmutableList.<EventListenerDescriptor>of(new EventListenerDescriptor(MemberCreatedEventListener.class, MemberCreatedEvent.class))
        );

        DocumentedPlatform documentedPlatform = new DocumentedPlatform();
        documentedPlatform.registerDomain(Facebook.NAME, domainDescriptor);
        documentedPlatform.accept(new DefaultDocumentedElementInitializer());

        final KasperDocResource2 res = new KasperDocResource2(documentedPlatform);

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
