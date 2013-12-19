package com.viadeo.kasper.client.platform.domain.sample2;

import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;


public class SampleDomainBox2 {

    public static class Infra { }

    @XKasperDomain(label = "SampleDomainBox2", prefix = "test", description = "A domain definition used only for the test")
    public static class MyCustomDomain implements Domain { }

    @XKasperCommandHandler(domain = MyCustomDomain.class)
    public static class MyCustomCommandHandler extends CommandHandler<Command> {

        public MyCustomCommandHandler(Infra infra) { }
    }
}
