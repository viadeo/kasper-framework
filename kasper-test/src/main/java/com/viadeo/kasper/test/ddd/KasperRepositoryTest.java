package com.viadeo.kasper.test.ddd;

import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.ddd.impl.Repository;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.test.FixtureConfiguration;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 *
 * @param <ENTITY> Aggregate Root
 */
@XKasperUnregistered
public class KasperRepositoryTest<ENTITY extends AggregateRoot> implements IRepository<ENTITY> {

	private org.axonframework.repository.Repository<ENTITY> axonRepository;
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
			final EventSourcingRepository eventRepository = (EventSourcingRepository) this.axonRepository;
			eventRepository.setEventBus(eventBus);
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
						final Object aggregateIdentifier = invocation.getArguments()[0];
						final Long expectedVersion = (Long) invocation.getArguments()[1];
			            return that.load(aggregateIdentifier, expectedVersion);
			         }				
				}
			);
		
		when(mocked.load(any())).then(
				new Answer<ENTITY>() {
					public ENTITY answer(InvocationOnMock invocation) {
						final Object aggregateIdentifier = invocation.getArguments()[0];
			            return that.load(aggregateIdentifier);
			         }				
				}
			);		
		
		if (Repository.class.isAssignableFrom(repositoryClass)) {
			doAnswer(new Answer<Object>() {
				public Object answer(InvocationOnMock invocation) {
					final EventBus eventBus = (EventBus) invocation.getArguments()[0];
					that.setEventBus(eventBus);
		            return "";
		         }				
			}).when((Repository<?>) mocked).setEventBus(any(EventBus.class));
		}
		
		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				final ENTITY aggregate = (ENTITY) invocation.getArguments()[0];
				that.add(aggregate);
	            return "";
	         }				
		}).when(mocked).add((ENTITY) any(AggregateRoot.class));
		
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
	public static <E extends AggregateRoot, R extends IRepository<E>> R mockAs(
			final FixtureConfiguration<E> fixture, final Class<R> repositoryClass) {
		final KasperRepositoryTest<E> repository = new KasperRepositoryTest<>(fixture);
		return repository.asMockOf(repositoryClass);
	}
	
}
