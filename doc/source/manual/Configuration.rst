Kasper configuration
====================

Kasper is made to not be dependant of any configuration framework, however it provides all required stuff to be ran
within a `Spring <http://static.springsource.org/spring/docs/current/spring-framework-reference/html/>`_ context.

Booting Kasper platform
-----------------------

First add a dependency in your project to **com.viadeo.kasper:kasper-platform**.

In order to boot a Kasper platform you will need to initialize the following dependency tree (with default implementation
specified in parenthesis):

* Platform (*KasperPlatform*)
    * CommandGateway (*CommandGateway interface*)
        * CommandBus (*KasperCommandBus*)
    * QueryGateway (*DefaultQueryGateway*)
        * QueryServicesLocator (*DefaultQueryServicesLocator*)
    * EventBus (*KasperHybridEventBus*)
    * RootProcessor (*AnnotationRootProcessor*)
        * ComponentsInstanceManager (*SimpleComponentsInstanceManager*)
        * CommandHandlersProcessor
            * CommandBus
            * DomainLocator (*DefaultDomainLocator*)
        * RepositoriesProcessor
            * EventBus
            * DomainLocator
        * EventListenersProcessor
            * EventBus
        * QueryServicesProcessor
            * QueryServicesLocator
        * DomainsProcessor
            * DomainLocator
        * ServiceFiltersProcessor
            * QueryServicesLocator

Boot Kasper platform without Spring
...................................

In order to boot the platform without Spring, you can use the **PlatformFactory** helper :

.. code-block:: java
    :linenos:

    /* Get a Kasper platform, with all default implementations */
    final PlatformFactory pf = new PlatformFactory();
    final Platform kasper = pf.getPlatform();
    kasper.boot();

    /* Get a Kasper platform, with some overriden components implementations */
    /* - Use your own configuration class which implements PlatformConfiguration */
    /* - Extend DefaultPlatformConfiguration for a default implementation */
    final PlatformConfiguration pfConf = new MyPlatformConfiguration();
    final PlatformFactory pf = new PlatformFactory(platformConfiguration);
    final Platform kasper = pf.getPlatform();
    kasper.boot();

    /* You can manually add some packages to be scanned */
    final PlatformFactory pf = new PlatformFactory();
    final Platform kasper = pf.getPlatform();
    kasper.getRootProcessor().addScanPrefix("com.company.project");
    kasper.boot();

    /* Or provides Kasper with some own created instances */
    final PlatformFactory pf = new PlatformFactory();
    final Platform kasper = pf.getPlatform();

    final ComponentsInstanceManager sman = kasper.getRootProcessor().getComponentsInstanceManager();
    sman.register(MyRepository.class, MyRepositoryFactory.build());

    kasper.boot();

    /* Platform can be booted during the creation process */
    final PlatformFactory pf = new PlatformFactory();
    final Platform kasper = pf.getPlatform(true);


Boot Kasper platform with Spring
................................

Add a **DefaultPlatformSpringConfiguration** bean as a
`Java configuration bean <http://static.springsource.org/spring/docs/current/spring-framework-reference/html/beans.html#beans-java>`_
in your Spring context, overriding it if necessary with your own bean retrieval methods.

**In your XML configuration** :


.. code-block:: xml
    :linenos:

    <beans>
      ...
      <bean id="kasperConf" class="com.viadeo.kasper.platform.configuration.DefaultPlatformSpringConfiguration"/>
      ...
    </beans>

**As an import of another Java configuration** :

.. code-block:: java
    :linenos:

    @Configuration
    @Import({ DefaultPlatformSpringConfiguration.class})
    public class MyApplicationSpringRuntime {
        ...
    }

**Override it in order to specify your own implementation of some components** :

.. code-block:: java
    :linenos:

    @Configuration
    public class KasperPlatformSpringConfiguration extends DefaultPlatformSpringConfiguration {

        @Bean
        @Override
        public ComponentsInstanceManager getComponentsInstanceManager() {
            final SpringComponentsInstanceManager sman = super.getComponentsInstanceManager();
            /* No bean will be created by the instance manager, they should exist in the context */
            sman.setBeansMustExists(true);
            return sman;
        }

    }
