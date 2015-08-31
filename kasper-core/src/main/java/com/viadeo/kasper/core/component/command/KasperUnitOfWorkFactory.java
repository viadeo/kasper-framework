// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command;

import org.axonframework.unitofwork.TransactionManager;
import org.axonframework.unitofwork.UnitOfWork;
import org.axonframework.unitofwork.UnitOfWorkFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public class KasperUnitOfWorkFactory implements UnitOfWorkFactory {

    private final TransactionManager transactionManager;

    // ------------------------------------------------------------------------

    /**
     * Initializes the Unit of Work Factory to create Unit of Work that are not bound to any transaction.
     */
    public KasperUnitOfWorkFactory() {
        this(null);
    }

    // ------------------------------------------------------------------------

    public KasperUnitOfWorkFactory(final TransactionManager transactionManager) {
        this.transactionManager = checkNotNull(transactionManager);
    }

    @Override
    public UnitOfWork createUnitOfWork() {
        return KasperUnitOfWork.startAndGet(transactionManager);
    }

}
