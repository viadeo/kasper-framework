// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.query.QueryService;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.doc.nodes.*;
import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.er.Relation;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Kasper autodoc library
 * 
 * Since Kasper components (concepts, relations, repositories, events, commands, handlers, listeners, ..)
 * are discovered by annotation processors with no specific order and relationships, this library allow
 * us to keep track of all components explicitely registered by the processors.
 * 
 * Once registered we can then deduce inferences, relationships, ownerships, etc..
 * 
 * So, all documentation nodes are calling this library in order to get dynamic links to other components
 * 
 * This library is not made for validation purposes, it just records submitted data
 * 
 */
@Component
public class KasperLibrary {

    /**
     * Access to the Kasper components resolvers
     */
    private Resolvers resolvers;

	/**
	 * Keeps track of registered domains, by name and by prefix
	 */
	private final Map<String, DocumentedDomain> domainNames;
	private final Map<String, DocumentedDomain> domainPrefixes;
	
	/**
	 * Stores all registered domain-related components
	 */
	private final Map<Class<? extends DocumentedDomainNode>, Map<String, ?>> domainEntities;
	
	/**
	 * Store commands (do not depend directly from a specific domain)
	 */
	private final Map<String, DocumentedCommand> commandEntities;
	
	/**
	 * Stores the concepts involved in a relation, as source or target entities
	 */
	private final Map<String, List<DocumentedRelation>> sourceConceptRelations;
	private final Map<String, List<DocumentedRelation>> targetConceptRelations;
	
	/**
	 * Stores all concepts/relations components, by aggregate
	 */
	private final Map<String, List<String>> aggregateComponents;
		
	/**
	 * Stores all command handlers by command name
	 */
	private final Map<String, DocumentedHandler> commandHandlers;
	
	/**
	 * Stores all event listeners by event name
	 */
	private final Map<String, List<DocumentedListener>> eventListeners;
	
	/**
	 * Static mapping between string component type names and associated classes
	 */
	private final Map<String, Class<? extends DocumentedDomainNode>> simpleTypes;
	private final Map<String, Class<? extends DocumentedDomainNode>> pluralTypes;
	
	// ========================================================================
	
	public KasperLibrary() {
		
		this.domainNames = Maps.newTreeMap();
		this.domainPrefixes = Maps.newTreeMap();
		
		this.sourceConceptRelations = Maps.newHashMap();
		this.targetConceptRelations = Maps.newHashMap();
		this.aggregateComponents = Maps.newHashMap();
		this.commandHandlers = Maps.newHashMap();
		this.eventListeners = Maps.newHashMap();
		
		this.domainEntities = Maps.newHashMap();
		this.commandEntities = Maps.newHashMap();
		
		this.simpleTypes = Maps.newHashMap();
		this.simpleTypes.put(DocumentedRepository.TYPE_NAME, DocumentedRepository.class);
		this.simpleTypes.put(DocumentedCommand.TYPE_NAME, DocumentedCommand.class);
		this.simpleTypes.put(DocumentedEvent.TYPE_NAME, DocumentedEvent.class);
		this.simpleTypes.put(DocumentedConcept.TYPE_NAME, DocumentedConcept.class);
		this.simpleTypes.put(DocumentedRelation.TYPE_NAME, DocumentedRelation.class);
		this.simpleTypes.put(DocumentedListener.TYPE_NAME, DocumentedListener.class);
		this.simpleTypes.put(DocumentedHandler.TYPE_NAME, DocumentedHandler.class);
		this.simpleTypes.put(DocumentedQueryService.TYPE_NAME, DocumentedQueryService.class);
		
		this.pluralTypes = Maps.newHashMap();
		this.pluralTypes.put(DocumentedRepository.PLURAL_TYPE_NAME, DocumentedRepository.class);
		this.pluralTypes.put(DocumentedCommand.PLURAL_TYPE_NAME, DocumentedCommand.class);
		this.pluralTypes.put(DocumentedEvent.PLURAL_TYPE_NAME, DocumentedEvent.class);
		this.pluralTypes.put(DocumentedConcept.PLURAL_TYPE_NAME, DocumentedConcept.class);
		this.pluralTypes.put(DocumentedRelation.PLURAL_TYPE_NAME, DocumentedRelation.class);
		this.pluralTypes.put(DocumentedListener.PLURAL_TYPE_NAME, DocumentedListener.class);
		this.pluralTypes.put(DocumentedHandler.PLURAL_TYPE_NAME, DocumentedHandler.class);
		this.pluralTypes.put(DocumentedQueryService.PLURAL_TYPE_NAME, DocumentedQueryService.class);
	}
	
