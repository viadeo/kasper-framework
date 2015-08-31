package com.viadeo.kasper.platform.bundle.fixture.command.handler;

import com.viadeo.kasper.core.component.annotation.XKasperCommandHandler;
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;
import com.viadeo.kasper.core.component.command.AutowiredCommandHandler;
import com.viadeo.kasper.platform.bundle.fixture.api.DummyDomain;
import com.viadeo.kasper.platform.bundle.fixture.api.DummyUnregisteredCommand;
import com.viadeo.kasper.platform.bundle.fixture.infra.DummyBackend;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;

@XKasperCommandHandler(domain = DummyDomain.class)
@XKasperUnregistered
public class DummyUnregisteredCommandHandler extends AutowiredCommandHandler<DummyUnregisteredCommand> {

    private DummyBackend toto;
    private Integer foo;

    @Inject
    public DummyUnregisteredCommandHandler(DummyBackend toto, @Value("${foo}") Integer foo) {
        this.toto = toto;
        this.foo = foo;
    }

    public int getFoo() {
        return foo;
    }
}