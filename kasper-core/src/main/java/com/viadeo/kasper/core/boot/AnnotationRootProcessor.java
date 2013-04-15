// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.viadeo.kasper.exception.KasperRuntimeException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

/**
 * Responsible for booting all Kasper annotation processors
 *
 * 3-step process:
 *
 * 1-BOOT    - record all available annotation processors
 * 2-SCAN    - scan all classes matching recorded processors configuration
 * 3-PROCESS - delegate processing of scanned classes to their respective processor
 *
 * This root processor can use Spring configured processors instance if executed inside Spring context.
 *
 * User can specify which processor instances to use, they will also override Spring ones.
 * User can filter scanned packages (default: all packages of jvm classpath).
 *
 * TODO: allow user to specify classpath as a list of URLs or at least as Reflections helpers
 */
public class AnnotationRootProcessor implements ApplicationContextAware {

	private static Logger LOGGER = LoggerFactory.getLogger(AnnotationRootProcessor.class);

	/** Only used if Spring context available in order to reuse injected processor instances */
	private transient ApplicationContext context;

	/** Allow user to explicitly specify processor instances to use before boot */
	private transient List<IAnnotationProcessor<?, ?>> userProcessors;

	/** User provided scan packages names */
	private transient List<String> scanPrefixes;

	/** If set, will not add its own package to scan prefixes */
	private transient boolean doNotScanDefaultPrefix = false;

	/** Scanned processors */
	private transient Map<Class<? extends Annotation>, List<IAnnotationProcessor<?, ?>>> processors;
	private transient Map<IAnnotationProcessor<?, ?>, Class<?>> processorsInterface;

	/** Class-path reflection resolver */
	private Reflections reflections;

	// ------------------------------------------------------------------------

	public AnnotationRootProcessor() {
		this(new String[0]);
	}

	public AnnotationRootProcessor(final String[] prefixes) {
		if (Preconditions.checkNotNull(prefixes).length > 0) {
			this.scanPrefixes = new ArrayList<String>();
			this.scanPrefixes.addAll(Arrays.asList(prefixes));
		}
	}

	//-------------------------------------------------------------------------

	/**
	 * Bootstrap the Kasper annotations processing phase
	 *
	 * @see IAnnotationProcessor
	 */
	public void boot() {
		LOGGER.info("Boot Kasper annotation processing");

		final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

		// Filter prefixes if requested ---------------------------------------
		final Set<String> prefixes = new HashSet<String>();

		if ((null != this.scanPrefixes) && (this.scanPrefixes.size() > 0)) {
			prefixes.addAll(this.scanPrefixes);

			if (!this.doNotScanDefaultPrefix) {
				final String currentPackage = this.getClass().getPackage().getName();
				prefixes.add(currentPackage);
			}

			final FilterBuilder filter = new FilterBuilder();
			for (final String prefix : prefixes) {
				filter.include(FilterBuilder.prefix(prefix));
				configurationBuilder.addUrls(ClasspathHelper.forPackage(prefix));
			}

			configurationBuilder.filterInputsBy(filter);
		} else {
			configurationBuilder.setUrls(ClasspathHelper.forJavaClassPath());
		}

		// Scan for annotations and types hierarchy ---------------------------
		configurationBuilder.setScanners(new TypeAnnotationsScanner(), new SubTypesScanner());

		reflections = new Reflections(configurationBuilder);

		// Scan processors
		scan();
	}

	// ------------------------------------------------------------------------

