package com.viadeo.kasper.platform.bundle.fixture.command.handler;

import com.viadeo.kasper.core.component.annotation.XKasperCommandHandler;
import com.viadeo.kasper.core.component.command.AutowiredCommandHandler;
import com.viadeo.kasper.platform.bundle.fixture.api.DummyCommand;
import com.viadeo.kasper.platform.bundle.fixture.api.DummyDomain;
import com.viadeo.kasper.platform.bundle.fixture.infra.DummyBackend;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;

@XKasperCommandHandler(domain = DummyDomain.class)
public class DummyCommandHandler extends AutowiredCommandHandler<DummyCommand> {

    private DummyBackend toto;
    private Integer foo;

    @Inject
    public DummyCommandHandler(DummyBackend toto, @Value("${foo}") Integer foo) {
        this.toto = toto;
        this.foo = foo;
    }

    public int getFoo() {
        return foo;
    }
}