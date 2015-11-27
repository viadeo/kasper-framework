// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.gateway;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.context.Context;
import org.axonframework.unitofwork.CurrentUnitOfWork;
import org.axonframework.unitofwork.DefaultUnitOfWork;
import org.axonframework.unitofwork.UnitOfWork;
import org.axonframework.unitofwork.UnitOfWorkFactory;

public class ContextualizedUnitOfWork extends DefaultUnitOfWork {

    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public Optional<Context> getContext() {
        return Optional.of(context);
    }

    public static ContextualizedUnitOfWork getCurrentUnitOfWork() {
        UnitOfWork unitOfWork = CurrentUnitOfWork.get();

        if ( ! (unitOfWork instanceof ContextualizedUnitOfWork) ) {
            throw new IllegalStateException("No contextualized UnitOfWork is currently started for this thread.");
        }

        return (ContextualizedUnitOfWork) unitOfWork;
    }

    public static class Factory implements UnitOfWorkFactory {
        @Override
        public UnitOfWork createUnitOfWork() {
            ContextualizedUnitOfWork unitOfWork = new ContextualizedUnitOfWork();
            unitOfWork.start();
            return unitOfWork;
        }
    }
}
