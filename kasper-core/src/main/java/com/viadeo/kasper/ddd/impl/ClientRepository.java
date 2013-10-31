package com.viadeo.kasper.ddd.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.IRepository;
import org.axonframework.repository.AggregateNotFoundException;
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

/**
 * Wrapping class for Kasper repositories only exposing client methods
 *
 * @param <AGR>
 */
public final class ClientRepository<AGR extends AggregateRoot> {

    final IRepository<AGR> repository;

    public ClientRepository(final IRepository repository) {
        this.repository = repository;
    }

    // -----

    @SuppressWarnings("unchecked")
    public <I extends IRepository<AGR>> I original() {
        return (I) this.repository;
    }

    // -----

    public Optional<AGR> load(final KasperID id, final Long expectedVersion) {
        try {
            return Optional.of((AGR) this.repository.load(id, expectedVersion));
        } catch (final AggregateNotFoundException e) {
            return Optional.absent();
        }
    }

    public Optional<AGR> load(final KasperID id) {
        try {
            return Optional.of((AGR) this.repository.load(id));
        } catch (final AggregateNotFoundException e) {
            return Optional.absent();
        }
    }

    public void add(final AGR aggregate) {
        this.repository.add(aggregate);
    }

    public boolean has(final KasperID id) {
        return this.repository.has(id);
    }

}
