package com.viadeo.kasper.test.ddd;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.repository.Repository;
import org.axonframework.test.FixtureConfiguration;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.viadeo.kasper.ddd.IAggregateRoot;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.ddd.impl.AbstractRepository;

/**
 *
 * @param <D> Domain
 * @param <ENTITY> Aggregate Root
 */
public class KasperRepositoryTest<ENTITY extends IAggregateRoot> implements IRepository<ENTITY>, Repository<ENTITY> {

	private Repository<ENTITY> axonRepository;
	private final KasperRepositoryTest<ENTITY> that = this;

	//------------------------------------------------------------------------
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		// DO nothing
	}

	//------------------------------------------------------------------------
	
	public KasperRepositoryTest(final FixtureConfiguration<ENTITY> fixture) {
		this.axonRepository = fixture.getRepository();
	}
	
	@Override
	public ENTITY load(final Object aggregateIdentifier, final Long expectedVersion) {
		return this.axonRepository.load(aggregateIdentifier, expectedVersion);
	}

	@Override
	public ENTITY load(final Object aggregateIdentifier) {
		return this.axonRepository.load(aggregateIdentifier);
	}

	@Override
	public void add(ENTITY aggregate) {
		this.axonRepository.add(aggregate);
	}

	// ------------------------------------------------------------------------
	
	public void setEventBus(final EventBus eventBus) {
		if (EventSourcingRepository.class.isAssignableFrom(this.axonRepository.getClass())) {
			@SuppressWarnings("rawtypes") // Safe
			final EventSourcingRepository ESRepository = (EventSourcingRepository) this.axonRepository;
			ESRepository.setEventBus(eventBus);
		}
	}

	// ------------------------------------------------------------------------
	
	/**
	 * @param repositoryClass the repository to mock as
	 * @return a mocked repository which uses (this) as implementation
	 */
	@SuppressWarnings("unchecked")
	public <R extends IRepository<ENTITY>> R asMockOf(final Class<R> repositoryClass) {
		final R mocked = mock(repositoryClass);
		
		when(mocked.load(any(), any(Long.class))).then(
				new Answer<ENTITY>() {
					public ENTITY answer(InvocationOnMock invocation) {
						final Object aggregateIdentifier = (Object) invocation.getArguments()[0];
						final Long expectedVersion = (Long) invocation.getArguments()[1];
			            return that.load(aggregateIdentifier, expectedVersion);
			         }				
				}
			);
		
		when(mocked.load(any())).then(
				new Answer<ENTITY>() {
					public ENTITY answer(InvocationOnMock invocation) {
						final Object aggregateIdentifier = (Object) invocation.getArguments()[0];
			            return that.load(aggregateIdentifier);
			         }				
				}
			);		
		
		if (AbstractRepository.class.isAssignableFrom(repositoryClass)) {
			doAnswer(new Answer<Object>() {
				public Object answer(InvocationOnMock invocation) {
					final EventBus eventBus = (EventBus) invocation.getArguments()[0];
					that.setEventBus(eventBus);
		            return "";
		         }				
			}).when((AbstractRepository<?>) mocked).setEventBus(any(EventBus.class));
		}
		
		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				final ENTITY aggregate = (ENTITY) invocation.getArguments()[0];
				that.add(aggregate);
	            return "";
	         }				
		}).when(mocked).add((ENTITY) any(IAggregateRoot.class));				
		
		return mocked;
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 * Static repository mock
	 * 
	 * @param fixture the Axon fixture to be used by the repository
	 * @param repositoryClass the repository class to mock as
	 * @return a mocked repository
	 */
	static public <E extends IAggregateRoot, R extends IRepository<E>> R mockAs(
			final FixtureConfiguration<E> fixture, final Class<R> repositoryClass) {
		final KasperRepositoryTest<E> repository = new KasperRepositoryTest<E>(fixture); 		
		return repository.asMockOf(repositoryClass);
	}
	
}
