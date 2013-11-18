// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.repository;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.IRepository;
import org.axonframework.repository.AggregateNotFoundException;

/**
 * Wrapping class for Kasper repositories, only exposing client methods
 *
 * @param <AGR> the enclosed aggregate type
 */
public final class ClientRepository<AGR extends AggregateRoot> {

    private final IRepository<AGR> repository;

    public ClientRepository(final IRepository repository) {
        this.repository = repository;
    }

    // -----

    /**
     * Return the original (unwrapped) aggregate
     * to be used for business indexes methods access
     */
    @SuppressWarnings("unchecked")
    public <I extends IRepository<AGR>> I business() {
        return (I) this.repository;
    }

    // -----

    /**
     * Try to load an aggregate, planning its save on UOW commit
     *
     * @param id the aggregate id
     * @param expectedVersion the aggregate expected version
     * @return the (optional) aggregate
     */
    public Optional<AGR> load(final KasperID id, final Long expectedVersion) {
        try {
            if (null == expectedVersion) {
                return Optional.of(this.repository.load(id));
            } else {
                return Optional.of(this.repository.load(id, expectedVersion));
            }
        } catch (final AggregateNotFoundException e) {
            return Optional.absent();
        }
    }

    /**
     * Try to load an aggregate, planning its save on UOW commit
     *
     * @param id the aggregate id
     * @param expectedVersion the (optional) aggregate expected version
     * @return the (optional) aggregate
     */
    public Optional<AGR> load(final KasperID id, final Optional<Long> expectedVersion) {
        Long version = null;
        if (expectedVersion.isPresent()) {
            version = expectedVersion.get();
        }

        return load(id, version);
    }

    /**
     * Try to load an aggregate, planning its save on UOW commit
     *
     * @param id the aggregate id
     * @return the (optional) aggregate
     */
    public Optional<AGR> load(final KasperID id) {
        try {
            return Optional.of(this.repository.load(id));
        } catch (final AggregateNotFoundException e) {
            return Optional.absent();
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Try to load an aggregate, no save will be planned on UOW commit
     *
     * Deprecated design : aggregates should only be loaded, with idea of change,
     * other data must be obtained from a query ad apssed to the command
     *
     * @param id the aggregate id
     * @param expectedVersion the aggregate expected version
     * @return the (optional) aggregate
     */
    @SuppressWarnings("deprecated")
    public Optional<AGR> get(final KasperID id, final Long expectedVersion) {
        try {
            if (null == expectedVersion) {
                return Optional.of(this.repository.get(id));
            } else {
                return Optional.of(this.repository.get(id, expectedVersion));
            }
        } catch (final AggregateNotFoundException e) {
            return Optional.absent();
        }
    }

    /**
     * Try to load an aggregate, no save will be planned on UOW commit
     *
     * Deprecated design : aggregates should only be loaded, with idea of change,
     * other data must be obtained from a query ad apssed to the command
     *
     * @param id the aggregate id
     * @param expectedVersion the (optional) aggregate expected version
     * @return the (optional) aggregate
     */
    public Optional<AGR> get(final KasperID id, final Optional<Long> expectedVersion) {
        Long version = null;
        if (expectedVersion.isPresent()) {
            version = expectedVersion.get();
        }

        return get(id, version);
    }

    /**
     * Try to load an aggregate, no save will be planned on UOW commit
     * Deprecated design : aggregates should only be loaded, with idea of change, other data must be obtained from a query ad apssed to the command
     *
     * @param id the aggregate id
     * @return the (optional) aggregate
     */
    @Deprecated
    @SuppressWarnings("deprecated")
    public Optional<AGR> get(final KasperID id){
        try {
            return Optional.of(this.repository.get(id));
        } catch (final AggregateNotFoundException e) {
            return Optional.absent();
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Add a new aggregate to the repository
     *
     * @param aggregate the aggregate to be saved
     */
    public void add(final AGR aggregate) {
        this.repository.add(aggregate);
    }

    /**
     * Checks whether an aggregate exists in repository
     *
     * @param id the id of the aggregate to test for existence
     * @return true if an aggregate with this id exists in the repository
     */
    public boolean has(final KasperID id) {
        return this.repository.has(id);
    }

}
