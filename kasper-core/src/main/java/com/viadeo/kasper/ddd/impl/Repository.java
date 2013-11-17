// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.exceptions.KasperCommandException;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventsourcing.AggregateDeletedException;
import org.axonframework.repository.AggregateNotFoundException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 
 * Base Kasper repository implementation for an entity storage repository
 *
 * Decorates an Axon repository :
 * - load() and add() are redirected to the decorated repository
 * - the decorated repository will use an ActionRepositoryFacade
 *   in order to call doSave(), doDelete() and doLoad() before finally
 *   calling (this), in order to let doXXX() methods abstract for final
 *   implementation
 *
 * Add special methods :
 * - get()
 * - has() + abstract doHas()
 * - optional doUpdate()
 *
 * @param <AGR> Aggregate Root
 *
 */
public abstract class Repository<AGR extends AggregateRoot> implements IRepository<AGR> {

    /**
     * The Axon repository
     */
    private DecoratedAxonRepository<AGR> axonRepository;

    /**
     * Temporary storage of event bus for lazy assignation
     */
    private EventBus eventBus;

    /**
     * Store initilization state
     */
    private boolean initialized = false;
	
	// ========================================================================

    /**
     * Initialize the repository
     */
	@Override
	public final void init() {
		if ( ! initialized) {
            @SuppressWarnings("unchecked") // Safe
            final Optional<Class<AGR>> entityType =
                    (Optional<Class<AGR>>) (ReflectionGenericsResolver.getParameterTypeFromClass(
                            this.getClass(), IRepository.class, IRepository.ENTITY_PARAMETER_POSITION));

            if (!entityType.isPresent()) {
                throw new KasperException("Cannot determine entity type for " + this.getClass().getName());
            }

            this.axonRepository = checkNotNull(this.getDecoratedRepository(entityType.get()));

            if (null != eventBus) {
                this.axonRepository.setEventBus(eventBus);
            }

            initialized = true;
        }
	}

    /**
     * @return the default instance of the decorated repository
     */
    protected DecoratedAxonRepository<AGR> getDecoratedRepository(final Class<AGR> entityType) {
 		return new AxonRepository<>(
                new ActionRepositoryVersionFacade<>(this),
                entityType
        );
    }
	
	// ------------------------------------------------------------------------

    /**
     * Lazy set the Axon repository
     */
	public void setEventBus(final EventBus eventBus) {
        if (null != this.axonRepository) {
		    this.axonRepository.setEventBus(checkNotNull(eventBus));
        } else {
            this.eventBus = checkNotNull(eventBus);
        }
	}
	
	// ========================================================================	
	// Redirect Axon.Repository calls to decored instance
	// ========================================================================
	
	/**
	 * @see org.axonframework.repository.Repository#load(java.lang.Object, java.lang.Long)
	 */
	@Override
	public final AGR load(final Object aggregateIdentifier, final Long expectedVersion) {
        init();
		return this.axonRepository.load(aggregateIdentifier, expectedVersion);
	}

	/**
	 * @see org.axonframework.repository.Repository#load(java.lang.Object)
	 */
	@Override
	public final AGR load(final Object aggregateIdentifier) {
        init();
		return this.axonRepository.load(aggregateIdentifier);
	}

 	/**
	 * @see org.axonframework.repository.Repository#add(Object)
	 */
	@Override
	public final void add(final AGR aggregate) {
        init();

        /* All aggregates must have an ID */
        if (null == aggregate.getIdentifier()) {
            throw new KasperCommandException("Aggregates must have an ID (use setID()) before saves");
        }

		this.axonRepository.add(aggregate);
	}

    // ------------------------------------------------------------------------
    // Defines new additional public handlers for Kasper repositories
    // ------------------------------------------------------------------------

    /**
     * Get an aggregate without planning further save on UOW commit
     *
     * @param aggregateIdentifier the aggregate identifier to fetch
     * @param expectedVersion the aggregate expected version to fetch
     * @return the fetched aggregate if any
     */
    @Override
    public final AGR get(final KasperID aggregateIdentifier, final Long expectedVersion) {
        return this.doLoad((Object) aggregateIdentifier, expectedVersion);
    }

    /**
     * Get an aggregate without planning further save on UOW commit
     *
     * @param aggregateIdentifier
     * @return the fetched aggregate if any
     */
    @Override
    public final AGR get(final KasperID aggregateIdentifier) {
        return this.get(aggregateIdentifier, null);
    }

    /**
     * (Optional) indicates existence of an aggregate in the repository
     *
     * @param id the aggregate id
     * @return true if this aggregate exists
     */
    @Override
    public final boolean has(final KasperID id) {
        return this.doHas(id);
    }

	// ========================================================================
    // Decored Axon repository will finally call our methods for action
    // through its ActionRepositoryFacade indirection
    // ========================================================================
	
	/**
	 * Load an aggregate from the repository
	 * 
	 * Convenient Axon wrapper for proper Kasper typing, ID type conformance checking
     * and Optional management
	 * 
	 * @param aggregateIdentifier the aggregate identifier
	 * @param expectedVersion the version of the aggregate to load
	 * 
	 * @return the aggregate
	 */
	protected AGR doLoad(final Object aggregateIdentifier, final Long expectedVersion) {
		checkNotNull(aggregateIdentifier);
		
		if (KasperID.class.isAssignableFrom(aggregateIdentifier.getClass())) {
			
			final Optional<AGR> agr = this.doLoad((KasperID) aggregateIdentifier, expectedVersion);

			if (agr.isPresent()) {

                /* manages with deleted aggregates */
                if (agr.get().isDeleted()) {
                    throw new AggregateDeletedException(agr.get().getEntityId(), "Not found");
                }

				return agr.get();
			}

			throw new AggregateNotFoundException(aggregateIdentifier, "Not found aggregate"); // Axon
			
		} else {
			throw new KasperException(String.format(
                        "Unable to manage with identifier of this kind : %s - should be KasperID",
                        aggregateIdentifier.getClass()
            ));
		}
	}
	
	// ------------------------------------------------------------------------
    // Abstract handlers to be implemented by child classes
    // ------------------------------------------------------------------------
	
	/**
	 * loads an aggregate from the repository
	 * 
	 * @param aggregateIdentifier the aggregate identifier
	 * @param expectedVersion the version of the aggregate to load or null
	 * 
	 * @return the (optional) aggregate
	 */
	protected abstract Optional<AGR> doLoad(final KasperID aggregateIdentifier, final Long expectedVersion);
	
	/**
	 * saves a new (create) or existing (update) aggregate to the repository
     *
     * in case of an update, the aggregate version will be set to null
     *
     * if you override doUpdate() this method will only be called on save
	 * 
	 * @param aggregate the aggregate to be saved on the repository
	 */
	protected abstract void doSave(final AGR aggregate);

 	/**
	 * updates an existing aggregate to the repository
     *
     * Overrides this method if you want to clearly separate saves and updates in two methods
	 *
	 * @param aggregate the aggregate to be saved on the repository
	 */
	protected void doUpdate(final AGR aggregate) {
        this.doSave(aggregate);
    }
	
	/**
	 * deletes (or mark as deleted) an existing aggregate from the repository
	 * 
	 * @param aggregate the aggregate to be deleted from the repository
	 */
	protected abstract void doDelete(final AGR aggregate);

    /**
     * (Optional) indicates existence of an aggregate in the repository
     *
     * @param aggregateIdentifier the aggregate identifier
     * @return true if an aggregate exists with this id
     */
    protected boolean doHas(final KasperID aggregateIdentifier) {
        throw new UnsupportedOperationException("has() operation not implemented");
    }

}