	// == DOMAINS =============================================================	
	// ========================================================================
	
	public DocumentedDomain recordDomain(final Class<? extends Domain> domainClazz) {
		final DocumentedDomain documentedDomain = new DocumentedDomain(this, domainClazz);
		
		this.domainNames.put(documentedDomain.getName(), documentedDomain);
		this.domainPrefixes.put(documentedDomain.getPrefix(), documentedDomain);
		
		return documentedDomain;
	}		
	
	// --
	
	public Map<String, DocumentedDomain> getDomains() {
		return this.domainNames;
	}
	
	// --
	
	public Optional<DocumentedDomain> getDomainFromName(final String domainName) {
		checkNotNull(domainName);
		
		if (this.domainNames.containsKey(domainName)) {
			return Optional.of(this.domainNames.get(domainName));
		}
		
		return Optional.absent();
	}
	
	// --
	
	public Optional<DocumentedDomain> getDomainFromPrefix(final String domainPrefix) {
		checkNotNull(domainPrefix);
		
		if (this.domainPrefixes.containsKey(domainPrefix)) {
			return Optional.of(this.domainPrefixes.get(domainPrefix));
		}
		
		return Optional.absent();
	}

	// == REPOSITORIES ========================================================
	// ========================================================================
	
	public DocumentedRepository recordRepository(final Class<? extends IRepository<?>> repositoryClazz) {
		final DocumentedRepository documentedRepository = new DocumentedRepository(this, repositoryClazz);		
		recordElement(documentedRepository.getDomainName(), documentedRepository);
		return documentedRepository;
	}		
	
	// --
	
	public Map<String, DocumentedRepository> getRepositories(final String domainName) {
		return getEntities(domainName, DocumentedRepository.class, false).get();
	}
	
	// == COMMANDS ============================================================
	// ========================================================================
	
	public DocumentedCommand recordCommand(final Class<? extends Command> commandClazz) {
		final DocumentedCommand documentedCommand = new DocumentedCommand(this, commandClazz);		
		
		this.commandEntities.put(documentedCommand.getName(), documentedCommand);
		
		return documentedCommand;
	}		
	
	// --
	
	public Map<String, DocumentedCommand> getCommands(final String domainName) {
		final Map<String, DocumentedHandler> handlers = getHandlers(domainName);
		
		final Map<String, DocumentedCommand> commands = Maps.newHashMap();
		for (final DocumentedHandler handler : handlers.values()) {
			final Optional<DocumentedCommand> command = getCommand(handler.getCommandName());
			if (command.isPresent()) {
				commands.put(command.get().getName(), command.get());
			}
		}
		
		return commands;
	}		
	
	// --
	
	public Optional<DocumentedCommand> getCommand(final String commandName) {
		return Optional.fromNullable(commandEntities.get(commandName));
	}
	
	// == EVENTS ==============================================================
	// ========================================================================
	
	public DocumentedEvent recordEvent(final Class<? extends Event> eventClazz) {
		final DocumentedEvent documentedEvent = new DocumentedEvent(this, eventClazz);		
		recordElement(documentedEvent.getDomainName(), documentedEvent);
		return documentedEvent;
	}		
	
	// --
	
	public Map<String, DocumentedEvent> getEvents(final String domainName) {
		return getEntities(domainName, DocumentedEvent.class, false).get();
	}		
	
	// --
	
	public Optional<DocumentedEvent> getEvent(final String domainName, final String eventName) {
		return Optional.fromNullable(getEntities(domainName, DocumentedEvent.class, false).get().get(eventName));
	}
	
	// == CONCEPTS ============================================================
	// ========================================================================
	
	public DocumentedConcept recordConcept(final Class<? extends Concept> conceptClazz) {
		final DocumentedConcept documentedConcept = new DocumentedConcept(this, conceptClazz);
		recordElement(documentedConcept.getDomainName(), documentedConcept);
		return documentedConcept;
	}		
	
	// --
	
	public Map<String, DocumentedConcept> getConcepts(final String domainName) {
		return getEntities(domainName, DocumentedConcept.class, false).get();
	}	
	
	// --
	
