
Defining a domain bundle
========================

A domain bundle can be considered as a domain box containing all components to be wired with the platform like :
command handlers, query handlers, event listeners, repositories.

The framework propose several implementations of `DomainBundle` in order to facilitate its usage.

Warning: According the chosen implementation, components of a bundle will be initialized after they have been configured by the wiring mechanisms of the platform.


..  _Defining_a_domain_bundle_from_scratch:

Defining a domain bundle from scratch
-----------------------

Using `com.viadeo.kasper.client.platform.domain.DefaultDomainBundle` as a simple bean.

.. code-block:: java
    :linenos:

    Domain domain = new SampleDomain()

    DomainBundle domainBundle1 = new DefaultDomainBundle(
        Lists.<CommandHandler>newArrayList(),
        Lists.<QueryHandler>newArrayList(),
        Lists.<Repository>newArrayList(),
        Lists.<EventListener>newArrayList(),
        Lists.<QueryInterceptorFactory>newArrayList(),
        Lists.<CommandInterceptorFactory>newArrayList(),
        Lists.<EventInterceptorFactory>newArrayList(),
        domain,
        "sample domain name"
    );

A builder exists in order to facilitate its instantiation.

.. code-block:: java
    :linenos:

    DomainBundle domainBundle1Bis = new DefaultDomainBundle.Builder(domain)
        .with(new SampleCommandHandler())
        .with(new SampleQueryHandler())
        .with(new SampleEventListener())
        .build()


..  _Defining_a_domain_bundle_existing_spring_configuration:

Defining a domain bundle from existing Spring configuration
-----------------------

Using `com.viadeo.kasper.client.platform.domain.SpringDomainBundle` is really simple and useful when your domain uses Java spring configuration.
This implementation will create an application context specific to your domain. Thereby the domain is completely isolated of all other components and bundles.

.. code-block:: java
    :linenos:

    Domain domain = new SampleDomain()

    DomainBundle domainBundle2= new SpringDomainBundle(
        new SampleDomain(),
        Lists.<Class>newArrayList(
            SampleDomainQueryConfiguration.class,
            SampleDomainCommandConfiguration.class
        )
    );


..  _Defining_a_domain_bundle_in_discovering_class:

Defining a domain bundle in discovering class
-----------------------

Using `com.viadeo.kasper.client.platform.domain.DiscoveryDomainBundle` is really simple. This implementation will scan from the specified package each
candidates component in order to build the domain.

.. code-block:: java
    :linenos:

    DomainBundle domainBundle3= new DiscoveryDomainBundle(
              "com.viadeo.platform.sample"
    );

Warning: If no domain or several domains has been discovered then an exception will be throw
