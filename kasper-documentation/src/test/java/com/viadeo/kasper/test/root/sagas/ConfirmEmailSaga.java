// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.root.sagas;

import com.google.common.base.Optional;
import com.viadeo.kasper.event.annotation.XKasperSaga;
import com.viadeo.kasper.event.saga.Saga;
import com.viadeo.kasper.event.saga.factory.SagaFactory;
import com.viadeo.kasper.event.saga.SagaIdReconciler;
import com.viadeo.kasper.test.root.Facebook;
import com.viadeo.kasper.test.root.events.MemberCreatedEvent;
import com.viadeo.kasper.test.root.events.MemberHasConfirmedEmailEvent;

import java.util.concurrent.TimeUnit;

@XKasperSaga(domain = Facebook.class, description = "Confirm an email for a given expiration time")
public class ConfirmEmailSaga implements Saga {

    @XKasperSaga.Start(getter = "getEntityId")
    @XKasperSaga.Schedule(delay = 1, unit = TimeUnit.DAYS, methodName = "notConfirmed")
    public void onMemberCreated(final MemberCreatedEvent event) { }

    @XKasperSaga.End(getter = "getId")
    public void onConfirmedEvent(final MemberHasConfirmedEmailEvent event) { }

    public void notConfirmed() { }

    @Override
    public Optional<SagaFactory> getFactory() {
        return Optional.absent();
    }

    @Override
    public Optional<SagaIdReconciler> getIdReconciler() {
        return Optional.absent();
    }
}