	public Optional<DocumentedConcept> getConcept(final String domainName, final String conceptName) {
		return Optional.fromNullable(getEntities(domainName, DocumentedConcept.class, false).get().get(conceptName));
	}
	
	// --
	
	public void registerAggregateComponent(final String agrName, final String componentName) {
		checkNotNull(agrName);
		checkNotNull(componentName);
		
		final List<String> components;
		if (!this.aggregateComponents.containsKey(agrName)) {
			components = Lists.newArrayList();
			this.aggregateComponents.put(agrName, components);
		} else {
			components = this.aggregateComponents.get(agrName);
		}
		
		components.add(componentName);
	}	
	
	// --
	
	public List<DocumentedConcept> getConceptComponents(final String domainName, final String agrName) {
		return getComponents(domainName, agrName, DocumentedConcept.TYPE_NAME);
	}

	public List<DocumentedConcept> getRelationComponents(final String domainName, final String agrName) {
		return getComponents(domainName, agrName, DocumentedRelation.TYPE_NAME);
	}
	
	@SuppressWarnings("unchecked")
	private <T extends DocumentedEntity> List<T> getComponents(final String domainName, final String agrName, final String entityType) {
		if (this.aggregateComponents.containsKey(agrName)) {
			final List<T> entities = Lists.newArrayList();
			final List<String> entityNames = this.aggregateComponents.get(agrName);
			for (final String name : entityNames) {
				final Optional<T> entity;
				if (entityType.equals(DocumentedConcept.TYPE_NAME)) {
					entity = (Optional<T>) getConcept(domainName, name);
				} else {
					entity = (Optional<T>) getRelation(domainName, name);
				}
				if (entity.isPresent() && !entities.contains(entity)) {
					entities.add(entity.get());
				}
			}
			return entities;
		}
		return Collections.EMPTY_LIST;
	}
	
	// == RELATIONS ===========================================================
	// ========================================================================
	
	public DocumentedRelation recordRelation(final Class<? extends Relation<?,?>> relationClazz) {
		final DocumentedRelation documentedRelation = new DocumentedRelation(this, relationClazz);		

		recordElement(documentedRelation.getDomainName(), documentedRelation);
		
		final String sourceConcept = documentedRelation.getSourceConceptName();
		final String targetConcept = documentedRelation.getTargetConceptName();
		
		// Record relation for source concept ---------------------------------
		final List<DocumentedRelation> sourceRelations;
		if (!sourceConceptRelations.containsKey(sourceConcept)) {
			sourceRelations = Lists.newArrayList();
			sourceConceptRelations.put(sourceConcept, sourceRelations);
		} else {
			sourceRelations = sourceConceptRelations.get(sourceConcept);
		}
		sourceRelations.add(documentedRelation);
		
		// Record relation for target concept ---------------------------------
		final List<DocumentedRelation> targetRelations;
		if (!targetConceptRelations.containsKey(targetConcept)) {
			targetRelations = Lists.newArrayList();
			targetConceptRelations.put(targetConcept, targetRelations);
		} else {
			targetRelations = targetConceptRelations.get(targetConcept);
		}
		targetRelations.add(documentedRelation);
		
		
		return documentedRelation;
	}
	
	// --
	
	public Map<String, DocumentedRelation> getRelations(final String domainName) {
		return getEntities(domainName, DocumentedRelation.class, false).get();
	}			
	
	// --
	
	public List<DocumentedRelation> getSourceConceptRelations(final String conceptName) {
		if (sourceConceptRelations.containsKey(checkNotNull(conceptName))) {
			return Collections.unmodifiableList(sourceConceptRelations.get(conceptName));
		}
		return Collections.emptyList();
	}
	
	// --
	
	public List<DocumentedRelation> getTargetConceptRelations(final String conceptName) {
		if (targetConceptRelations.containsKey(checkNotNull(conceptName))) {
			return Collections.unmodifiableList(targetConceptRelations.get(conceptName));
		}
		return Collections.emptyList();
	}	
	
	// --
	
	public Optional<DocumentedRelation> getRelation(final String domainName, final String relationName) {
		return Optional.fromNullable(getEntities(domainName, DocumentedRelation.class, false).get().get(relationName));
	}
	
	// == LISTENERS ===========================================================
	// ========================================================================
	
	public DocumentedListener recordListener(final Class<? extends EventListener<?>> listenerClazz) {
		final DocumentedListener documentedListener = new DocumentedListener(this, listenerClazz);
		recordElement(documentedListener.getDomainName(), documentedListener);
		return documentedListener;
	}		
	
