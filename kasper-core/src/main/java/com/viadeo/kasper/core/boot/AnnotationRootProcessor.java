// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Responsible for booting all Kasper annotation processors
 * <p/>
 * 3-step process:
 * <p/>
 * 1-BOOT    - record all available annotation processors
 * 2-SCAN    - scan all classes matching recorded processors configuration
 * 3-PROCESS - delegate processing of scanned classes to their respective processor
 * <p/>
 * This root processor can use a components instance manager if supplied
 * <p/>
 * User can filter scanned packages (default: all packages of jvm classpath).
 * <p/>
 * TODO: allow user to specify classpath as a list of URLs or at least as Reflections helpers
 */
public class AnnotationRootProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationRootProcessor.class);

    /**
     * Allow user to explicitly specify processor instances to use before boot
     */
    private transient List<AnnotationProcessor<?, ?>> userProcessors;

    /**
     * User provided scan packages names
     */
    private transient List<String> scanPrefixes;

    /**
     * If set, will not add its own package to scan prefixes
     */
    private transient boolean doNotScanDefaultPrefix = false;

    /**
     * Scanned processors
     */
    private transient Map<Class<?>, List<AnnotationProcessor<?, ?>>> processors;
    private transient Map<AnnotationProcessor<?, ?>, Class<? extends Annotation>> processorsInterface;

    /**
     * Class-path reflection resolver
     */
    private Reflections reflections;

    /**
     * Instances manager (optional)
     */
    private ComponentsInstanceManager instancesManager;

    // ------------------------------------------------------------------------

    public AnnotationRootProcessor() {
        this(new String[0]);
    }

    public AnnotationRootProcessor(final String[] prefixes) {
        if (Preconditions.checkNotNull(prefixes).length > 0) {
            this.scanPrefixes = new ArrayList<>();
            this.scanPrefixes.addAll(Arrays.asList(prefixes));
        }
    }

    //-------------------------------------------------------------------------

    /**
     * Bootstrap the Kasper annotations processing phase
     *
     * @see AnnotationProcessor
     */
    public void boot() {
        LOGGER.info("Boot Kasper annotation processing");

        final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

        // Filter prefixes if requested ---------------------------------------
        final Set<String> prefixes = new HashSet<>();

        if (!this.doNotScanDefaultPrefix) {
            prefixes.add("com.viadeo.kasper");
        }

        if ((null != this.scanPrefixes) && (this.scanPrefixes.size() > 0)) {
            prefixes.addAll(this.scanPrefixes);
        }

        final FilterBuilder filter = new FilterBuilder();
        for (final String prefix : prefixes) {
            filter.include(FilterBuilder.prefix(prefix));
            configurationBuilder.addUrls(ClasspathHelper.forPackage(prefix));
        }

        configurationBuilder.filterInputsBy(filter);

        // Scan for annotations and types hierarchy ---------------------------
        configurationBuilder.setScanners(new TypeAnnotationsScanner(), new SubTypesScanner());

        reflections = new Reflections(configurationBuilder);

        // Scan processors
        scan();
    }

    // ------------------------------------------------------------------------

    /**
     * Identify available kasper processors and process
     */
    private void scan() {
        LOGGER.info("Scan Kasper annotation processors");

        processors = Maps.newHashMap();
        processorsInterface = Maps.newHashMap();

        @SuppressWarnings("rawtypes") // Deterministic
        final Set<Class<? extends AnnotationProcessor>> classes =
                reflections.getSubTypesOf(AnnotationProcessor.class);

        // Add user-defined processor classes ---------------------------------
        if (null != this.userProcessors) {
            for (final AnnotationProcessor<?, ?> userProc : this.userProcessors) {
                if (!classes.contains(userProc.getClass())) {
                    classes.add(userProc.getClass());
                }
            }
        }

        if ((null == classes) || (0 == classes.size())) {
            throw new KasperException("Unable to find any Kasper annotation processor class");
        }

        // Instanciate and record each found processor ------------------------
        for (@SuppressWarnings("rawtypes") final Class<? extends AnnotationProcessor> clazz : classes) {

            if (Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }

            try {

                @SuppressWarnings("unchecked") // Safe
                final Optional<Class<? extends Annotation>> annoClass =
                        (Optional<Class<? extends Annotation>>) ReflectionGenericsResolver.getParameterTypeFromClass(clazz,
                                AnnotationProcessor.class, AnnotationProcessor.ANNOTYPE_PARAMETER_POSITION);

                @SuppressWarnings("unchecked") // Safe
                final Optional<Class<?>> interfaceClass =
                        (Optional<Class<?>>) ReflectionGenericsResolver.getParameterTypeFromClass(clazz,
                                AnnotationProcessor.class, AnnotationProcessor.INTERFACE_PARAMETER_POSITION);

                if (annoClass.isPresent() && interfaceClass.isPresent()) {
                    Object objInstance = null;

                    if (!interfaceClass.get().isInterface()) {
                        throw new KasperException(interfaceClass.get().getSimpleName()
                                + " is not an interface in processor "
                                + clazz.getSimpleName());
                    }

                    // 1- User supplied processor
                    if (null != this.userProcessors) {
                        for (final AnnotationProcessor<?, ?> processor : this.userProcessors) {
                            if (clazz.isAssignableFrom(processor.getClass())) {
                                objInstance = processor;
                            }
                        }
                    }

                    // 2- Spring injected processor
                    if (null == objInstance) {
                        final Optional<AnnotationProcessor> optInstance = getComponentsInstanceManager().getInstanceFromClass(clazz);
                        if (optInstance.isPresent()) {
                            objInstance = optInstance.get();
                        }
                    }

                    // 3- New processor instance
                    if (null == objInstance) {
                        objInstance = clazz.newInstance();
                        getComponentsInstanceManager().recordInstance(clazz, objInstance);
                    }

                    // Forward its components instance manager to the processor if suitable
                    if (SingletonAnnotationProcessor.class.isAssignableFrom(objInstance.getClass())) {
                        final SingletonAnnotationProcessor singletonProcessor = (SingletonAnnotationProcessor) objInstance;
                        if (!singletonProcessor.hasComponentsInstanceManager()) {
                            singletonProcessor.setComponentsInstanceManager(getComponentsInstanceManager());
                        }
                    }

                    LOGGER.debug("Registered Kasper processor : " + clazz.getName());

                    if (!processors.containsKey(interfaceClass.get())) {
                        processors.put(interfaceClass.get(), new ArrayList<AnnotationProcessor<?, ?>>());
                    }

                    if (!processors.get(interfaceClass.get()).contains(objInstance)) {
                        processors.get(interfaceClass.get()).add((AnnotationProcessor<?, ?>) objInstance);
                        processorsInterface.put((AnnotationProcessor<?, ?>) objInstance, annoClass.get());
                    }

                } else {
                    throw new KasperException("Unable to find parameter type for type " + clazz.getSimpleName());
                }

            } catch (final InstantiationException e) {
                throw new KasperException("Error when instantiating Kasper annotation processor", e);
            } catch (final IllegalAccessException e) {
                throw new KasperException("Error when accessing Kasper annotation processor", e);
            }
        }

        // Delegate to each recorded processor
        process();
    }

    //-------------------------------------------------------------------------

    /**
     * Apply processors on all matching classes
     */
    protected void process() {
        LOGGER.info("Delegate to Kasper annotation processors");

        for (final Class<?> tplClass : processors.keySet()) {
            for (final AnnotationProcessor<?, ?> processor : processors.get(tplClass)) {
                final Class<? extends Annotation> annotation = processorsInterface.get(processor);

                LOGGER.info(String.format("Delegate for %s to %s", tplClass.getSimpleName(), processor.getClass().getSimpleName()));

                final Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(annotation);
                final Set<Class<?>> conformClasses = (Set<Class<?>>) reflections.getSubTypesOf(tplClass);
                conformClasses.addAll(annotatedClasses);

                // For all suitable classes
                for (final Class<?> clazz : conformClasses) {

                    // Filter out interfaces and abstract classes
                    if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {

                        // Filter out user-excluded classes
                        if (null == clazz.getAnnotation(XKasperUnregistered.class)) {

                            // Check annotation presence if pertinent
                            if ((null != clazz.getAnnotation(annotation)) || !processor.isAnnotationMandatory()) {

                                try {

                                    // PROCESSOR DELEGATION
                                    processor.process(clazz);


                                } catch (Exception e) {
                                    LOGGER.warn("Unexpected error during processor delegation, <class=" + clazz.getName() + ">: ", e);
                                }

                            } else {
                                throw new KasperException(
                                            String.format("%s must have an annotation : %s",
                                                    clazz.getName(), annotation.getSimpleName()));
                            }

                        } else {
                            LOGGER.debug(String.format("Ignore unregistered class %s", clazz.getName()));
                        }

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
    public void registerProcessor(final AnnotationProcessor<?, ?> processor) {
        Preconditions.checkNotNull(processor);

        if (null == this.userProcessors) {
            this.userProcessors = new ArrayList<>();
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
            this.scanPrefixes = new ArrayList<>();
        }
        this.scanPrefixes = Arrays.asList(scanPrefixes.clone());
    }

    // -----

    /**
     * @param scanPrefix add a new package name to scan
     */
    public void addScanPrefix(final String scanPrefix) {
        if (null == this.scanPrefixes) {
            this.scanPrefixes = new ArrayList<>();
        }
        this.scanPrefixes.add(scanPrefix);
    }

    // -----

    /**
     * Exclude own processors, can be useful for system isolation or testing purposes
     *
     * @param doNotScanDefaultPrefix if true we will not add our package name to the scanned ones
     */
    public void setDoNotScanDefaultPrefix(final boolean doNotScanDefaultPrefix) {
        this.doNotScanDefaultPrefix = doNotScanDefaultPrefix;
    }

    //-------------------------------------------------------------------------

    public void setComponentsInstanceManager(final ComponentsInstanceManager instancesManager) {
        this.instancesManager = instancesManager;
    }

    /**
     * Return the current instances manager, revert (create) to a simple map-backed one
     * if no one has been provided
     *
     * @return the components instance manager to use
     */
    public ComponentsInstanceManager getComponentsInstanceManager() {
        if (null == this.instancesManager) {
            LOGGER.info("No Components instance manager has been provided, revert back to simple one (default)");
            this.instancesManager = new SimpleComponentsInstanceManager();
        }
        return this.instancesManager;
    }

}
