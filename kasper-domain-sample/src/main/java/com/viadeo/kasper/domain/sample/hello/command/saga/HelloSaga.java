// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.command.saga;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.annotation.XKasperSaga;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.SagaIdReconciler;
import com.viadeo.kasper.domain.sample.hello.api.HelloDomain;
import com.viadeo.kasper.domain.sample.hello.api.event.BuddyChangedForHelloMessageEvent;
import com.viadeo.kasper.domain.sample.hello.api.event.HelloCreatedEvent;
import com.viadeo.kasper.domain.sample.hello.api.event.HelloDeletedEvent;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

@XKasperSaga(domain = HelloDomain.class, description = "hello saga" )
public class HelloSaga implements Saga {

    private static final Logger LOGGER = getLogger(HelloSaga.class);
    private String buddy;
    private KasperID id;

    @Override
    public Optional<SagaIdReconciler> getIdReconciler() {
        return Optional.absent();
    }

    @XKasperSaga.Start(getter = "getEntityId")
    public void start(final HelloCreatedEvent event){
        this.id = event.getEntityId();
        this.buddy = event.getForBuddy();
        LOGGER.info("A Hello saga has started : "+event.getEntityId());
    }

    @XKasperSaga.Step(getter = "getEntityId")
    @XKasperSaga.Schedule(delay = 1, unit = TimeUnit.MINUTES, methodName = "sayCoucou")
    public void stepHelloBuddyChanged(final BuddyChangedForHelloMessageEvent event){
        this.buddy = event.getNewForBuddy();
        LOGGER.info("A Hello step has been triggers : "+event.getEntityId());
    }

    @XKasperSaga.End(getter = "getEntityId")
    public void end(final HelloDeletedEvent event){
        LOGGER.info("A Hello saga has ended : "+event.getEntityId());
    }

    public String getBuddy() {
        return buddy;
    }

    private void sayCoucou(){
        LOGGER.info("A Hello schedule method call has been triggered by scheduler : " + this.id);
    }
}