	// --
	
	public Map<String, DocumentedListener> getListeners(final String domainName) {
		return getEntities(domainName, DocumentedListener.class, false).get();
	}		
	
	// --
	
	public void registerListener(final DocumentedListener listener,final String eventName) {
		checkNotNull(listener);
		checkNotNull(eventName);
		
		final List<DocumentedListener> listeners;
		if (!this.eventListeners.containsKey(eventName)) {
			listeners = Lists.newArrayList();
			this.eventListeners.put(eventName, listeners);
		} else {
			listeners = this.eventListeners.get(eventName);
		}
		
		listeners.add(listener);		
	}
	
	// --
	
	@SuppressWarnings("unchecked")
	public List<DocumentedListener> getListenersForEvent(final String domainName, final String eventName) {
		if (this.eventListeners.containsKey(eventName)) {
			return this.eventListeners.get(eventName);
		}
		return Collections.EMPTY_LIST;
	}
	
	
	// == HANDLERS ============================================================
	// ========================================================================
	
	public DocumentedHandler recordHandler(final Class<? extends CommandHandler<?>> handlerClazz) {
		final DocumentedHandler documentedHandler = new DocumentedHandler(this, handlerClazz);
		recordElement(documentedHandler.getDomainName(), documentedHandler);
		return documentedHandler;
	}		
	
	// --
	
	public Map<String, DocumentedHandler> getHandlers(final String domainName) {
		return getEntities(domainName, DocumentedHandler.class, false).get();
	}	
	
	// --
	
	public void registerHandler(final DocumentedHandler handler, final String commandName){
		checkNotNull(handler);
		checkNotNull(commandName);
		
		this.commandHandlers.put(commandName, handler);
	}
	
	// --
	
	public Optional<DocumentedHandler> getHandlerForCommand(final String commandName) {
		return Optional.fromNullable(this.commandHandlers.get(commandName));
	}

	// == QUERY SERVICES ======================================================
	// ========================================================================
	
	public DocumentedQueryService recordQueryService(final Class<? extends QueryService<?,?>> queryServiceClazz) {
		final DocumentedQueryService documentedQueryService = new DocumentedQueryService(this, queryServiceClazz);		
		recordElement(documentedQueryService.getDomainName(), documentedQueryService);
		return documentedQueryService;
	}		
	
	// --
	
	public Map<String, DocumentedQueryService> getQueryServices(final String domainName) {
		return getEntities(domainName, DocumentedQueryService.class, false).get();
	}		
	
	// --
	
	public Optional<DocumentedQueryService> getQueryService(final String domainName, final String queryServiceName) {
		return Optional.fromNullable(getEntities(domainName, DocumentedQueryService.class, false).get().get(queryServiceName));
	}

	
	// == Common generic methods ==============================================
	// ========================================================================
	
	public <T extends DocumentedNode> Optional<Map<String, T>> getEntities(final String domainName, final String entityPluralType) {
		checkNotNull(domainName);
		checkNotNull(entityPluralType);
		
		@SuppressWarnings("unchecked") // Safe
		final Class<T> entityClass = (Class<T>) this.pluralTypes.get(entityPluralType);
		
		if (null == entityClass) {
			return Optional.absent();
		}
		
		return getEntities(domainName, entityClass);
	}
	
	// --
	
	@SuppressWarnings("unchecked") // Safe
	public <T extends DocumentedNode> Optional<Map<String, T>> getEntities(final String domainName, final Class<T> entityClass) {
		final Optional<Map<String, T>> ret; 
		
		if (entityClass.equals(DocumentedCommand.class)) {
			ret = Optional.of((Map<String, T>) getCommands(domainName));
		} else {
			ret = getEntities(domainName, entityClass, true);
		}
		
		return ret;
	}
	
	// --
	
	@SuppressWarnings("unchecked") // Checked
	public <T extends DocumentedNode> Optional<Map<String, T>> getEntities(final String domainName, final Class<T> entityClass, final boolean returnAbsent) {
		checkNotNull(domainName);
		checkNotNull(entityClass);
		
		if (this.domainEntities.containsKey(entityClass)) {
			final Map<String, ?> entityMap = this.domainEntities.get(entityClass);
			if (entityMap.containsKey(domainName)) {
				final Map<String, T> entities = (Map<String, T>) entityMap.get(domainName);
				return Optional.of(Collections.unmodifiableMap(entities));
			}
		}
		
		if (returnAbsent) {
			return Optional.absent();
		} else {
			final Map<String, T> empty = Collections.emptyMap(); 
			return Optional.of(empty);
		}
	}
	
