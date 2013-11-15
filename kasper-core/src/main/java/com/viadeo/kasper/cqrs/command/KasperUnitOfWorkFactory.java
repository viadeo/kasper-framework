// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import org.axonframework.unitofwork.TransactionManager;
import org.axonframework.unitofwork.UnitOfWork;
import org.axonframework.unitofwork.UnitOfWorkFactory;

public class KasperUnitOfWorkFactory implements UnitOfWorkFactory {

    private final TransactionManager transactionManager;

    /**
     * Initializes the Unit of Work Factory to create Unit of Work that are not bound to any transaction.
     */
    public KasperUnitOfWorkFactory() {
        this(null);
    }

    /**
     * Initializes the factory to create Unit of Work bound to transactions managed by the given
     * <code>transactionManager</code>
     *
     * @param transactionManager The transaction manager to manage the transactions for Unit Of Work created by this
     *                           factory
     */
    public KasperUnitOfWorkFactory(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public UnitOfWork createUnitOfWork() {
        return KasperUnitOfWork.startAndGet(transactionManager);
    }

}