	/** Identify available kasper processors and process */
	private void scan() {
		LOGGER.info("Scan Kasper annotation processors");

		processors = Maps.newHashMap();
		processorsInterface = Maps.newHashMap();

		@SuppressWarnings("rawtypes") // Deterministic
		final Set<Class<? extends IAnnotationProcessor>> classes =
				reflections.getSubTypesOf(IAnnotationProcessor.class);

		// Add user-defined processor classes ---------------------------------
		if (null != this.userProcessors) {
			for (final IAnnotationProcessor<?, ?> userProc : this.userProcessors) {
				if (!classes.contains(userProc.getClass())) {
					classes.add(userProc.getClass());
				}
			}
		}

		if ((null == classes) || (0 == classes.size())) {
			throw new KasperRuntimeException("Unable to find any Kasper annotation processor class");
		}

		// Instanciate and record each found processor ------------------------
		for (@SuppressWarnings("rawtypes") final Class<? extends IAnnotationProcessor> clazz : classes) {

			if (Modifier.isAbstract(clazz.getModifiers())) {
				continue;
			}

			try {

				@SuppressWarnings("unchecked") // Safe
				final Optional<Class<? extends Annotation>> annoClass =
						(Optional<Class<? extends Annotation>>) ReflectionGenericsResolver.getParameterTypeFromClass(clazz,
								IAnnotationProcessor.class, IAnnotationProcessor.ANNOTYPE_PARAMETER_POSITION);

				@SuppressWarnings("unchecked") // Safe
				final Optional<Class<?>> interfaceClass =
						(Optional<Class<?>>) ReflectionGenericsResolver.getParameterTypeFromClass(clazz,
								IAnnotationProcessor.class, IAnnotationProcessor.INTERFACE_PARAMETER_POSITION);

				if (annoClass.isPresent() && interfaceClass.isPresent()) {
					Object objInstance = null;

					if (!interfaceClass.get().isInterface()) {
						throw new KasperRuntimeException(interfaceClass.get().getSimpleName()
								+ " is not an interface in processor "
								+ clazz.getSimpleName());
					}

					// 1- User supplied processor
					if (null != this.userProcessors) {
						for (final IAnnotationProcessor<?, ?> processor : this.userProcessors) {
							if (clazz.isAssignableFrom(processor.getClass())) {
								objInstance = processor;
							}
						}
					}

					// 2- Spring injected processor
					if ((null == objInstance) && (null != this.context)) {
						try {
							objInstance = context.getBean(clazz);
						} catch (final NoSuchBeanDefinitionException e) {
							// Ignore
						}
					}

					// 3- New processor instance
					if (null == objInstance) {
						objInstance = clazz.newInstance();
						if (null != this.context) {
							final ConfigurableBeanFactory cfb = (ConfigurableBeanFactory) this.context.getAutowireCapableBeanFactory();
							objInstance = ((AutowireCapableBeanFactory) cfb).createBean(clazz);
							cfb.registerSingleton(clazz.getSimpleName(), objInstance);
						}
					}

					LOGGER.debug("Registered Kasper processor : " + clazz.getName());

					if (!processors.containsKey(annoClass.get())) {
						processors.put(annoClass.get(), new ArrayList<IAnnotationProcessor<?, ?>>());
					}

					if (!processors.get(annoClass.get()).contains(objInstance)) {
						processors.get(annoClass.get()).add((IAnnotationProcessor<?, ?>) objInstance);
						processorsInterface.put((IAnnotationProcessor<?, ?>) objInstance, interfaceClass.get());
					}

				} else {
					throw new KasperRuntimeException("Unable to find parameter type for type " + clazz.getSimpleName());
				}

			} catch (final InstantiationException e) {
				throw new KasperRuntimeException("Error when instantiating Kasper annotation processor", e);
			} catch (final IllegalAccessException e) {
				throw new KasperRuntimeException("Error when accessing Kasper annotation processor", e);
			}
		}

		// Delegate to each recorded processor
		process();
	}

	//-------------------------------------------------------------------------

	/** Apply processors on all matching classes */
	protected void process() {
		LOGGER.info("Delegate to Kasper annotation processors");

		for (final Class<? extends Annotation> annotation : processors.keySet()) {
			for (final IAnnotationProcessor<?, ?> processor : processors.get(annotation)) {
				final Class<?> tplClass = processorsInterface.get(processor);

				LOGGER.info(String.format("Delegate for %s to %s", tplClass.getSimpleName(), processor.getClass().getSimpleName()));

				final Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(annotation);

				for (final Class<?> clazz : annotated) {
					if (tplClass.isAssignableFrom(clazz)) {

						// PROCESSOR DELEGATION
						try{
							processor.process(clazz);
						}catch (Exception e){
							LOGGER.warn("Unexpected error during processor delegation, <class="+clazz.getName()+">: "+e.getCause());
						}

					} else {
						throw new KasperRuntimeException(
								String.format("%s must extends/implements %s", clazz.getName(), tplClass.getName()));
					}
				}
			}
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * User processor instances
	 * Overrides Spring injected instances if any
	 *
	 * @param processor the processor to register
	 */
	public void registerProcessor(final IAnnotationProcessor<?, ?> processor) {
		Preconditions.checkNotNull(processor);

		if (null == this.userProcessors) {
			this.userProcessors = new ArrayList<IAnnotationProcessor<?, ?>>();
		}

		this.userProcessors.add(processor);
	}

	//-------------------------------------------------------------------------

	/**
	 * User specified scan prefixes
	 *
	 * @param scanPrefixes list of packages name to filter (exclude others)
	 */
	public void setScanPrefixes(final String[] scanPrefixes) {
		Preconditions.checkNotNull(scanPrefixes);

		if (null == this.scanPrefixes) {
			this.scanPrefixes = new ArrayList<String>();
		}
		this.scanPrefixes = Arrays.asList(scanPrefixes.clone());
	}

	// -----

	/** @param scanPrefix add a new package name to scan */
	public void addScanPrefix(final String scanPrefix) {
		if (null == this.scanPrefixes) {
			this.scanPrefixes = new ArrayList<String>();
		}
		this.scanPrefixes.add(scanPrefix);
	}

	// -----

	/**
	 * Exclude own processors, can be useful for system isolation or testing purposes
	 *
	 * @param doNotScanDefaultPrefix if true we will not our package name to the scanned ones
	 */
	public void setDoNotScanDefaultPrefix(final boolean doNotScanDefaultPrefix) {
		this.doNotScanDefaultPrefix = doNotScanDefaultPrefix;
	}

	//-------------------------------------------------------------------------

	/**
	 * Set Spring context if available
	 * Not mandatory
	 *
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(final ApplicationContext context)
			throws BeansException {
		this.context = context;
	}

}