	// ------------------------------------------------------------------------
	
	public <T extends DocumentedDomainNode> Optional<T> getEntity(final String domainName, final String entityType, final String entityName) {
		checkNotNull(domainName);
		checkNotNull(entityType);
		checkNotNull(entityName);
		
		@SuppressWarnings("unchecked") // Safe
		final Class<T> entityClass = (Class<T>) this.simpleTypes.get(entityType);
		return getEntity(domainName, entityClass, entityName);
	}
	
	// --
	
	public <T extends DocumentedNode> Optional<T> getEntity(final String domainName, final Class<T> entityClass, final String entityName) {
		checkNotNull(domainName);
		checkNotNull(entityClass);
		checkNotNull(entityName);
		
		final Optional<Map<String, T>> entities = getEntities(domainName, entityClass);
		if (!entities.isPresent()) {
			return Optional.absent();
		}
		return Optional.fromNullable(entities.get().get(entityName));
	}	
	
	// --
	
	@SuppressWarnings("unchecked") // Safe
	public <T extends DocumentedDomainNode> Optional<T> getEntity(final String domainName, final String entityName) {
		
		Class<T> entityClass = (Class<T>) this.simpleTypes.get(DocumentedConcept.TYPE_NAME);		
		final Optional<T> concept = getEntity(domainName, entityClass, entityName);
		if (concept.isPresent()) {
			return concept;
		}
		
		entityClass = (Class<T>) this.simpleTypes.get(DocumentedRelation.TYPE_NAME);		
		final Optional<T> relation = getEntity(domainName, entityClass, entityName);
		if (relation.isPresent()) {
			return relation;
		}
		
		return Optional.absent();
	}
	
	// == TOOLS ===============================================================
	// ========================================================================
	
	@SuppressWarnings("unchecked")
	private <T extends DocumentedDomainNode> void recordElement(final String domain, final T node) {
		final Map<String, T> nodes;
		
		final Map<String, Map<String, T>> list;
		if (this.domainEntities.containsKey(node.getClass())) {
			list = (Map<String, Map<String, T>>) this.domainEntities.get(node.getClass());
		} else {
			list = Maps.newHashMap();
			this.domainEntities.put(node.getClass(), list);
		}
		
		if (list.containsKey(domain)) {
			nodes = list.get(domain);
		} else {
			nodes = Maps.newHashMap();
			list.put(domain, nodes);
		}
		
		nodes.put(node.getName(), node);
	}
	
	// --
	
	public <T extends DocumentedDomainNode> Map<String, DocumentedNode> simpleNodesFrom(final Map<String, T> nodes) {
		checkNotNull(nodes);
		
		final TreeMap<String, DocumentedNode> simpleNodes = Maps.newTreeMap();
		
		for (final DocumentedNode node : nodes.values()) {			
			final DocumentedNode newNode = getSimpleNodeFrom(node);
			
			newNode.setUrl(node.getURL());
			simpleNodes.put(node.getName(), newNode);
		}
		
		return simpleNodes;
	}
	
	// --
	
	public <T extends DocumentedDomainNode> Map<String, DocumentedNode> simpleNodesFrom(final List<T> nodes) {
		checkNotNull(nodes);
		
		final Map<String, DocumentedNode> simpleNodes = Maps.newTreeMap();
		
		for (final DocumentedNode node : nodes) {			
			final DocumentedNode newNode = getSimpleNodeFrom(node);
			
			newNode.setUrl(node.getURL());
			simpleNodes.put(node.getName(), newNode);
		}
		
		return simpleNodes;
	}	
	
	// --
	
	public <T extends DocumentedNode> DocumentedNode getSimpleNodeFrom(T node) {
		if (DocumentedRelation.class.isAssignableFrom(node.getClass())) {
			return new DocumentedSimpleRelation((DocumentedRelation) node);
		}
		return new DocumentedNode(node);
	}

    // ------------------------------------------------------------------------

    public void setResolvers(final Resolvers resolvers) {
        this.resolvers = checkNotNull(resolvers);
    }

    public Resolvers getResolvers() {
        return this.resolvers;
    }

}
